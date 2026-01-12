package com.aeb.pinyin.impl.segmentation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aeb.pinyin.impl.utils.UnicodeUtils;

/**
 * Segmentator, which finds the segmentation  with the maximum probability, 
 * given the matched patterns from an Aho-Corasick matching.<br>
 * If there are overlapping MatchPatterns, the segmentator finds the combination of patterns, with the highest
 * combined probability.<br>
 */
public class MaxProbabilitySegmentator {
	
	/**
	 * Temporary data structure to hold a sequence of matched patterns along with its combined probability.
	 */
	protected class MatchSequence {
		
		public MatchSequence(MatchedPattern initialPattern) {
			patterns.add(initialPattern);
			length = initialPattern.pattern.length;
			probability = BigDecimal.valueOf(initialPattern.pattern.probability);
		}
		
		public MatchSequence(MatchSequence sequenceToClone) {
			patterns.addAll(sequenceToClone.patterns);
			length = sequenceToClone.length;
			probability = sequenceToClone.probability;
		}
		
		/**
		 * The list of matched patterns in this sequence.
		 */
		protected List<MatchedPattern> patterns = new ArrayList<>();
		
		/**
		 * The current length of the sequence in code points.<br>
		 * The length increases as patterns are added up to the total length of the text.
		 */
		protected int length;
		
		/**
		 * The combined probability of all patterns in this sequence.<br>
		 * We are multiplying where small probabilities (e.g. 1e-12) which easily leads to floating point underflow
		 * in long texts, hence we are using BigDecimal.
		 */
		protected BigDecimal probability;
		
		/**
		 * Return true if this sequence is better than the existing one.
		 * Considering probability along with the decimal point shifts to the right.
		 */
		public boolean isBetterThan(MatchSequence existingSequence) {
			return this.probability.compareTo(existingSequence.probability) > 0;
		}
		
		/**
		 * Adds the pattern to the sequence, updating length and probability.
		 */
		public void add(MatchedPattern pattern) {
			patterns.add(pattern);
			length += pattern.pattern.length;
			if (possibleSuffixCodePoints != null && patterns.size() > 1) {
				MatchedPattern previousPattern = patterns.get(patterns.size() - 2);
				if (canNotBeASuffix(previousPattern)) {
					//When the previous pattern can not be a suffix, the Aho-Corasick matching will
					//not produce any other MatchedPattern ending at that or before that code point.
					//Hence the current MatchSequence is clone of the currently and future best
					//MatchSequence ending at the previous pattern, so we can virtually renorm 
					//probability of the previous sequence to 1.0
					//i.e. set the probability of this to the probability of the current pattern only.
					//This avoids too long BigDecimal numbers:
					//In a long text the internal representation of multiplied BigDecimal may become very long,
					//which consumes memory and slows down calculations.
					probability = BigDecimal.valueOf(pattern.pattern.probability);
					return;
				}
			}
			
			probability = probability.multiply(BigDecimal.valueOf(pattern.pattern.probability));
		}
		
