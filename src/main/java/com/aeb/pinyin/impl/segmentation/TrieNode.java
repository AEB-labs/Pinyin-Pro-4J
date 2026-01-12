package com.aeb.pinyin.impl.segmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aeb.pinyin.api.SegmentationOptions;

/**
 * Trie node for the Aho Corasick algorithm, i.e. a prefix tree node with fail arcs for Aho Corasick matching.
 */
public class TrieNode {
	
	public TrieNode(TrieNode parent, String prefix, String key) {
		this.parent = parent;
		this.prefix = prefix;
		this.key = key;
	}
	
	/**
	 * Child nodes of this trie node for each character occuring in the dictionary defined by {@link Pattern}s.
	 */
	public final Map<String, TrieNode> children = new HashMap<>();
	
	/**
	 * Arc in the tree to the fail node for the Aho Corasick algorithm
	 */
	public TrieNode fail;
	
	/**
	 * The patterns (i.e. dictionary entries) ending at this node.<br>
	 * If more than 1 dictionary (i.e. set of patterns) is used, patterns from different dictionaries,
	 * ordered by priority and probability.<br>
	 * The ones with the highest priority and probability are at the beginning of the list.<br>
	 * Typically only the first pattern is used for segmentation, unless it is excluded by  {@link SegmentationOptions}, e.g. surname mode.
	 */
	public final List<Pattern> patterns = new ArrayList<>();
	
	/**
	 * The prefix string of this node (i.e. the concatenation of all keys from the root to the parent of this node).
	 */
	public final String prefix;
	
	/**
	 * The key string of this node (i.e. the character associated with this node).
	 */
	public final String key;
	
	/**
	 * Parent node in the trie tree.
	 */
	public final TrieNode parent;
	
	/**
	 * Insert the given pattern into the patterns list according to its priority and probability.<br>
	 */
	public void insertPatternByPriority(Pattern pattern) {
		int oldSize = patterns.size();
		// Add place at end of list
		patterns.add(null);
		// Shift elements until insert point is found
		for (int i = oldSize - 1; i >= 0; i--) {
			Pattern _pattern = patterns.get(i);
			if (pattern.priority == _pattern.priority &&
					pattern.probability >= _pattern.probability) {
				patterns.set(i + 1, _pattern);
			} else if (pattern.priority.ordinal() > _pattern.priority.ordinal()) {
				patterns.set(i + 1, _pattern);
			} else {
				patterns.set(i + 1, pattern);
				return;
			}
		}
		patterns.set(0, pattern);
	}
	
	@Override
	public String toString() {
		return toString(1, 20);
	}
	
	/**
	 * Useful for debugging and testing for displaying a node and its children up to a given recursion depth.
	 */
	public String toString(int recursionDepth, int maxChilds) {
		StringBuilder sb = new StringBuilder();
		toString0(recursionDepth, sb, 0, maxChilds);
		return sb.toString();
	}
	
	private void toString0(int recursionDepth, StringBuilder sb, int indentation, int maxChilds) {
		indent(indentation, sb);
		sb.append(
				"Trie " + prefix + "/" + key
						+ ", fail: " + (fail == null ? "-" : fail.prefix + "/" + fail.key)
						+ "\r\n");
		for (var pattern : patterns) {
			indent(indentation + 1, sb);
			sb.append(pattern.toString());
			sb.append("\r\n");
		}
		
		if (recursionDepth > 0) {
			int childCount = 0;
			for (TrieNode child : children.values()) {
				if (childCount++ > maxChilds) {
					indent(indentation + 1, sb);
					sb.append("... more childs ...\r\n");
					break;
				}
				child.toString0(recursionDepth - 1, sb, indentation + 1, maxChilds);
			}
		}
	}
	
	private void indent(int indentation, StringBuilder sb) {
		for (int i = 0; i < indentation; i++) {
			sb.append("  ");
		}
	}
	
}
