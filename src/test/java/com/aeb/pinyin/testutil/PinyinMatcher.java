package com.aeb.pinyin.testutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.aeb.pinyin.api.PinyinPro4J;
import com.aeb.pinyin.api.PinyinToneMode;
import com.aeb.pinyin.api.PinyinWordSplitVariants;
import com.aeb.pinyin.api.PinyinWordSyllables;

/**
 * Matches two Pinyin strings by first splitting them into syllables and then comparing the syllables.<br>
 * This is useful for quality checking of Pinyin conversion algorithms.<br>
 */
public class PinyinMatcher {
	
	public PinyinMatcher(PinyinToneMode toneMode) {
		this.toneMode = toneMode;
	}
	
	public static class MatchInfo {
		
		public MatchInfo() {
		}
		
		public MatchInfo(
				int wordCountTested,
				int wordCountReference,
				int syllableCount,
				int wordMatchCount,
				int syllableMatchCount) {
			super();
			this.wordCountTested = wordCountTested;
			this.wordCountReference = wordCountReference;
			this.syllableCount = syllableCount;
			this.wordMatchCount = wordMatchCount;
			this.syllableMatchCount = syllableMatchCount;
		}
		
		public int wordCountTested = 0;
		public int wordCountReference = 0;
		public int syllableCount = 0;
		public int wordMatchCount = 0;
		public int syllableMatchCount = 0;
		
		public void add(MatchInfo other) {
			this.wordCountTested += other.wordCountTested;
			this.wordCountReference += other.wordCountReference;
			this.syllableCount += other.syllableCount;
			this.wordMatchCount += other.wordMatchCount;
			this.syllableMatchCount += other.syllableMatchCount;
		}
		
		public double percentSyllableMatch() {
			if (syllableCount == 0) {
				return 100.0;
			} else {
				return ((syllableMatchCount / (double) syllableCount) * 100.0);
			}
		}
		
		public double percentWordMatch() {
			if (wordCountTested == 0) {
				return 100.0;
			} else {
				return ((wordMatchCount / (double) wordCountTested) * 100.0);
			}
		}
		
		public boolean isWordMatchPerfect() {
			return wordCountTested == wordMatchCount;
		}
		
		public boolean isSyllableMatchPerfect() {
			return syllableCount == syllableMatchCount;
		}
		
	}
	
	private final PinyinToneMode toneMode;
	
	public MatchInfo match(String referencePinyin, List<PinyinWordSyllables> testedPinyin) {
		List<PinyinWordSplitVariants> splitsReference = PinyinPro4J.splitPinyinText(referencePinyin, toneMode);
		int wordCountTested = testedPinyin.size();
		int wordMatchCount = new LongestCommonSubsequenceLength<>(
				splitsReference.stream().map(split -> split.originalPinyin()).toList(),
				testedPinyin.stream().map(split -> split.originalPinyin()).toList(),
				(a, b) -> a.equals(b)).computeLongestCommonSubsequenceLength();
		
		List<String> testedSyllables = testedPinyin.stream()
				.flatMap(split -> split.syllables().stream())
				.toList();
		List<List<String>> syllableVariantsReferences = getSyllableVariants(splitsReference);
		int syllableCount = testedSyllables.size();
		AtomicInteger maxSyllableMatchCount = new AtomicInteger(-1);
		for (List<String> syllableVariantsReference : syllableVariantsReferences) {
			int syllableMatchCount = new LongestCommonSubsequenceLength<>(
					syllableVariantsReference,
					testedSyllables,
					(a, b) -> a.equals(b)).computeLongestCommonSubsequenceLength();
			if (syllableMatchCount > maxSyllableMatchCount.get()) {
				maxSyllableMatchCount.set(syllableMatchCount);
			}
		}
		return new MatchInfo(
				wordCountTested,
				referencePinyin.split(" ").length,
				syllableCount,
				wordMatchCount,
				maxSyllableMatchCount.get());
	}
	
	private List<List<String>> getSyllableVariants(List<PinyinWordSplitVariants> splitsReference) {
		List<List<String>> result = new ArrayList<>();
		result.add(Collections.emptyList());
		for (PinyinWordSplitVariants splitVariant : splitsReference) {
			for (PinyinWordSyllables variant : splitVariant.variants()) {
				List<List<String>> newResult = new ArrayList<>();
				for (List<String> existingResultElement : result) {
					List<String> newResultElement = new ArrayList<>(existingResultElement);
					newResultElement.addAll(variant.syllables());
					newResult.add(newResultElement);
				}
				result = newResult;
			}
		}
		return result;
	}
	
}
