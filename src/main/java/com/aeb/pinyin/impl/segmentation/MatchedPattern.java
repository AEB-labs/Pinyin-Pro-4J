package com.aeb.pinyin.impl.segmentation;

/**
 * A {@link Pattern} matched by the Aho Corasick algorithm in the text with its start index.
 */
public class MatchedPattern {
	
	public MatchedPattern(Pattern pattern, int index) {
		this.pattern = pattern;
		this.index = index;
	}
	
	/**
	 * The matched pattern.
	 */
	public final Pattern pattern;
	
	/**
	 * Start index of the matched pattern in the text.
	 */
	public final int index;
	
	@Override
	public String toString() {
		return "Match @" + index + ": " + pattern;
	}
	
}
