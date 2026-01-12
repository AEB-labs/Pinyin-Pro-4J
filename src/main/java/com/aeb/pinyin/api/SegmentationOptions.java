package com.aeb.pinyin.api;

import com.aeb.pinyin.impl.segmentation.MaxProbabilitySegmentator;

/**
 * Options for segmentation process.
 */
public class SegmentationOptions {
	
	/**
	 * The surname handling mode.<br>
	 * If {@link SurnameMode#ON} , character sequences which constitute a surname will be preferred during segmentation, discarding
	 * any other possibly overlapping segmentations.<br>
	 * If {@link SurnameMode#HEAD}, only surnames at the start of the text will be preferred.<br>
	 * Defaults to {@link SurnameMode#OFF}.
	 */
	public SurnameMode surnameMode = SurnameMode.OFF;
	
	/**
	 * In {@link MaxProbabilitySegmentator} do renorm the probability of match sequences
	 * at separators.<br>
	 * This improves the segmentation speed and should therefore be true (unless for testcases).<br>
	 * Defaults to true.<br>
	 */
	public boolean renormProbabilitiesIfPossible = true;
	
	/**
	 * Fluent setter.
	 */
	public SegmentationOptions setSurnameMode(SurnameMode surnameMode) {
		this.surnameMode = surnameMode;
		return this;
	}
	
	/**
	 * Fluent setter.
	 */
	public SegmentationOptions setRenormProbabilitiesIfPossible(boolean renormProbabilitiesIfPossible) {
		this.renormProbabilitiesIfPossible = renormProbabilitiesIfPossible;
		return this;
	}
	
	@Override
	public String toString() {
		return "SegmentationOptions[surnameMode=" + surnameMode +
				", renormProbabilitiesIfPossible=" + renormProbabilitiesIfPossible + "]";
	}
	
}
