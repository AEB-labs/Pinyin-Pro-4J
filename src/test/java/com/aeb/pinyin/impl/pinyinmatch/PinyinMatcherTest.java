package com.aeb.pinyin.impl.pinyinmatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.aeb.pinyin.api.ConversionQualityTestBase;
import com.aeb.pinyin.api.PinyinToneMode;
import com.aeb.pinyin.api.SurnameMode;
import com.aeb.pinyin.testutil.PinyinMatcher.MatchInfo;

public class PinyinMatcherTest extends ConversionQualityTestBase {
	
	@Test
	public void test() {
		check(
				"四川长和科技有限公司",
				"sìchuān zhǎng hé kējì yǒuxiàn gōngsī",
				5,
				3,
				10,
				9);
		
	}
	
	private void check(
			String zh,
			String expectedPinyin,
			int expectedWordCount,
			int expectedWordMatchCount,
			int expectedSyllableCount,
			int expectedSyllableMatchCount) {
		MatchInfo matchStandard = match(zh, expectedPinyin);
		assertEquals(expectedWordCount, matchStandard.wordCountTested);
		assertEquals(expectedWordMatchCount, matchStandard.wordMatchCount);
		assertEquals(expectedSyllableCount, matchStandard.syllableCount);
		assertEquals(expectedSyllableMatchCount, matchStandard.syllableMatchCount);
	}
	
	@Override
	protected SurnameMode getSurnameMode() {
		return SurnameMode.OFF;
	}
	
	@Override
	protected PinyinToneMode getToneMode() {
		return PinyinToneMode.STANDARD;
	}
	
}
