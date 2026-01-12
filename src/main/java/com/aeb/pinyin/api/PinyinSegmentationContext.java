package com.aeb.pinyin.api;

import java.util.List;

import com.aeb.pinyin.impl.segmentation.AhoCorasickMatching;
import com.aeb.pinyin.impl.segmentation.MatchedPattern;
import com.aeb.pinyin.impl.segmentation.SegmentationPostProcessing;

/**
 * A context for providing pinyin segementations.<br>
 * 
 * Creating a context is an expensive operation, so reuse a context whenever
 * possible.<br>
 * 
 */
public class PinyinSegmentationContext {
	
	protected PinyinSegmentationContext(AhoCorasickMatching ac) {
		this.ac = ac;
	}
	
	protected final AhoCorasickMatching ac;
	
	/**
	 * Segmentation is the first step to convert Chinese text to Pinyin.<br>
	 * @param text The text to segmentize.
	 * @param options Segmentation options.
	 * @return A list of {@link Segment}s containing the matched patterns.
	 */
	protected List<Segment> segmentizeText(String text, SegmentationOptions options) {
		List<MatchedPattern> rawSegments = ac.segmentize(text, options);
		return new SegmentationPostProcessing().processSegments(text, rawSegments);
	}
	
}
