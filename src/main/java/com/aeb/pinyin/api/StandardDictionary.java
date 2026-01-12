package com.aeb.pinyin.api;

public enum StandardDictionary {
	
	/**
	 * Very basic buit-in dictionary with smallest memory footprint.
	 */
	BUILTIN(null),
	
	/**
	 * Medium sized dictionary.
	 */
	MODERN_CHINESE("modern.json"),
	
	/**
	 * Most complete dictionary available from pinyin-pro
	 */
	COMPLETE("complete.json");
	
	StandardDictionary(String resourceName) {
		this.resourceName = resourceName;
	}
	
	public final String resourceName;
	
}
