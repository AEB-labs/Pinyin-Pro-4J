package com.aeb.pinyin.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.aeb.pinyin.impl.data.JsonReader;
import com.aeb.pinyin.impl.segmentation.AhoCorasickMatching;
import com.aeb.pinyin.impl.segmentation.Pattern;
import com.aeb.pinyin.impl.segmentation.Priority;
import com.aeb.pinyin.impl.segmentation.Probability;

/**
 * A builder for a {@link PinyinSegmentationContext} with some protected intermediate
 * variables for better unit testing.
 */
class PinyinSegmentationContextBuilder {
	
	PinyinSegmentationContextBuilder(StandardDictionary standardDictionary) {
		super();
		this.standardDictionary = standardDictionary;
	}
	
	final StandardDictionary standardDictionary;
	
	Map<String, String> dict2;
	Map<String, String> dict3;
	Map<String, String> dict4;
	Map<String, String> dict5;
	Map<String, String> dictNumbers;
	Map<String, String> dictSurnames;
	
	List<Pattern> standardDictionaryPatterns;
	
	List<Pattern> allPatterns = new ArrayList<>();
	
	AhoCorasickMatching ac;
	
	PinyinSegmentationContext build() {
		readSimpleDict("dict5.json", m -> dict5 = m, Priority.Normal, Probability.Dict);
		readSimpleDict("dict4.json", m -> dict4 = m, Priority.Normal, Probability.Dict);
		readSimpleDict("dict3.json", m -> dict3 = m, Priority.Normal, Probability.Dict);
		readSimpleDict("dict2.json", m -> dict2 = m, Priority.Normal, Probability.Dict);
		readSimpleDict("numbers.json", m -> dictNumbers = m, Priority.Normal, Probability.Rule);
		readSimpleDict("surnames.json", m -> dictSurnames = m, Priority.Surname, Probability.Surname);
		
		readAdditionalDictionaryPatterns();
		
		ac = new AhoCorasickMatching(allPatterns);
		return new PinyinSegmentationContext(ac);
	}
	
	private void readAdditionalDictionaryPatterns() {
		if (standardDictionary == null || standardDictionary.resourceName == null) {
			standardDictionaryPatterns = new ArrayList<>();
			return;
		}
		standardDictionaryPatterns = new JsonReader().readPatternDictionary(
				JsonReader.class,
				standardDictionary.resourceName,
				Priority.Normal);
		allPatterns.addAll(standardDictionaryPatterns);
	}
	
	private void readSimpleDict(
			String resourceName,
			Consumer<Map<String, String>> mapSetter,
			Priority priority,
			Probability probability) {
		Map<String, String> dict = new JsonReader().readSimpleDictionary(JsonReader.class, resourceName);
		mapSetter.accept(dict);
		List<Pattern> dictAsPatternList = convertSimpleDictionaryAsPatternList(dict, priority, probability);
		allPatterns.addAll(dictAsPatternList);
	}
	
	private List<Pattern> convertSimpleDictionaryAsPatternList(
			Map<String, String> simpleDictionary,
			Priority priority,
			Probability probability) {
		return simpleDictionary
				.entrySet()
				.stream()
				.map(
						entry -> new Pattern(
								entry.getKey(),
								entry.getValue(),
								priority,
								probability.getProbability(entry.getKey()),
								null))
				.toList();
	}
	
}