package com.aeb.pinyin.impl.segmentation;

import com.aeb.pinyin.api.CustomPattern;

/**
 * A pattern of chinese text associated with one possible pinyin
 * transcription.<br>
 * This may be a single chinese letter or a word of a chinese dictionary.<br>
 * In a text multiple patterns can match. The priority and probability member
 * variables guide the segmentation algorithm to find the most probably
 * segementation.<br>
 */
public class Pattern {
	
	public Pattern(String zh, String pinyin, Priority priority, double probability, String partOfSpeech) {
		this.zh = zh;
		this.pinyin = pinyin;
		this.length = zh.codePointCount(0, zh.length());
		this.priority = priority;
		this.probability = probability;
		this.partOfSpeech = partOfSpeech;
	}
	
	public Pattern(CustomPattern customPattern) {
		this.zh = customPattern.getZh();
		this.pinyin = customPattern.getPinyin();
		this.length = zh.codePointCount(0, zh.length());
		this.priority = Priority.newCustomPriority(customPattern);
		this.probability = customPattern.getProbability();
		this.partOfSpeech = null;
	}
	
	/**
	 * Orignal chinese word.
	 */
	public final String zh;
	
	/**
	 * Pinyin translation.
	 */
	public final String pinyin;
	
	/**
	 * Priority of the pattern.
	 * In case of more than one dictionary having the the same chinese text,
	 * this is the first sort criterion for choosing the pattern to use.
	 */
	public final Priority priority;
	
	/**
	 * Word frequency probability.
	 * In case of more than one dictionary having the the same chinese text,
	 * this is the second sort criterion (after priority).
	 */
	public final double probability;
	
	/**
	 * Length (of code points) of zh.
	 */
	public final int length;
	
	/**
	 * Part of speech (only filled for some dictionaries).
	 */
	public final String partOfSpeech;
	
	@Override
	public String toString() {
		return "Pattern " + zh + "->" + pinyin + "(prio=" + priority + ", prob=" + probability + ", len=" + length
				+ ")";
	}
	
}
