package com.aeb.pinyin.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link PinyinPro4J#getPinyinsForCharacter(String)} and {@link PinyinPro4J#getFirstPinyinForCharacter(String)} methods for single characters.
 */
public class SingleCharacterTest {
	
	@Test
	public void testGetPinyinsForCharacter() {
		List<String> pinyinsForCharacter = PinyinPro4J.getPinyinsForCharacter("乐");
		assertEquals(4, pinyinsForCharacter.size());
		assertEquals("lè", pinyinsForCharacter.get(0));
		assertEquals("yuè", pinyinsForCharacter.get(1));
		assertEquals("yào", pinyinsForCharacter.get(2));
		assertEquals("lào", pinyinsForCharacter.get(3));
		
		//Same possible pinyins as above, but different character
		List<String> pinyinsForCharacter2 = PinyinPro4J.getPinyinsForCharacter("樂");
		assertEquals(4, pinyinsForCharacter2.size());
		assertEquals("lè", pinyinsForCharacter2.get(0));
		assertEquals("yuè", pinyinsForCharacter2.get(1));
		assertEquals("yào", pinyinsForCharacter2.get(2));
		assertEquals("lào", pinyinsForCharacter2.get(3));
		
		//Character not found
		List<String> pinyinsForCharacter3 = PinyinPro4J.getPinyinsForCharacter("x");
		assertEquals(0, pinyinsForCharacter3.size());
	}
	
	@Test
	public void testGetFirstPinyinForCharacter() {
		assertEquals("lè", PinyinPro4J.getFirstPinyinForCharacter("乐"));
		assertEquals("lè", PinyinPro4J.getFirstPinyinForCharacter("樂"));
		assertEquals(null, PinyinPro4J.getFirstPinyinForCharacter("x"));
	}
	
	@Test
	public void testGetPinyinsForCharacterWithException() {
		assertThrows(IllegalArgumentException.class, () -> {
			PinyinPro4J.getPinyinsForCharacter("乐乐");
		});
	}
	
	@Test
	public void testGetAllPinyinSyllablesStandard() {
		Set<String> syllables = PinyinPro4J.getAllPinyinSyllables(PinyinToneMode.STANDARD);
		assertEquals(true, syllables.contains("lè"));
		assertEquals(true, syllables.contains("lǜ"));
		assertEquals(1392, syllables.size());
	}
	
	@Test
	public void testGetAllPinyinSyllablesNumber() {
		Set<String> syllables = PinyinPro4J.getAllPinyinSyllables(PinyinToneMode.NUMBERS);
		assertEquals(true, syllables.contains("le4"));
		assertEquals(true, syllables.contains("lü4"));
		assertEquals(1392, syllables.size());
	}
	
	@Test
	public void testGetAllPinyinSyllablesNumberAscii() {
		Set<String> syllables = PinyinPro4J.getAllPinyinSyllables(PinyinToneMode.NUMBERS_ASCII);
		assertEquals(true, syllables.contains("le4"));
		assertEquals(true, syllables.contains("lv4"));
		
		assertEquals(1388, syllables.size());
	}
	
	@Test
	public void testGetAllPinyinSyllablesNone() {
		Set<String> syllables = PinyinPro4J.getAllPinyinSyllables(PinyinToneMode.NONE);
		assertEquals(true, syllables.contains("le"));
		assertEquals(true, syllables.contains("lü"));
		assertEquals(416, syllables.size());
	}
	
	@Test
	public void testGetAllPinyinSyllablesNoneAscii() {
		Set<String> syllables = PinyinPro4J.getAllPinyinSyllables(PinyinToneMode.NONE_ASCII);
		assertEquals(true, syllables.contains("le"));
		assertEquals(true, syllables.contains("lv"));
		assertEquals(415, syllables.size());
	}
	
}
