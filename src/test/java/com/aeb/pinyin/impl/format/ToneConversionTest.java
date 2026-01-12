package com.aeb.pinyin.impl.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.aeb.pinyin.api.PinyinToneMode;

public class ToneConversionTest {
	
	public void testStandard() {
		assertEquals(
				"jiào",
				ToneConversion.convertPinyinSyllable("jiào", PinyinToneMode.STANDARD));
	}
	
	public static Stream<Arguments> conversionSamples() {
		return Stream.of(
				
				//Tones on a
				data("jiao", "jiao", "jiao", "jiao", "jiao"),
				data("jiāo", "jiao1", "jiao1", "jiao", "jiao"),
				data("jiáo", "jiao2", "jiao2", "jiao", "jiao"),
				data("jiǎo", "jiao3", "jiao3", "jiao", "jiao"),
				data("jiào", "jiao4", "jiao4", "jiao", "jiao"),
				
				//Tones on e
				data("zēng", "zeng1", "zeng1", "zeng", "zeng"),
				data("zéng", "zeng2", "zeng2", "zeng", "zeng"),
				data("zěng", "zeng3", "zeng3", "zeng", "zeng"),
				data("zèng", "zeng4", "zeng4", "zeng", "zeng"),
				
				//Tones on i
				data("ī", "i1", "i1", "i", "i"),
				data("í", "i2", "i2", "i", "i"),
				data("ǐ", "i3", "i3", "i", "i"),
				data("ì", "i4", "i4", "i", "i"),
				
				//Tones on o
				data("ō", "o1", "o1", "o", "o"),
				data("ó", "o2", "o2", "o", "o"),
				data("ǒ", "o3", "o3", "o", "o"),
				data("ò", "o4", "o4", "o", "o"),
				
				//Tones on u
				data("ū", "u1", "u1", "u", "u"),
				data("ú", "u2", "u2", "u", "u"),
				data("ǔ", "u3", "u3", "u", "u"),
				data("ù", "u4", "u4", "u", "u"),
				
				//Tones on ü
				data("ǖ", "ü1", "v1", "ü", "v"),
				data("ǘ", "ü2", "v2", "ü", "v"),
				data("ǚ", "ü3", "v3", "ü", "v"),
				data("ǜ", "ü4", "v4", "ü", "v"),
				
				//Tones on ê
				data("ê̄", "ê1", "e1", "ê", "e"),
				data("ế", "ê2", "e2", "ê", "e"),
				data("ê̌", "ê3", "e3", "ê", "e"),
				data("ề", "ê4", "e4", "ê", "e"),
				
				//Tones on n
				data("n̄", "n1", "n1", "n", "n"),
				data("ń", "n2", "n2", "n", "n"),
				data("ň", "n3", "n3", "n", "n"),
				data("ǹ", "n4", "n4", "n", "n"),
				
				//Tones on m
				data("m̄", "m1", "m1", "m", "m"),
				data("ḿ", "m2", "m2", "m", "m"),
				data("m̌", "m3", "m3", "m", "m"),
				data("m̀", "m4", "m4", "m", "m")
		
		//
		);
	}
	
	@ParameterizedTest
	@MethodSource("conversionSamples")
	public void conversionSamples(
			String pinyin,
			String expectedNumbers,
			String expectedNumbersAscii,
			String expectedNone,
			String expectedNoneAscii) {
		assertEquals(
				expectedNumbers,
				ToneConversion.convertPinyinSyllable(pinyin, PinyinToneMode.NUMBERS));
		assertEquals(
				expectedNumbersAscii,
				ToneConversion.convertPinyinSyllable(pinyin, PinyinToneMode.NUMBERS_ASCII));
		assertEquals(
				expectedNone,
				ToneConversion.convertPinyinSyllable(pinyin, PinyinToneMode.NONE));
		assertEquals(
				expectedNoneAscii,
				ToneConversion.convertPinyinSyllable(pinyin, PinyinToneMode.NONE_ASCII));
	}
	
	private static Arguments data(
			String pinyin,
			String expectedNumbers,
			String expectedNumbersAscii,
			String expectedNone,
			String expectedNoneAscii) {
		return Arguments.of(
				pinyin,
				expectedNumbers,
				expectedNumbersAscii,
				expectedNone,
				expectedNoneAscii);
	}
	
}
