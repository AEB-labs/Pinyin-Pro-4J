package com.aeb.pinyin.api;

import java.util.List;
import java.util.Set;

import com.aeb.pinyin.impl.format.PinyinFormatter;
import com.aeb.pinyin.impl.pinyinsplit.PinyinSplitter;
import com.aeb.pinyin.impl.single.SingleCharacterPinyin;

/**
 * Main class of the PinyinPro4J library with all API methods for Pinyin processing.<br>
 */
public class PinyinPro4J {
	
	/**
	 * Creates a new PinyinContext with the given additional dictionary.<br>
	 * A context is required for segmentation of Chinese text into Pinyin segments.<br>
	 * Creating a context is an expensive operation, so reuse a context whenever.<br>
	 * @param dict The additional dictionary to use, or null for no additional dictionary.
	 * @return The created PinyinContext.
	 */
	public static PinyinSegmentationContext createNewContext(StandardDictionary dict) {
		return new PinyinSegmentationContextBuilder(dict).build();
	}
	
	/**
	 * Creates a new PinyinContext with the given additional dictionary and {@link CustomPattern}s.<br>
	 * A context is required for segmentation of Chinese text into Pinyin segments.<br>
	 * Creating a context is an expensive operation, so reuse a context whenever.<br>
	 * @param dict The additional dictionary to use, or null for no additional dictionary.
	 * @param customPatterns The additional {@link CustomPattern}s to use.
	 * @return The created PinyinContext.
	 */
	public static PinyinSegmentationContext createNewContext(
			StandardDictionary dict,
			List<CustomPattern> customPatterns) {
		return new PinyinSegmentationContextBuilder(dict).setCustomPatterns(customPatterns).build();
	}
	
	/**
	 * Converts the given text to Pinyin using the given segmentation options, format options and context.<br>
	 * First segementation is performed, then formatting of the segments to Pinyin string.<br>
	 * @param text The chinese text to convert.
	 * @param segmentationOptions Options for the segmentation process.
	 * @param formatOptions Options for formatting the Pinyin output.
	 * @param context The context to use for segmentation.<br>
	 *                Creating a context is an expensive operation, so reuse a context whenever possible.<br>
	 *                You can create a context with {@link #createNewContext(StandardDictionary)}.
	 * @return
	 */
	public static String convertToPinyin(
			String text,
			SegmentationOptions segmentationOptions,
			PinyinFormatOptions formatOptions,
			PinyinSegmentationContext context) {
		List<Segment> segments = segmentizeText(text, segmentationOptions, context);
		PinyinFormatter formatter = new PinyinFormatter(formatOptions);
		return formatter.format(segments);
	}
	
	/**
	 * Segments the given text into {@link Segment}s using the given context and segmentation options.<br>
	 * This method can be used for further processing of the segments, e.g. for custom formatting.<br>
	 * 
	 * @param text The text to segment.
	 * @param options The segmentation options to use.
	 * @param context The context to use for segmentation.<br>
	 *                Creating a context is an expensive operation, so reuse a context whenever possible.<br>
	 *                You can create a context with {@link #createNewContext(StandardDictionary)}.
	 * @return
	 */
	public static List<Segment> segmentizeText(
			String text,
			SegmentationOptions options,
			PinyinSegmentationContext context) {
		return context.segmentizeText(text, options);
	}
	
	/**
	 * Gets all possible Pinyin representations for the given single Chinese character.<br>
	 * @param ch Has to be a String with only one codepoint (i.e. one Chinese character).
	 * @return An array of Pinyin strings. If the character is not found (is not CJK), an empty array is returned.
	 * @throws IllegalArgumentException If the input string does not have exactly one character.
	 */
	public static List<String> getPinyinsForCharacter(String ch) {
		return SingleCharacterPinyin.getPinyinsForCharacter(ch);
	}
	
	/**
	 * Gets the first possible Pinyin representation for the given single Chinese character.<br>
	 * @param ch Has to be a String with only one codepoint (i.e. one Chinese character) or null.
	 * @return The first Pinyin string. If the character is not found (is not CJK) or is null, null is returned.
	 * @throws IllegalArgumentException If the input string does not have exactly one character.
	 */
	public static String getFirstPinyinForCharacter(String ch) {
		return SingleCharacterPinyin.getFirstPinyinForCharacter(ch);
	}
	
	/**
	 * Gets all possible Pinyin syllables in the given tone mode.<br>
	 * @param toneMode The tone mode of the returned Pinyin syllables.
	 * @return A set of all possible Pinyin syllables in the given tone mode.
	 */
	public static Set<String> getAllPinyinSyllables(PinyinToneMode toneMode) {
		return SingleCharacterPinyin.getAllPinyinSyllables(toneMode);
	}
	
	/**
	 * Splits the given Pinyin text into its words and returns for each word a {@link PinyinWordSplitVariants} object,
	 * which contains possibly multiple possible {@link PinyinWordSyllables}, each denoting a possible split of the word into syllables.<br>
	 * Words are expected to be separated by white spaces, whereas syllables in a word are not expected to be separated.<br> 
	 * Character sequences that do not correspond to valid Pinyin syllables will not be split but
	 * kept as-is as one syllable, which is denoted by {@link PinyinWordSyllables#isValidPinyin()} .<br>
	 * @param pinyinText The Pinyin text to split.
	 * @param toneMode The tone mode of the Pinyin text.
	 * @return For each word in the text, the possible splittings of the word into syllables.
	 */
	public static List<PinyinWordSplitVariants> splitPinyinText(
			String pinyinText,
			PinyinToneMode toneMode) {
		PinyinSplitter splitter = PinyinSplitter.getSplitter(toneMode);
		return splitter.splitPinyinText(pinyinText);
	}
	
}
