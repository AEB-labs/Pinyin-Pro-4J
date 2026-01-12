package com.aeb.pinyin.api;

public class TestContextProvider {
	
	private static PinyinSegmentationContext completeContext;
	
	public static synchronized PinyinSegmentationContext getCompleteContext() {
		if (completeContext == null) {
			completeContext = PinyinPro4J.createNewContext(StandardDictionary.COMPLETE);
		}
		return completeContext;
	}
	
}
