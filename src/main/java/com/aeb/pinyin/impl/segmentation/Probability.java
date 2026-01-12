package com.aeb.pinyin.impl.segmentation;

import java.util.regex.Pattern;

import com.aeb.pinyin.impl.utils.UnicodeUtils;

/**
 * Probability levels for {@link com.aeb.pinyin.impl.segmentation.Pattern}s.<br>
 * Used to determine which {@link Pattern} should be preferred when multiple overlapping patterns match the same text.<br>
 * The {@link MaxProbabilitySegmentator} uses these probability levels to find the most probable segmentation of a chinese text.<br>
 * Remark, that probabilities from complete.json or modern.json are not fixed to these values. 
 */
public enum Probability {
	
	/**
	 * Currently not used.
	 */
	Unkown(1e-13),
	
	/**
	 * Used for numbers.
	 */
	Rule(1e-12),
	
	/**
	 * Used for simple dictionaries like dict2.json ... dict5.json.
	 */
	Dict(2e-8),
	
	/**
	 * Used for surnames. The probability increases with the length of the surname.
	 */
	Surname(1.0) {
		@Override
		public double getProbability(String zh) {
			return probability + UnicodeUtils.stringLength(zh);
		}
	},
	
	/**
	 * Used for custom user dictionaries (they will win always).
	 */
	Custom(1.0);
	
	protected double probability;
	
	Probability(double probability) {
		this.probability = probability;
	}
	
	public double getProbability(@SuppressWarnings("unused") String zh) {
		return probability;
	}
	
}
