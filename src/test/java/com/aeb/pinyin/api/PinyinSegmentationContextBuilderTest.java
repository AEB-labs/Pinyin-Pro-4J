package com.aeb.pinyin.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the building of the {@link PinyinSegmentationContext} via the {@link PinyinSegmentationContextBuilder}.
 */
public class PinyinSegmentationContextBuilderTest {
	
	@Test
	public void testNullDictionary() {
		PinyinSegmentationContextBuilder unitUnderTest = new PinyinSegmentationContextBuilder(null);
		checkReaderVariablesAfterBuild(unitUnderTest);
		assertEquals(0, unitUnderTest.standardDictionaryPatterns.size());
	}
	
	@Test
	public void testBuiltinDictionary() {
		PinyinSegmentationContextBuilder unitUnderTest = new PinyinSegmentationContextBuilder(
				StandardDictionary.BUILTIN);
		checkReaderVariablesAfterBuild(unitUnderTest);
		assertEquals(0, unitUnderTest.standardDictionaryPatterns.size());
	}
	
	@Test
	public void testCompleteDictionary() {
		PinyinSegmentationContextBuilder unitUnderTest = new PinyinSegmentationContextBuilder(
				StandardDictionary.COMPLETE);
		checkReaderVariablesAfterBuild(unitUnderTest);
		int additionalSize = unitUnderTest.standardDictionaryPatterns.size();
		assertTrue(additionalSize > 300000 && additionalSize < 400000);
	}
	
	@Test
	public void testModernDictionary() {
		PinyinSegmentationContextBuilder unitUnderTest = new PinyinSegmentationContextBuilder(
				StandardDictionary.MODERN_CHINESE);
		checkReaderVariablesAfterBuild(unitUnderTest);
		int additionalSize = unitUnderTest.standardDictionaryPatterns.size();
		assertTrue(additionalSize > 50000 && additionalSize < 100000);
	}
	
	protected void checkReaderVariablesAfterBuild(PinyinSegmentationContextBuilder unitUnderTest) {
		PinyinSegmentationContext buildResult = unitUnderTest.build();
		assertNotNull(buildResult);
		checkDict2(unitUnderTest.dict2);
		checkDict3(unitUnderTest.dict3);
		checkDict4(unitUnderTest.dict4);
		checkDict5(unitUnderTest.dict5);
		checkDictNumbers(unitUnderTest.dictNumbers);
		checkDictSurnames(unitUnderTest.dictSurnames);
		assertEquals(
				unitUnderTest.standardDictionaryPatterns.size() +
						unitUnderTest.dict2.size() + unitUnderTest.dict3.size() + unitUnderTest.dict4.size()
						+ unitUnderTest.dict5.size() + unitUnderTest.dictNumbers.size()
						+ unitUnderTest.dictSurnames.size(),
				unitUnderTest.allPatterns.size());
	}
	
	protected void checkDict2(Map<String, String> dict) {
		assertTrue(dict.size() > 2000);
		assertTrue(dict.size() < 3000);
		String value = dict.get("朝朝");
		assertEquals("zhāo zhāo", value);
	}
	
	protected void checkDict3(Map<String, String> dict) {
		assertTrue(dict.size() > 300);
		assertTrue(dict.size() < 400);
		String value = dict.get("一字决");
		assertEquals("yī zì jué", value);
	}
	
	protected void checkDict4(Map<String, String> dict) {
		assertTrue(dict.size() > 1500);
		assertTrue(dict.size() < 1700);
		String value = dict.get("成吉思汗");
		assertEquals("chéng jí sī hán", value);
	}
	
	protected void checkDict5(Map<String, String> dict) {
		assertTrue(dict.size() > 10);
		assertTrue(dict.size() < 20);
		String value = dict.get("巴尔干半岛");
		assertEquals("bā ěr gàn bàn dǎo", value);
	}
	
	protected void checkDictNumbers(Map<String, String> dict) {
		assertTrue(dict.size() > 100);
		assertTrue(dict.size() < 200);
		String value = dict.get("九行");
		assertEquals("jiǔ háng", value);
	}
	
	protected void checkDictSurnames(Map<String, String> dict) {
		assertTrue(dict.size() > 400);
		assertTrue(dict.size() < 500);
		String value = dict.get("南宫");
		assertEquals("nán gōng", value);
	}
	
}
