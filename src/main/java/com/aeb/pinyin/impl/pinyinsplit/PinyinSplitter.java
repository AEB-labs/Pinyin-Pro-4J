package com.aeb.pinyin.impl.pinyinsplit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aeb.pinyin.api.PinyinPro4J;
import com.aeb.pinyin.api.PinyinToneMode;
import com.aeb.pinyin.api.PinyinWordSplitVariants;
import com.aeb.pinyin.api.PinyinWordSyllables;

/**
 * Split a text in Pinyin into its words and syllables.<br>
 */
public class PinyinSplitter {
	
	private static Map<PinyinToneMode, PinyinSplitter> allSplittersCache = Collections.synchronizedMap(
			new HashMap<>());
	
	public static PinyinSplitter getSplitter(PinyinToneMode toneMode) {
		return allSplittersCache.computeIfAbsent(toneMode, key -> new PinyinSplitter(toneMode));
	}
	
	/**
	 * Creates a new Pinyin syllable splitter for the given tone mode.
	 * @param toneMode The tone mode of the Pinyin text to split.
	 */
	private PinyinSplitter(PinyinToneMode toneMode) {
		PinyinPro4J.getAllPinyinSyllables(toneMode).stream().forEach(syllable -> {
			char firstChar = syllable.charAt(0);
			syllableStartCache.computeIfAbsent(firstChar, key -> new java.util.HashSet<>()).add(syllable);
		});
	}
	
	private final Map<Character, Set<String>> syllableStartCache = new HashMap<>();
	
	/**
	 * Splits the given Pinyin text into its words and returns for each word a {@link PinyinWordSplitVariants} object,
	 * which contains possibly multiple possible {@link PinyinWordSyllables}, each denoting a possible split of the word into syllables.<br>
	 * Words are expected to be separated by white spaces, whereas syllables in a word are not expected to be separated.<br> 
	 * Character sequences that do not correspond to valid Pinyin syllables will not be split but
	 * kept as-is as one syllable, which is denoted by {@link PinyinWordSyllables#isValidPinyin()} .<br>
	 * @param pinyinText The Pinyin text to split.
	 * @return For each word in the text, the possible splittings of the word into syllables.
	 */
	public List<PinyinWordSplitVariants> splitPinyinText(String pinyinText) {
		List<PinyinWordSplitVariants> totalResults = new ArrayList<>();
		String[] wordStrings = pinyinText.trim().split("\\s+");
		for (String wordString : wordStrings) {
			if (wordString.isBlank()) {
				continue;
			}
			PinyinWordSplitVariants wordResults = splitPinyinWord(wordString);
			totalResults.add(wordResults);
		}
		return totalResults;
	}
	
	/**
	 * Splits one Pinyin word (without spaces) into its syllables.
	 * @param wordString The Pinyin word to split.
	 * @return The possible splittings of the word into syllables.
	 */
	private PinyinWordSplitVariants splitPinyinWord(String wordString) {
		if ("".equals(wordString)) {
			return new PinyinWordSplitVariants("", new PinyinWordSyllables());
		}
		Character firstChar = wordString.charAt(0);
		Set<String> possibleSyllablesWithFirstChar = syllableStartCache.getOrDefault(firstChar, Collections.emptySet());
		List<String> possibleSyllables = possibleSyllablesWithFirstChar.stream()
				.filter(wordString::startsWith)
				.toList();
		if (possibleSyllables.isEmpty()) {
			// No valid syllable found at start of word - return the whole word as a single "syllable"
			return new PinyinWordSplitVariants(wordString, new PinyinWordSyllables(wordString, false));
		}
		List<PinyinWordSyllables> goodResults = new ArrayList<>();
		List<PinyinWordSyllables> invalidResults = new ArrayList<>();
		for (String candidate : possibleSyllables) {
			PinyinWordSplitVariants subSolutions = splitPinyinWord(wordString.substring(candidate.length()));
			for (PinyinWordSyllables subSolution : subSolutions.variants()) {
				List<String> combinedSyllables = new ArrayList<>();
				combinedSyllables.add(candidate);
				combinedSyllables.addAll(subSolution.syllables());
				if (subSolution.isValidPinyin()) {
					goodResults.add(new PinyinWordSyllables(wordString, combinedSyllables, true));
				} else {
					invalidResults.add(new PinyinWordSyllables(wordString, combinedSyllables, false));
				}
			}
		}
		if (!goodResults.isEmpty()) {
			return new PinyinWordSplitVariants(wordString, goodResults);
		} else {
			return new PinyinWordSplitVariants(wordString, invalidResults);
		}
	}
	
}
