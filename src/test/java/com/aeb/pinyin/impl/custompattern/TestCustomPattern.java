package com.aeb.pinyin.impl.custompattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.aeb.pinyin.api.CustomPattern;
import com.aeb.pinyin.api.PinyinFormatOptions;
import com.aeb.pinyin.api.PinyinPro4J;
import com.aeb.pinyin.api.PinyinSegmentationContext;
import com.aeb.pinyin.api.SegmentationOptions;
import com.aeb.pinyin.api.StandardDictionary;
import com.aeb.pinyin.api.TestContextProvider;

public class TestCustomPattern {
	
	private PinyinSegmentationContext stdContext = TestContextProvider.getCompleteContext();
	
	@Test
	public void testCustomDictionaryMatchAtBeginning() {
		
		CustomPattern customPattern1 = new CustomPattern("关东", "guān dōng")
				.setProbability(0.12)
				.setAppliesAtStart(true)
				.setAppliesInMiddle(false)
				.setAppliesAtEnd(false);
		
		PinyinSegmentationContext customContext = PinyinPro4J.createNewContext(
				StandardDictionary.COMPLETE,
				List.of(customPattern1));
		
		//Matches at beginning
		match(stdContext, "关东军", "guāndōngjūn");
		match(customContext, "关东军", "guāndōng jūn");
		
		//Matches not at end
		match(stdContext, "闯关东", "chuǎngguāndōng");
		match(customContext, "闯关东", "chuǎngguāndōng");
		
		//Matches not in the middle
		match(stdContext, "闯关东问", "chuǎngguāndōng wèn");
		match(customContext, "闯关东问", "chuǎngguāndōng wèn");
	}
	
	@Test
	public void testCustomDictionaryMatchInMiddle() {
		
		CustomPattern customPattern1 = new CustomPattern("关东", "guān dōng")
				.setProbability(0.12)
				.setAppliesAtStart(false)
				.setAppliesInMiddle(true)
				.setAppliesAtEnd(false);
		
		PinyinSegmentationContext customContext = PinyinPro4J.createNewContext(
				StandardDictionary.COMPLETE,
				List.of(customPattern1));
		
		//Matches not at beginning
		match(stdContext, "关东军", "guāndōngjūn");
		match(customContext, "关东军", "guāndōngjūn");
		
		//Matches not at end
		match(stdContext, "闯关东", "chuǎngguāndōng");
		match(customContext, "闯关东", "chuǎngguāndōng");
		
		//Matches in the middle
		match(stdContext, "闯关东问", "chuǎngguāndōng wèn");
		match(customContext, "闯关东问", "chuǎng guāndōng wèn");
	}
	
	@Test
	public void testCustomDictionaryMatchAtEnd() {
		
		CustomPattern customPattern1 = new CustomPattern("关东", "guān dōng")
				.setProbability(0.12)
				.setAppliesAtStart(false)
				.setAppliesInMiddle(false)
				.setAppliesAtEnd(true);
		
		PinyinSegmentationContext customContext = PinyinPro4J.createNewContext(
				StandardDictionary.COMPLETE,
				List.of(customPattern1));
		
		//Matches not at beginning
		match(stdContext, "关东军", "guāndōngjūn");
		match(customContext, "关东军", "guāndōngjūn");
		
		//Matches at end
		match(stdContext, "闯关东", "chuǎngguāndōng");
		match(customContext, "闯关东", "chuǎng guāndōng");
		
		//Matches not in the middle
		match(stdContext, "闯关东问", "chuǎngguāndōng wèn");
		match(customContext, "闯关东问", "chuǎngguāndōng wèn");
	}
	
	protected String match(PinyinSegmentationContext context, String zh, String expectedPinyin) {
		String pinyin = PinyinPro4J.convertToPinyin(
				zh,
				new SegmentationOptions(),
				new PinyinFormatOptions(),
				context);
		assertThat(pinyin, is(expectedPinyin));
		
		return pinyin;
	}
	
}
