package com.aeb.pinyin.impl.segmentation;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.aeb.pinyin.api.SegmentationOptions;

/**
 * Test the {@link AhoCorasickMatching} implemention with some basic sample patterns.
 */
public class AhoCorasickMatchingTest {
	
	@Test
	public void testMatch() {
		AhoCorasickMatching ac = getSampleAC();
		
		System.out.print(ac.root.toString(5, 20));
		
		List<MatchedPattern> matchResult = ac.match("abcaa", new SegmentationOptions());
		String actualMatchingResult = getPatternSignature(matchResult);
		assertEquals("0-a/0-ab/1-bc/1-bca/3-a/2-caa/4-a", actualMatchingResult);
		
		//Test match with unknown characters
		List<MatchedPattern> matchResult2 = ac.match("xbcybc", new SegmentationOptions());
		String actualMatchingResult2 = getPatternSignature(matchResult2);
		assertEquals("1-bc/4-bc", actualMatchingResult2);
	}
	
	@Test
	public void testSegmentize() {
		AhoCorasickMatching ac = getSampleAC();
		
		List<MatchedPattern> tokenizeResult = ac.segmentize("abcaa", new SegmentationOptions());
		String actualMatchingResult = getPatternSignature(tokenizeResult);
		assertEquals("0-ab/2-caa", actualMatchingResult);
		
		List<MatchedPattern> tokenizeResult2 = ac.segmentize("xayab", new SegmentationOptions());
		String actualMatchingResult2 = getPatternSignature(tokenizeResult2);
		assertEquals("0-x/1-a/2-y/3-ab", actualMatchingResult2);
		
	}
	
	@Test
	public void testMatch2() {
		AhoCorasickMatching ac = new AhoCorasickMatching(
				Arrays.asList(
						new Pattern("ab", "ab", Priority.Normal, 0.1, null),
						new Pattern("a", "a", Priority.Normal, 0.1, null),
						new Pattern("b", "b", Priority.Normal, 0.1, null)));
		
		List<MatchedPattern> tokenizeResult = ac.match("ab", new SegmentationOptions());
		String actualMatchingResult = getPatternSignature(tokenizeResult);
		assertEquals("0-a/0-ab/1-b", actualMatchingResult);
		
	}
	
	@Test
	public void testMatchCharsWichAreOnlyPrefix() {
		AhoCorasickMatching ac = new AhoCorasickMatching(
				Arrays.asList(
						new Pattern("abc", "abc", Priority.Normal, 0.1, null)));
		
		List<MatchedPattern> tokenizeResult = ac.match("ab", new SegmentationOptions());
		String actualMatchingResult = getPatternSignature(tokenizeResult);
		assertEquals("", actualMatchingResult);
		
	}
	
	@Test
	public void testSegmentizeCharsWichAreOnlyPrefix() {
		AhoCorasickMatching ac = new AhoCorasickMatching(
				Arrays.asList(
						new Pattern("abc", "abc", Priority.Normal, 0.1, null)));
		
		List<MatchedPattern> tokenizeResult = ac.segmentize("ab", new SegmentationOptions());
		String actualMatchingResult = getPatternSignature(tokenizeResult);
		assertEquals("0-a/1-b", actualMatchingResult);
	}
	
	@Test
	public void testSegmentizeEmptyString() {
		AhoCorasickMatching ac = new AhoCorasickMatching(
				Arrays.asList(
						new Pattern("abc", "abc", Priority.Normal, 0.1, null)));
		
		List<MatchedPattern> tokenizeResult = ac.segmentize("", new SegmentationOptions());
		String actualMatchingResult = getPatternSignature(tokenizeResult);
		assertEquals("", actualMatchingResult);
	}
	
	private AhoCorasickMatching getSampleAC() {
		//The example below is taken from https://cp-algorithms.com/string/aho_corasick.html
		return new AhoCorasickMatching(
				Arrays.asList(
						new Pattern("a", "a", Priority.Normal, 0.1, null),
						new Pattern("ab", "ab", Priority.Normal, 0.10, null),
						new Pattern("bc", "bc", Priority.Normal, 0.11, null),
						new Pattern("bca", "bca", Priority.Normal, 0.1, null),
						new Pattern("caa", "caa", Priority.Normal, 0.1, null)));
	}
	
	private String getPatternSignature(List<MatchedPattern> matchResult) {
		return matchResult.stream()
				.map(mp -> mp.index + "-" + mp.pattern.zh)
				.collect(joining("/"));
	}
	
}
