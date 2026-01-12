package com.aeb.pinyin.impl.segmentation;

import java.util.List;

import com.aeb.pinyin.api.Segment;
import com.aeb.pinyin.impl.single.SingleCharacterPinyin;
import com.aeb.pinyin.impl.utils.UnicodeUtils;

/**
 * Class for post-processing of results of segmentation as returned by {@link MaxProbabilitySegmentator}.<br>
 */
public class SegmentationPostProcessing {
	
	/**
	 * Postprocessing consists of the following steps:<br>
	 * - Handle special CJK characters (e.g. reduplication or tone marks).<br>
	 * - Merge consecutive single-character segments which are non-CJK, i.e. have no Pinyin translation.<br>
	 * - Return a list of {@link Segment}s representing the final segmentation result.
	 * @param text The original text.
	 * @param matchedPatterns The matched patterns to be post-processed.
	 * @return The post-processed segments.
	 */
	public List<Segment> processSegments(String text, List<MatchedPattern> matchedPatterns) {
		String[] textChars = UnicodeUtils.splitString(text);
		List<Segment> result = new java.util.ArrayList<>();
		for (MatchedPattern match : matchedPatterns) {
			// Check for single-character segments which are either special CJK characters or non-CJK characters
			if (match.pattern.length == 1 && match.pattern.priority.ordinal() <= Priority.Normal.ordinal()) {
				String pinyin = processSpecialCJKCharacters(textChars, match.index, match.index + 1);
				Segment seg = new Segment(
						match.pattern.zh,
						match.index,
						!pinyin.equals(match.pattern.zh),
						pinyin);
				result.add(seg);
			} else {
				// Regular segment
				Segment seg = new Segment(
						match.pattern.zh,
						match.index,
						true,
						match.pattern.pinyin);
				result.add(seg);
			}
		}
		return mergeNonCJKSegments(result);
	}
	
	private List<Segment> mergeNonCJKSegments(List<Segment> segements) {
		List<Segment> result = new java.util.ArrayList<>();
		int index = 0;
		while (index < segements.size()) {
			Segment seg = segements.get(index);
			if (!seg.isZh) {
				//Merge all following non-CJK segments
				StringBuilder sbOrigin = new StringBuilder(seg.origin);
				StringBuilder sbPinyin = new StringBuilder(seg.pinyin);
				int nextIndex = index + 1;
				while (nextIndex < segements.size() && !segements.get(nextIndex).isZh) {
					sbOrigin.append(segements.get(nextIndex).origin);
					sbPinyin.append(segements.get(nextIndex).pinyin);
					nextIndex++;
				}
				Segment mergedSeg = new Segment(
						sbOrigin.toString(),
						seg.index,
						false,
						sbPinyin.toString());
				result.add(mergedSeg);
				index = nextIndex;
			} else {
				result.add(seg);
				index++;
			}
		}
		return result;
	}
	
	private String processSpecialCJKCharacters(String[] codePoints, int indexFrom, int indexToExclusive) {
		StringBuilder sb = new StringBuilder();
		for (int i = indexFrom; i < indexToExclusive; i++) {
			String codePoint = codePoints[i];
			String previousCodePoint = (i > 0) ? codePoints[i - 1] : null;
			String nextCodePoint = (i < codePoints.length - 1) ? codePoints[i + 1] : null;
			sb.append(processSpecialCJKCharacter(codePoint, previousCodePoint, nextCodePoint));
		}
		return sb.toString();
	}
	
	private String processSpecialCJKCharacter(String codePoint, String previousCodePoint, String nextCodePoint) {
		StringBuilder sb = new StringBuilder();
		if (processReduplicationCharacter(codePoint, previousCodePoint, sb)) {
			return sb.toString();
		}
		if (processToneSandhiLiao(codePoint, previousCodePoint, sb)) {
			return sb.toString();
		}
		if (processToneSandhi(codePoint, previousCodePoint, nextCodePoint, sb)) {
			return sb.toString();
		}
		
		return getSingleCharacterPinyin(codePoint);
	}
	
