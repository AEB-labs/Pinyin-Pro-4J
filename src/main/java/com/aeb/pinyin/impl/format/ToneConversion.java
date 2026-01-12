package com.aeb.pinyin.impl.format;

import java.util.HashMap;
import java.util.Map;

import com.aeb.pinyin.api.PinyinToneMode;
import com.aeb.pinyin.impl.utils.UnicodeUtils;

/**
 * Conversion utility for Pinyin tone representations.
 */
public class ToneConversion {
	
	private static final String A_DATA = "a ā á ǎ à";
	private static final String E_DATA = "e ē é ě è";
	private static final String I_DATA = "i ī í ǐ ì";
	private static final String O_DATA = "o ō ó ǒ ò";
	private static final String U_DATA = "u ū ú ǔ ù";
	private static final String UE_DATA = "ü ǖ ǘ ǚ ǜ";
	private static final String E_CIRCONFLEX_DATA = "ê ê̄ ế ê̌ ề";
	private static final String N_DATA = "n n̄ ń ň ǹ";
	private static final String M_DATA = "m m̄ ḿ m̌ m̀";
	
	private static final Map<String, String> toneNumberMap = createToneNumberMap();
	
	private static final Map<String, String> toneRemovedMap = createToneRemovedMap();
	
	private static final Map<String, String> toneRemovedAsciiMap = createToneRemovedAsciiMap();
	
	private static HashMap<String, String> createToneNumberMap() {
		HashMap<String, String> map = new HashMap<>();
		addToneNumberMapEntries(map, A_DATA);
		addToneNumberMapEntries(map, E_DATA);
		addToneNumberMapEntries(map, I_DATA);
		addToneNumberMapEntries(map, O_DATA);
		addToneNumberMapEntries(map, U_DATA);
		addToneNumberMapEntries(map, UE_DATA);
		addToneNumberMapEntries(map, E_CIRCONFLEX_DATA);
		addToneNumberMapEntries(map, N_DATA);
		addToneNumberMapEntries(map, M_DATA);
		return map;
	}
	
	private static void addToneNumberMapEntries(HashMap<String, String> map, String data) {
		String[] split = data.split(" ");
		for (int i = 1; i <= 4; i++) {
			map.put(split[i], String.valueOf(i));
		}
	}
	
	private static HashMap<String, String> createToneRemovedMap() {
		HashMap<String, String> map = new HashMap<>();
		addToneRemovedMapEntries(map, A_DATA);
		addToneRemovedMapEntries(map, E_DATA);
		addToneRemovedMapEntries(map, I_DATA);
		addToneRemovedMapEntries(map, O_DATA);
		addToneRemovedMapEntries(map, U_DATA);
		addToneRemovedMapEntries(map, UE_DATA);
		addToneRemovedMapEntries(map, E_CIRCONFLEX_DATA);
		addToneRemovedMapEntries(map, N_DATA);
		addToneRemovedMapEntries(map, M_DATA);
		return map;
	}
	
	private static void addToneRemovedMapEntries(HashMap<String, String> map, String data) {
		String[] split = data.split(" ");
		for (int i = 1; i <= 4; i++) {
			map.put(split[i], split[0]);
		}
	}
	
	private static HashMap<String, String> createToneRemovedAsciiMap() {
		HashMap<String, String> map = new HashMap<>();
		for (var entry : toneRemovedMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if ("ü".equals(value)) {
				value = "v";
			}
			if ("ê".equals(value)) {
				value = "e";
			}
			map.put(key, value);
		}
		return map;
	}
	
	/**
	 * Convert one pinyin syllable according to the given tone mode.
	 * @param pinyinSyllable A single pinyin syllable in standard format (with tone marks)
	 * @param mode The target tone mode
	 * @return The converted pinyin syllable
	 */
	public static String convertPinyinSyllable(String pinyinSyllable, PinyinToneMode mode) {
		if (mode == PinyinToneMode.STANDARD) {
			return pinyinSyllable;
		}
		
		String toneNumberResult = null;
		Map<String, String> convertMap = switch (mode) {
			case NONE, NUMBERS -> toneRemovedMap;
			case NONE_ASCII, NUMBERS_ASCII -> toneRemovedAsciiMap;
			default -> throw new IllegalArgumentException("Unknown PinyinToneMode: " + mode);
		};
		StringBuilder sb = new StringBuilder();
		final String[] splits = UnicodeUtils.splitString(pinyinSyllable);
		for (int i = 0; i < splits.length; i++) {
			String c = splits[i];
			String cPlus = (i + 1 < splits.length) ? c + splits[i + 1] : null;
			String convertedChar = cPlus == null ? null : convertMap.get(cPlus);
			String toneNumber = cPlus == null ? null : toneNumberMap.get(cPlus);
			if (convertedChar != null) {
				//2 char sequence found (e.g. ê̄)
				i++; //NOSONAR
			} else {
				convertedChar = convertMap.get(c);
				toneNumber = toneNumberMap.get(c);
				if (convertedChar == null) {
					convertedChar = c;
				}
			}
			
			sb.append(convertedChar);
			if (toneNumber != null && toneNumberResult == null) {
				//First found tone number will win (should be only one per syllable)
				toneNumberResult = toneNumber;
			}
		}
		if (toneNumberResult != null && (mode == PinyinToneMode.NUMBERS || mode == PinyinToneMode.NUMBERS_ASCII)) {
			sb.append(toneNumberResult);
		}
		
		return sb.toString();
	}
	
}
