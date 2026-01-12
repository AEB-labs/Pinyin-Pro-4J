package com.aeb.pinyin.api;

import java.util.Collections;
import java.util.List;

/**
 * Multiple possible splittings of a pinyin word into syllables.
 * @param originalPinyin The original Pinyin word.
 * @param variants The list of possible syllable splittings.
 */
public record PinyinWordSplitVariants(String originalPinyin, List<PinyinWordSyllables> variants) {
	
	public PinyinWordSplitVariants(String originalPinyin, PinyinWordSyllables singleVariant) {
		this(originalPinyin, Collections.singletonList(singleVariant));
	}
	
	@Override
	public String toString() {
		return originalPinyin + " => " + String.join(" | ", variants.stream().map(Object::toString).toList());
	}
}
