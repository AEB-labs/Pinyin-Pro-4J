package com.aeb.pinyin.impl.format;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Stream;

import com.aeb.pinyin.api.NonZhFormatOption;
import com.aeb.pinyin.api.PinyinFormatOptions;
import com.aeb.pinyin.api.Segment;

/**
 * Formatter for outputting Pinyin strings from a segmentation result according to various formatting options.<br>
 */
public class PinyinFormatter {
	
	public PinyinFormatter(PinyinFormatOptions formatOptions) {
		this.formatOptions = formatOptions;
	}
	
	private final PinyinFormatOptions formatOptions;
	
	/**
	 * Formats the segments as a Pinyin string according to the formatting options.
	 * @param segments The segments to format.
	 * @return The formatted Pinyin string.
	 */
	public String format(List<Segment> segments) {
		StringBuilder sb = new StringBuilder();
		for (Segment segment : segments) {
			if (!segment.isZh && formatOptions.nonZhOption == NonZhFormatOption.REMOVE) {
				continue;
			}
			if (!sb.isEmpty()) {
				sb.append(formatOptions.segmentSeparator);
			}
			if (segment.isZh) {
				sb.append(formatZh(segment));
			} else {
				sb.append(formatNonZh(segment));
			}
		}
		return sb.toString();
	}
	
	private String formatZh(Segment segment) {
		final String pinyin = segment.pinyin;
		final String[] syllables = pinyin.split(" ");
		return Stream.of(syllables)
				.map(syllable -> ToneConversion.convertPinyinSyllable(syllable, formatOptions.toneMode))
				.collect(joining(formatOptions.syllableSeparator));
	}
	
	//Gets only called for non-Chinese segments, when nonZhOption is not REMOVE
	private String formatNonZh(Segment segment) {
		if (formatOptions.nonZhOption == NonZhFormatOption.KEEP || formatOptions.segmentSeparator.isEmpty()) {
			return segment.origin;
		}
		String origin = segment.origin;
		while (origin.startsWith(formatOptions.segmentSeparator)) {
			origin = origin.substring(formatOptions.segmentSeparator.length());
		}
		while (origin.endsWith(formatOptions.segmentSeparator)) {
			origin = origin.substring(0, origin.length() - formatOptions.segmentSeparator.length());
		}
		return origin;
	}
	
}
