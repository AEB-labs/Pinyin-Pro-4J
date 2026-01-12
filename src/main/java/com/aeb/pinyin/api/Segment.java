package com.aeb.pinyin.api;

/**
 * A segment of text, i.e. a part of the text recognized as one chinese word or a
 * contiguous non-chinese text part.<br>
 */
public class Segment {
	
	public Segment(String origin, int index, boolean isZh, String pinyin) {
		super();
		this.origin = origin;
		this.index = index;
		this.isZh = isZh;
		this.pinyin = pinyin;
	}
	
	/**
	 * Part of the original text.
	 */
	public final String origin;
	
	/**
	 * Start index of the segment in the original text.
	 */
	public final int index;
	
	/**
	 * True, if the original text was recognized as Chinese text, false otherwise.
	 */
	public final boolean isZh;
	
	/**
	 * Pinyin representation of orginal text with tone marks and blanks separating syllables.<br>
	 * If the isZh flag is false, this is identical to the origin text.
	 */
	public final String pinyin;
	
	/**
	 * String representation for debugging and testing, containing all relevant fields.
	 */
	@Override
	public String toString() {
		String prefix = isZh ? "ZH" : "NON-ZH";
		return prefix + "@" + index + ":" + origin + "=>" + pinyin;
	}
	
}
