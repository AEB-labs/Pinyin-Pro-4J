package com.aeb.pinyin.api;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link PinyinPro4J#convertToPinyin(String, SegmentationOptions, PinyinFormatOptions, PinyinSegmentationContext)}.
 */
public class ConvertToPinyinTest {
	
	private static final PinyinFormatOptions standard = new PinyinFormatOptions();
	
	private static final PinyinFormatOptions toneMarksNone = new PinyinFormatOptions()
			.setToneMode(PinyinToneMode.NONE);
	private static final PinyinFormatOptions toneMarksNoneAscii = new PinyinFormatOptions()
			.setToneMode(PinyinToneMode.NONE_ASCII);
	private static final PinyinFormatOptions toneMarksNumbers = new PinyinFormatOptions()
			.setToneMode(PinyinToneMode.NUMBERS);
	private static final PinyinFormatOptions toneMarksNumbersAscii = new PinyinFormatOptions()
			.setToneMode(PinyinToneMode.NUMBERS_ASCII);
	
	private static final PinyinFormatOptions syllablableSepartorDash = new PinyinFormatOptions()
			.setSyllableSeparator("-");
	
	private static final PinyinFormatOptions segmentSepartorDash = new PinyinFormatOptions()
			.setSegmentSeparator("-");
	
	private static final PinyinFormatOptions keepNonZh = new PinyinFormatOptions()
			.setNonZhOption(NonZhFormatOption.KEEP);
	
	private static final PinyinFormatOptions removeNonZh = new PinyinFormatOptions()
			.setNonZhOption(NonZhFormatOption.REMOVE);
	
	public static Stream<Arguments> dataSamples() {
		return Stream.of(
				data("", "", standard),
				
				//Standard tones with different separators
				data("阿比让习", "ābǐràng xí", standard),
				data("阿比让习", "ābǐràng-xí", segmentSepartorDash),
				data("阿比让习", "ā-bǐ-ràng xí", syllablableSepartorDash),
				
				//Non-Chinese characters handling
				data("阿比让 xy 习", "ābǐràng xy xí", standard),
				data("阿比让 xy 习", "ābǐràng  xy  xí", keepNonZh),
				data("阿比让 xy 习", "ābǐràng xí", removeNonZh),
				
				//Different tone mark options
				data("閭", "lǘ", standard),
				data("閭", "lü", toneMarksNone),
				data("閭", "lv", toneMarksNoneAscii),
				data("閭", "lü2", toneMarksNumbers),
				data("閭", "lv2", toneMarksNumbersAscii)
		
		//
		);
	}
	
	public PinyinSegmentationContext completeContext = TestContextProvider.getCompleteContext();
	
	@ParameterizedTest
	@MethodSource("dataSamples")
	public void testConvertToPinyin(String origin, String expectedConversionResult, PinyinFormatOptions options) {
		String actualResult = PinyinPro4J.convertToPinyin(
				origin,
				new SegmentationOptions(),
				options,
				completeContext);
		Assertions.assertEquals(expectedConversionResult, actualResult);
	}
	
	private static Arguments data(String origin, String expectedConversionResult, PinyinFormatOptions options) {
		return Arguments.of(origin, expectedConversionResult, options);
	}
	
}