		private boolean canNotBeASuffix(MatchedPattern previousPattern) {
			return previousPattern.pattern.length == 1 && !possibleSuffixCodePoints.contains(
					previousPattern.pattern.zh);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("MatchSequence(length: ");
			sb.append(length);
			sb.append(", probability: ");
			sb.append(probability.toString());
			sb.append(", patterns:");
			for (int i = 0; i < patterns.size(); i++) {
				sb.append("\r\n  ");
				if (i > 10) {
					sb.append("... more patterns ...");
					break;
				} else {
					sb.append(patterns.get(i).toString());
				}
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * Constructor for segmentation for the given text.
	 */
	public MaxProbabilitySegmentator(String text) {
		this.codepoints = UnicodeUtils.splitString(text);
	}
	
	/**
	 * All code points of the text to segmentize.
	 */
	protected final String[] codepoints;
	
	/**
	 * Possible suffix code points from the Aho-Corasick matching.
	 * If a code point is not in this set,
	 * it is safe, that only one pattern can end at this code point.
	 * Hence we can re-norm the probability of a MatchSequence to 1.0, avoiding
	 * too long BigDecimal numbers.
	 */
	protected Set<String> possibleSuffixCodePoints = null;
	
	/**
	 * Map of so far best match sequences for a given length in code points.
	 */
	private final Map<Integer, MatchSequence> bestMatchSequenceForLength = new HashMap<>();
	
	/**
	 * Callback method, called for matched patterns from Aho-Corasick matching.<br>
	 * Instead of passing a list of all matched patterns after matching to the segmentator, we prefer to
	 * use a callback here, as this avoids storing all matched patterns in a list first.<br>
	 * So patterns belonging to inferior match sequences may be cleaned very fast by the garbage collector.
	 */
	public void callbackForPattern(MatchedPattern pattern) {
		MatchSequence bestSequence = getBestMatchSequence(pattern.index);
		if (bestSequence == null) {
			if (pattern.index != 0) {
				throw new RuntimeException("Internal error, found no sequence ending at index " + pattern.index);
			}
			MatchSequence matchSequence = new MatchSequence(pattern);
			putSequenceIfBetter(matchSequence);
		} else {
			//Make a clone, add the new pattern and put if better
			bestSequence = new MatchSequence(bestSequence);
			bestSequence.add(pattern);
			putSequenceIfBetter(bestSequence);
		}
	}
	
	/**
	 * Get the segmentation result as list of matched patterns, after all patterns have been passed to the callback.<br>
	 * The result is the best match sequence for the full length of the text.
	 */
	public List<MatchedPattern> getSegmentationResult() {
		if (codepoints.length == 0) {
			return Collections.emptyList();
		}
		return getBestMatchSequence(codepoints.length).patterns;
	}
	
	private MatchSequence getBestMatchSequence(int index) {
		MatchSequence best = bestMatchSequenceForLength.get(index);
		if (best == null && index > 0) {
			//This can happen if there is no match sequence ending at the given index
			//e.g. because of non chinese characters in the text or chinese characters, which are
			//only used as prefixes in longer words.
			//We create a dummy match sequence with a single character, which may later be 
			//handled further by postprocessing, if its a chinese character, which normally does
			//not appear alone (e.g. tone marks).
			fillBestMatchSequenceForIndexWithSingleCodePoint(index);
			best = bestMatchSequenceForLength.get(index);
			if (best == null) {
				throw new RuntimeException("Match sequence for index " + index + " could not be created.");
			}
		}
		return best;
	}
	
	private void fillBestMatchSequenceForIndexWithSingleCodePoint(int index) {
		String codePoint = codepoints[index - 1];
		MatchSequence sequenceBefore = getBestMatchSequence(index - 1);
		MatchedPattern newMatchPattern = new MatchedPattern(
				new Pattern(codePoint, codePoint, Priority.Normal, 1.0, null),
				index - 1);
		MatchSequence newMatchSequence;
		if (sequenceBefore == null) {
			//Can only be null if index-1 == 0
			newMatchSequence = new MatchSequence(newMatchPattern);
		} else {
			//Clone and add single code point pattern
			newMatchSequence = new MatchSequence(sequenceBefore);
			newMatchSequence.add(newMatchPattern);
		}
		bestMatchSequenceForLength.put(newMatchSequence.length, newMatchSequence);
	}
	
	/**
	 * Put the newly constructed match sequence into the map if it is better than the existing one for that length.
	 */
	private void putSequenceIfBetter(MatchSequence matchSequence) {
		MatchSequence existingSequence = bestMatchSequenceForLength.get(matchSequence.length);
		if (existingSequence == null || matchSequence.isBetterThan(existingSequence)) {
			bestMatchSequenceForLength.put(matchSequence.length, matchSequence);
		}
	}
	
	public Set<String> getPossibleSuffixCodePoints() {
		return possibleSuffixCodePoints;
	}
	
	public void setPossibleSuffixCodePoints(Set<String> possibleSuffixCodePoints) {
		this.possibleSuffixCodePoints = possibleSuffixCodePoints;
	}
	
}
