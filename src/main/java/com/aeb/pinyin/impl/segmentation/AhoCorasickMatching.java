package com.aeb.pinyin.impl.segmentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.aeb.pinyin.api.SegmentationOptions;
import com.aeb.pinyin.api.SurnameMode;
import com.aeb.pinyin.impl.utils.UnicodeUtils;

/**
 * Aho Corasick (AC) implementation to find all words in dictionaries in a given
 * text using a trie tree with fail arcs.<br>
 * See e.g. https://cp-algorithms.com/string/aho_corasick.html
 */
public class AhoCorasickMatching {
	
	public AhoCorasickMatching(List<Pattern> patterns) {
		addPatterns(patterns);
	}
	
	protected TrieNode root = new TrieNode(null, "", "");
	
	/**
	 * Keeping track of possible suffix code points is useful for optimization 
	 * of the {@link MaxProbabilitySegmentator}.  See comment there for details.
	 */
	protected Set<String> possibleSuffixCodePoints = new HashSet<>();
	
	protected int numNodes = 1;
	protected int properFailNodes = 0;
	protected int sumFailNodePrefixLength = 0;
	
	private void addPatterns(List<Pattern> patterns) {
		Map<Integer, List<TrieNode>> nodesToFillFailPointer = new TreeMap<>();
		for (Pattern p : patterns) {
			addPattern(p, nodesToFillFailPointer);
		}
		fillFailPointer(nodesToFillFailPointer);
	}
	
	private void addPattern(Pattern pattern, Map<Integer, List<TrieNode>> nodesToFillFailPointer) {
		String[] zhChars = UnicodeUtils.splitString(pattern.zh);
		for (int i = 1; i < zhChars.length; i++) {
			possibleSuffixCodePoints.add(zhChars[i]);
		}
		TrieNode cur = root;
		for (int i = 0; i < zhChars.length; i++) {
			String c = zhChars[i];
			TrieNode previousCur = cur;
			int finali = i;
			cur = cur.children.computeIfAbsent(
					c,
					k -> {
						TrieNode newNode = new TrieNode(previousCur, previousCur.prefix + previousCur.key, c);
						numNodes++;
						nodesToFillFailPointer.computeIfAbsent(finali, l -> new ArrayList<>()).add(newNode);
						return newNode;
					});
		}
		cur.insertPatternByPriority(pattern);
	}
	
	private void fillFailPointer(Map<Integer, List<TrieNode>> nodesToFillFailPointer) {
		// failNodeQueue is a TreeSet, hence ordered by the key, which is the length of
		// the patterns
		for (Entry<Integer, List<TrieNode>> entry : nodesToFillFailPointer.entrySet()) {
			for (TrieNode node : entry.getValue()) {
				fillFailPointer(node);
			}
		}
	}
	
	private void fillFailPointer(TrieNode node) {
		TrieNode failNode = node.parent == null ? null : node.parent.fail;
		String key = node.key;
		while (failNode != null && !failNode.children.containsKey(key)) {
			failNode = failNode.fail;
		}
		if (failNode == null) {
			node.fail = this.root;
		} else {
			node.fail = failNode.children.get(key);
			properFailNodes++;
			sumFailNodePrefixLength += node.fail.prefix.codePointCount(0, node.fail.prefix.length());
		}
	}
	
	/**
	 * Search the text and return the most probable segmentation as list of matched patterns.
	 * @param text The text to segmentize
	 * @param options Segmentation options
	 * @return The segmentation as list of matched patterns
	 */
	public List<MatchedPattern> segmentize(String text, SegmentationOptions options) {
		//Currently only the MaxProbabilitySegmentator is implemented
		MaxProbabilitySegmentator segmentator = new MaxProbabilitySegmentator(text);
		if (options.renormProbabilitiesIfPossible) {
			segmentator.setPossibleSuffixCodePoints(this.possibleSuffixCodePoints);
		}
		match(text, options, segmentator::callbackForPattern);
		return segmentator.getSegmentationResult();
	}
	
	/**
	 * Match the text and return all matched patterns.
	 * @param text The text to match
	 * @param options Segmentation options
	 * @return A list of all matched patterns
	 */
	public List<MatchedPattern> match(String text, SegmentationOptions options) {
		List<MatchedPattern> result = new ArrayList<>();
		match(text, options, result::add);
		return result;
	}
	
	/**
	 * Match the text and call onMatch for each matched pattern.
	 * @param text The text to match
	 * @param options Segmentation options
	 * @param onMatch Callback for each matched pattern
	 */
	public void match(String text, SegmentationOptions options, Consumer<MatchedPattern> onMatch) {
		TrieNode cur = root;
		String[] zhChars = UnicodeUtils.splitString(text);
		
		for (int i = 0; i < zhChars.length; i++) {
			String c = zhChars[i];
			//debug(":::Handle char " + i + ": " + c);
			//debug(":::Current node before handling: ", cur);
			int finali = i;
			
			while (cur != null && !cur.children.containsKey(c)) {
				cur = cur.fail;
				//debug(":::Gone to fail node in init: ", cur);
			}
			
			if (cur == null) {
				cur = this.root;
				//debug(":::Gone to root");
			} else {
				cur = cur.children.get(c);
				//debug(":::Gone to child ", cur);
				Pattern pattern = findFirstMatchingPattern(cur.patterns, finali, options.surnameMode);
				if (pattern != null) {
					MatchedPattern newPattern = new MatchedPattern(pattern, i - pattern.length + 1);
					onMatch.accept(newPattern);
					//debugAddPattern("from cur", cur, newPattern);
				} else {
					//debug(":::No pattern in ", cur);
				}
			}
			
			TrieNode failNode = cur.fail;
			while (failNode != null) {
				//debug(":::Gone to fail node in posthandling: ", failNode);
				Pattern pattern = findFirstMatchingPattern(failNode.patterns, finali, options.surnameMode);
				if (pattern != null) {
					MatchedPattern newPattern = new MatchedPattern(pattern, i - pattern.length + 1);
					onMatch.accept(newPattern);
					//debugAddPattern(" from fail", failNode, newPattern);
				} else {
					//debug(":::No pattern in postfix handling in failnode ", failNode);
				}
				failNode = failNode.fail;
			}
			
		}
	}
	
	//	private void debug(String message, TrieNode n) {
	//		if (n == null) {
	//			debug(message + ": null");
	//		} else {
	//			debug(message + ": " + n.prefix + "/" + n.key);
	//		}
	//	}
	//	
	//	private void debugAddPattern(String message, TrieNode n, MatchPattern pattern) {
	//		debug(
	//				":::Add pattern " + message + " @" + pattern.index + " (" + pattern.pattern.zh + ") from node "
	//						+ n.prefix + "/" + n.key);
	//	}
	//	
	//	private void debug(String message) {
	//		System.out.println(message);
	//	}
	
	/**
	 * Find the first {@link Pattern} in the patternsToSearchIn, using the
	 * surnameMode.
	 */
	protected Pattern findFirstMatchingPattern(
			List<Pattern> patternsToSearchIn,
			int currentIndexInZhText,
			SurnameMode surnameMode) {
		return patternsToSearchIn.stream()
				.filter(item -> {
					return switch (surnameMode) {
						case SurnameMode.ON -> true;
						case SurnameMode.OFF -> item.priority != Priority.Surname;
						case SurnameMode.HEAD -> {
							yield item.priority != Priority.Surname ||
									item.length - 1 - currentIndexInZhText == 0;
						}
					};
				})
				.findFirst()
				.orElse(null);
	}
	
}
