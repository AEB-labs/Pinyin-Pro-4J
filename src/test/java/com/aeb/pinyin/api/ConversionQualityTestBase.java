package com.aeb.pinyin.api;

import java.util.List;

import com.aeb.pinyin.impl.format.ToneConversion;
import com.aeb.pinyin.testutil.PinyinMatcher;
import com.aeb.pinyin.testutil.PinyinMatcher.MatchInfo;

/**
 * Base class for conversion quality tests.
 */
public abstract class ConversionQualityTestBase {
	
	public PinyinSegmentationContext completeContext = TestContextProvider.getCompleteContext();
	
	protected MatchInfo match(String zh, String expectedPinyin) {
		System.out.println("ZH " + zh);
		System.out.println("EX " + normalizePinyin(expectedPinyin));
		
		System.out.println(
				"AC " +
						normalizePinyin(
								PinyinPro4J.convertToPinyin(
										zh,
										new SegmentationOptions().setSurnameMode(getSurnameMode()),
										new PinyinFormatOptions(),
										completeContext)));
		PinyinMatcher matcher = new PinyinMatcher(getToneMode());
		List<PinyinWordSyllables> wordList = toWordList(zh);
		MatchInfo matchStandard = matcher.match(
				normalizePinyin(expectedPinyin),
				wordList);
		System.out.println(
				"RE "
						+ (matchStandard.isWordMatchPerfect() ? "W+" : "W-")
						+ "/"
						+ (matchStandard.isSyllableMatchPerfect() ? "S+" : "S-")
						+ " "
						+ matchStandard.percentWordMatch() + "%W "
						+ matchStandard.percentSyllableMatch() + "%S ");
		return matchStandard;
	}
	
	protected List<PinyinWordSyllables> toWordList(String t) {
		List<Segment> segments = PinyinPro4J.segmentizeText(
				t,
				new SegmentationOptions().setSurnameMode(getSurnameMode()),
				completeContext);
		return segments
				.stream()
				.map(
						segment -> new PinyinWordSyllables(
								normalizePinyin(segment.pinyin).replace(" ", ""),
								List.of(normalizePinyin(segment.pinyin).split(" ")),
								true))
				.toList();
	}
	
	protected final String normalizePinyin(String pinyin) {
		return ToneConversion.convertPinyinSyllable(pinyin, getToneMode());
	}
	
	protected abstract SurnameMode getSurnameMode();
	
	protected abstract PinyinToneMode getToneMode();
	
}