	/**
	 * Handling reduplication with the character "々"
	 */
	private boolean processReduplicationCharacter(String codePoint, String prev, StringBuilder sb) {
		if ("々".equals(codePoint)) {
			String prevPinyin = SingleCharacterPinyin.getFirstPinyinForCharacter(prev);
			if (prevPinyin == null) {
				sb.append("tóng");
			} else {
				sb.append(prevPinyin);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Handling tone sandhi of the character "了"
	 */
	private boolean processToneSandhiLiao(String codePoint, String prev, StringBuilder sb) {
		if ("了".equals(codePoint)) {
			String prevPinyin = SingleCharacterPinyin.getFirstPinyinForCharacter(prev);
			if (prevPinyin == null) {
				sb.append("liǎo");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Handling tone sandhi of the characters “一” and “不”.
	 * NOT IMPLEMENTED:
	 * Currently this method does not implement tone sandhi rules, for reasons see e.g. 
	 * https://resources.allsetlearning.com/chinese/pronunciation/Tone_changes_for_%22bu%22
	 * which says, that tone changes are normally not written in pinyin transcriptions.
	 */
	@SuppressWarnings("unused")
	private boolean processToneSandhi(String codePoint, String prev, String next, StringBuilder sb) {
		return false;
		
		//This would be the original javascript code from pinyin-pro:
		
		///**
		// * @description: 连续变调处理：https://zh.wiktionary.org/wiki/Appendix:%E2%80%9C%E4%B8%80%E2%80%9D%E5%8F%8A%E2%80%9C%E4%B8%8D%E2%80%9D%E7%9A%84%E5%8F%98%E8%B0%83
		// */
		//const toneSandhiMap = {
		//  // 说不说，说一说，叠词之间发音为轻声
		//  不: {
		//    bú: [4], // "不" 后面跟 4 声时，变调为 2 声
		//  },
		//  一: {
		//    yí: [4], // "一" 后面跟 4 声时，变调为 2 声
		//    yì: [1, 2, 3],
		//  },
		//};
		//
		//const toneSandhiIgnoreSuffix = {
		//  不: ['的', '而', '之', '后', '也', '还', '地'],
		//  一: ['的', '而', '之', '后', '也', '还', '是'],
		//};
		//export const toneSandhiList = Object.keys(toneSandhiMap);
		//
		//// 处理「一」和 「不」字的变调
		//export function processToneSandhi(cur: string, pre: string, next: string) {
		//  if (toneSandhiList.indexOf(cur) === -1) {
		//    return getSingleWordPinyin(cur);
		//  }
		//  // 轻声变调：说不说，说一说，叠词之间发音为轻声
		//  if (pre === next && pre && getSingleWordPinyin(pre) !== pre) {
		//    return getPinyinWithoutTone(getSingleWordPinyin(cur));
		//  }
		//  // 「一」和 「不」字变调处理
		//  if (
		//    next &&
		//    !toneSandhiIgnoreSuffix[
		//      cur as keyof typeof toneSandhiIgnoreSuffix
		//    ].includes(next)
		//  ) {
		//    const nextPinyin = getSingleWordPinyin(next);
		//    if (nextPinyin !== next) {
		//      const nextTone = getNumOfTone(nextPinyin);
		//      const pinyinMap = toneSandhiMap[cur as keyof typeof toneSandhiMap];
		//      for (let pinyin in pinyinMap) {
		//        const tones = pinyinMap[pinyin as keyof typeof pinyinMap] as number[];
		//        if (tones.indexOf(Number(nextTone)) !== -1) {
		//          return pinyin;
		//        }
		//      }
		//    }
		//  }
		//}
		
	}
	
	/**
	 * Returns the single character Pinyin for the codePoint, or, if not found, the codePoint itself.
	 */
	private String getSingleCharacterPinyin(String codePoint) {
		String singlePinyin = SingleCharacterPinyin.getFirstPinyinForCharacter(codePoint);
		if (singlePinyin != null) {
			return singlePinyin;
		}
		return codePoint;
	}
	
}
