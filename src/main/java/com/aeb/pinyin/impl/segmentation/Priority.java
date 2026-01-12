package com.aeb.pinyin.impl.segmentation;

import java.util.regex.Pattern;

/**
 * Priority levels for {@link Pattern}s.<br>
 * Used to determine which pattern should be preferred for the same sequence of chinese characters,
 * when multiple patterns match the same text.<br>
 * The Priority with the highest ordinal value has the highest priority.<br>
 */
public enum Priority {
	
	Normal,
	Surname,
	Custom;

}
