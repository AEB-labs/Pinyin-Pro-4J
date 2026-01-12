package com.aeb.pinyin.api;

/**
 * Parameters for formatting Pinyin output.
 */
public class PinyinFormatOptions {
	
	/**
	 * The separator to use between two segments of the original text.
	 * Segments are either chinese words (recognize by the dictionary passed, when constructing a {@link PinyinSegmentationContext})
	 * or consecutive non-chinese characters.<br>
	 * Defaults to a single space.
	 */
	public String segmentSeparator = " ";
	
	/**
	 * The separator to use between Pinyin syllables within a segment.<br>
	 * Defaults to empty string (no separator), which results in concatenated syllables for one segment (i.e. zh word).
	 */
	public String syllableSeparator = "";
	
	/**
	 * Mode how to represent tones in Pinyin output.<br>
	 */
	public PinyinToneMode toneMode = PinyinToneMode.STANDARD;
	
	/**
	 * Option for handling non-Chinese segments.
	 */
	public NonZhFormatOption nonZhOption = NonZhFormatOption.TRIM;
	
	/**
	 * Setter for fluent API.
	 */
	public PinyinFormatOptions setSegmentSeparator(String segmentSeparator) {
		this.segmentSeparator = segmentSeparator;
		return this;
	}
	
	/**
	 * Setter for fluent API.
	 */
	public PinyinFormatOptions setSyllableSeparator(String syllableSeparator) {
		this.syllableSeparator = syllableSeparator;
		return this;
	}
	
	/**
	 * Setter for fluent API.
	 */
	public PinyinFormatOptions setNonZhOption(NonZhFormatOption nonZhOption) {
		this.nonZhOption = nonZhOption;
		return this;
	}
	
	/**
	 * Setter for fluent API.
	 */
	public PinyinFormatOptions setToneMode(PinyinToneMode toneMode) {
		this.toneMode = toneMode;
		return this;
	}
	
	@Override
	public String toString() {
		return "PinyinFormatOptions [segmentSeparator=" + segmentSeparator + ", syllableSeparator=" + syllableSeparator
				+ ", toneMode=" + toneMode + ", nonZhOption=" + nonZhOption + "]";
	}
	
}
