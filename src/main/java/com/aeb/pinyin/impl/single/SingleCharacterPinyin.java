package com.aeb.pinyin.impl.single;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.aeb.pinyin.api.PinyinToneMode;
import com.aeb.pinyin.impl.data.JsonReader;
import com.aeb.pinyin.impl.format.ToneConversion;

/**
 * API methods for getting the different Pinyin possibilities of a single Chinese character.<br>
 * Typically one chinese character has multiple Pinyin representations, depending on the context.<br>
 */
public class SingleCharacterPinyin {
	
	private static volatile Map<String, String> singleCharPinyinMap;
	
	private static Map<String, String> getSingleCharPinyinMap() {
		if (singleCharPinyinMap == null) {
			singleCharPinyinMap = new JsonReader().readReverseDictionary(JsonReader.class, "dict1.json");
		}
		return singleCharPinyinMap;
	}
	
	private static Map<PinyinToneMode, Set<String>> allPinyinSyllablesCache = Collections.synchronizedMap(
			new HashMap<>());
	
	/**
	 * Gets all possible Pinyin syllables in the given tone mode.<br>
	 * @param toneMode The tone mode for the returned Pinyin syllables.
	 * @return A set of all possible Pinyin syllables in the given tone mode.
	 */
	public static Set<String> getAllPinyinSyllables(PinyinToneMode toneMode) {
		Set<String> result = allPinyinSyllablesCache.get(toneMode);
		if (result == null) {
			synchronized (allPinyinSyllablesCache) {
				if (toneMode == PinyinToneMode.STANDARD) {
					return allPinyinSyllablesCache.computeIfAbsent(
							PinyinToneMode.STANDARD,
							key -> Collections.unmodifiableSet(
									getSingleCharPinyinMap().values()
											.stream()
											.flatMap(pinyinStr -> Arrays.stream(pinyinStr.split(" ")))
											.collect(Collectors.toSet())));
				} else {
					Set<String> standard = getAllPinyinSyllables(PinyinToneMode.STANDARD);
					return allPinyinSyllablesCache.computeIfAbsent(
							toneMode,
							key -> Collections.unmodifiableSet(
									standard.stream()
											.map(
													pinyin -> ToneConversion.convertPinyinSyllable(
															pinyin,
															toneMode))
											.collect(Collectors.toSet())));
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets all possible Pinyin representations for the given Chinese character.<br>
	 * @param ch Has to be a String with only one codepoint (i.e. one Chinese character).
	 * @return An array of Pinyin strings. If the character is not found (is not CJK), an empty array is returned.
	 * @throws IllegalArgumentException If the input string does not have exactly one character.
	 */
	public static List<String> getPinyinsForCharacter(String ch) {
		if (ch.codePointCount(0, ch.length()) != 1) {
			throw new IllegalArgumentException("Input string must have exactly one character.");
		}
		String pinyins = getSingleCharPinyinMap().get(ch);
		if (pinyins == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(pinyins.split(" "));
	}
	
	/**
	 * Gets the first possible Pinyin representation for the given Chinese character.<br>
	 * @param ch Has to be a String with only one codepoint (i.e. one Chinese character) or null.
	 * @return The first Pinyin string. If the character is not found (is not CJK) or is null, null is returned.
	 * @throws IllegalArgumentException If the input string does not have exactly one character.
	 */
	public static String getFirstPinyinForCharacter(String ch) {
		if (ch == null) {
			return null;
		}
		List<String> pinyins = getPinyinsForCharacter(ch);
		if (pinyins.isEmpty()) {
			return null;
		}
		return pinyins.get(0);
	}
	
}
