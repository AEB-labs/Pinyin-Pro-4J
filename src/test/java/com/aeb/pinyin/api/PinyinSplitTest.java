package com.aeb.pinyin.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link PinyinPro4J#splitPinyinText(String, PinyinToneMode)}
 */
public class PinyinSplitTest {
	
	@Test
	public void test() {
		check(
				"sìchuān zhǎng hé kējì yǒuxiàn gōngsī",
				"sìchuān => (sì-chuān) | (sì-chu-ān); zhǎng => (zhǎng); hé => (hé); kējì => (kē-jì); yǒuxiàn => (yǒu-xiàn); gōngsī => (gōng-sī)");
		check(
				"sìchuan zhǎng hé kējì yǒuxiàn gōngsī",
				"sìchuan => (sì-chu-a-n!); zhǎng => (zhǎng); hé => (hé); kējì => (kē-jì); yǒuxiàn => (yǒu-xiàn); gōngsī => (gōng-sī)");
		
	}
	
	private void check(String pinyin, String expectedSplit) {
		List<PinyinWordSplitVariants> splitPinyinText = PinyinPro4J
				.splitPinyinText(pinyin, PinyinToneMode.STANDARD);
		String actualSplit = String.join("; ", splitPinyinText.stream().map(Object::toString).toList());
		assertEquals(expectedSplit, actualSplit);
	}
	
}
