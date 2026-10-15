package com.aeb.pinyin.impl.segmentation;

import com.aeb.pinyin.api.CustomPattern;
import com.aeb.pinyin.api.SegmentationOptions;
import com.aeb.pinyin.api.SurnameMode;

/**
 * Priority levels for {@link Pattern}s.<br>
 * Used to determine which pattern should be preferred for the same sequence of chinese characters,
 * when multiple patterns match the same text.<br>
 * The Priority with the highest ordinal value has the highest priority.<br>
 * Additionally, a pattern may be excluded from matching at the beginning, in the middle or at the end.
 */
public class Priority {
	
	public static final Priority Normal = new Priority(0);
	public static final Priority Surname = new Priority(1) {
		
		@Override
		public boolean applies(SegmentationOptions options, boolean atStart, boolean atEnd) {
			return switch (options.surnameMode) {
				case SurnameMode.ON -> true;
				case SurnameMode.OFF -> false;
				case SurnameMode.HEAD -> atStart;
			};
		}
		
	};
	
	public static final Priority newCustomPriority(CustomPattern customPattern) {
		return new Priority(
				3,
				customPattern.isAppliesAtStart(),
				customPattern.isAppliesInMiddle(),
				customPattern.isAppliesAtEnd());
	}
	
	private final int ordinal;
	private final boolean appliesAtStart;
	private final boolean appliesAtEnd;
	private final boolean appliesInMiddle;
	
	public Priority(int ordinal) {
		this(ordinal, true, true, true);
	}
	
	public Priority(int ordinal, boolean appliesAtStart, boolean appliesInMiddle, boolean appliesAtEnd) {
		super();
		this.ordinal = ordinal;
		this.appliesAtStart = appliesAtStart;
		this.appliesInMiddle = appliesInMiddle;
		this.appliesAtEnd = appliesAtEnd;
	}
	
	public int ordinal() {
		return ordinal;
	}
	
	@SuppressWarnings("unused")
	public boolean applies(SegmentationOptions options, boolean isAtStart, boolean isAtEnd) {
		if (isAtStart && this.appliesAtStart) {
			return true;
		}
		if (isAtEnd && this.appliesAtEnd) {
			return true;
		}
		if (!isAtStart && !isAtEnd && this.appliesInMiddle) {
			return true;
		}
		return false;
	}
	
}
