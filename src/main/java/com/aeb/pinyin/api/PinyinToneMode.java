package com.aeb.pinyin.api;

/**
 * Tone representation mode for Pinyin formatting.
 */
public enum PinyinToneMode {
	
	/**
	 * Standard tone representation with tone marks ,e.g. "pīn".
	 */
	STANDARD,
	
	/**
	 * Tone numbers representation, e.g. "pin1".<br>
	 * However, ü and ê are still represented with their diacritics.
	 */
	NUMBERS,
	
	/**
	 * Tone numbers representation, e.g. "pin1".<br>
	 * However, ü is replaced by v and ê by e.
	 */
	NUMBERS_ASCII,
	
	/**
	 * No tones e.g. "pin".<br>
	 * ü and ê are still represented with their diacritics.
	 */
	NONE,
	
	/**
	 * No tones e.g. "pin".<br>
	 * ü is replaced by v and ê by e.
	 */
	NONE_ASCII;
	
}
