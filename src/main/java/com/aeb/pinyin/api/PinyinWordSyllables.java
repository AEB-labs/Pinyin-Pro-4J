package com.aeb.pinyin.api;

import java.util.Collections;
import java.util.List;

/**
 * One possible splitting of a pinyin word into syllables.
 * @param syllables The list of syllables in the word.
 * @param isValidPinyin Indicates whether all syllables are valid Pinyin syllables.
 */
public record PinyinWordSyllables(String originalPinyin, List<String> syllables, boolean isValidPinyin) {
	
	public PinyinWordSyllables(String singleSyllable, boolean isValidPinyin) {
		this(singleSyllable, Collections.singletonList(singleSyllable), isValidPinyin);
	}
	
	public PinyinWordSyllables() {
		this("", Collections.emptyList(), true);
	}
	
	@Override
	public String toString() {
		return "(" + String.join("-", syllables) + (isValidPinyin ? ")" : "!)");
	}
}
