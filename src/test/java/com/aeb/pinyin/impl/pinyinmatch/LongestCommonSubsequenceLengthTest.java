package com.aeb.pinyin.impl.pinyinmatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.aeb.pinyin.testutil.LongestCommonSubsequenceLength;

/**
 * Test {@link LongestCommonSubsequenceLength}.
 */
public class LongestCommonSubsequenceLengthTest {
	
	@Test
	public void test() {
		check(
				List.of(),
				List.of(),
				0);
		check(
				List.of("A", "B"),
				List.of(),
				0);
		check(
				List.of("A", "B", "C", "D"),
				List.of("A", "B", "C", "D"),
				4);
		check(
				List.of("A", "B", "C", "D"),
				List.of("A"),
				1);
		check(
				List.of("A", "B", "C", "D"),
				List.of("C", "D"),
				2);
		
		check(
				List.of("A", "B", "C", "D", "E"),
				List.of("B", "X", "D", "Y", "E"),
				3);
	}
	
	private void check(List<String> list1, List<String> list2, int expected) {
		int computedLcsLength = new LongestCommonSubsequenceLength<>(
				list1,
				list2,
				(a, b) -> a.equals(b)).computeLongestCommonSubsequenceLength();
		assertEquals(expected, computedLcsLength);
		int computedLcsLength2 = new LongestCommonSubsequenceLength<>(
				list2,
				list1,
				(a, b) -> a.equals(b)).computeLongestCommonSubsequenceLength();
		assertEquals(expected, computedLcsLength2);
	}
	
}
