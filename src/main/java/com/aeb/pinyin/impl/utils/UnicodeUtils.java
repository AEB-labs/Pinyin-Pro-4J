package com.aeb.pinyin.impl.utils;

public class UnicodeUtils {
	
	/**
	 * @return The number of unicode points in the String.
	 *         Same as String.codePointCount(0,s.length()) in Java.
	 */
	public static int stringLength(String s) {
		return s.codePointCount(0, s.length());
	}
	
	/**
	 * @return The String splitted in its unicode points.
	 */
	public static String[] splitString(String s) {
		return s.codePoints().mapToObj(Character::toString).toArray(String[]::new);
	}
	
	public static boolean isDoubleUnicodePrefixChar(char c) {
		return c >= '\uD800' && c <= '\uDBFF';
	}
	
	public static boolean isDoubleUnicodeSuffixChar(char c) {
		return c >= '\uDC00' && c <= '\uDFFF';
	}
	
}
