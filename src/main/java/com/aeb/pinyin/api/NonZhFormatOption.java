package com.aeb.pinyin.api;

public enum NonZhFormatOption {
	
	/**
	 * Keep non-Chinese segments as they are.
	 */
	KEEP,
	
	/**
	 * Remove non-Chinese segments from the output.
	 */
	REMOVE,
	
	/**
	 * Trim leading and trailing characters equal to the segment separator from non-Chinese segments.
	 * E.g. if the segment separator is a space, this trims all leading and trailing spaces of a non-chinese segment,
	 * thereby avoiding duplicate spaces as separation between a chinese and non-chinese segments.<br>
	 * Example: "变调 ab 变调" with segment separator " " becomes "biàn diào ab biàn diào" instead of "biàn diào &nbsp;ab &nbsp;biàn diào".<br>	
	
	 */
	TRIM;
	
}
