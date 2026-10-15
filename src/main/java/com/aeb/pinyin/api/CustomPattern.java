package com.aeb.pinyin.api;

/**
 * A list of {@link CustomPattern} can improve the pinyin segmentation for special kinds of
 * texts, e.g. for company names or addresses.<br><br>
 * 
 * Probabilities of custom patterns:<br><br>
 * 
 * The main use case for {@link CustomPattern}s is that in certain types of text, the probability of a word contained in a {@link StandardDictionary} is
 * much higher than usual. In this case the Chinese text in a {@link CustomPattern} is identical to a Chinese text in 
 * a {@link StandardDictionary} and the custom pattern will be taken, i.e. their probability will be taken, instead of the probability 
 * from the {@link StandardDictionary}. The default way to do this is to set the probability of a {@link CustomPattern} to
 * 0.1 + (0.01 * len), which is higher than any probability from a {@link StandardDictionary} and which also guarantees, that longer
 * {@link CustomPattern}s will be preferred to smaller {@link CustomPattern}s.<br><br>
 * 
 * Another use case is, that {@link CustomPattern}s amend words, which are not contained in a {@link StandardDictionary},
 * though the {@link StandardDictionary#COMPLETE} is very exhaustive. In this case it is important, that the probability 
 * of such a word is set to values comparable with other tokens in the {@link StandardDictionary}.<br><br> 
 * 
 */
public class CustomPattern {
	
	/**
	 * Create a custom pattern with standard probability 0.1 + (0.01 * len) and applicable everywhere in the text.<br>
	 * See also the class comment.
	 * @param zh The chinese word, e.g. 关东
	 * @param pinyin The pinyin translation of the chinese word with tone marks, e.g. "guān dōng"
	 *        Every pinyin syllable, representing one chinese character should be separated by blank from the next.
	 */
	public CustomPattern(String zh, String pinyin) {
		super();
		this.zh = zh;
		this.pinyin = pinyin;
		this.probability = 0.1 + (0.01 * zh.codePointCount(0, zh.length()));
	}
	
	/**
	 * Orignal chinese word.
	 */
	private final String zh;
	
	/**
	 * Pinyin translation.
	 */
	private final String pinyin;
	
	/**
	 * Word frequency probability.
	 * In case of more than one dictionary having the the same chinese text,
	 * this is the second sort criterion (after priority).
	 */
	private double probability;
	
	private boolean appliesAtStart = true;
	private boolean appliesAtEnd = true;
	private boolean appliesInMiddle = true;
	
	/**
	 * See class comment.
	 */
	public double getProbability() {
		return probability;
	}
	
	/**
	 * See class comment.
	 */
	public CustomPattern setProbability(double probability) {
		this.probability = probability;
		return this;
	}
	
	/**
	 * True, if the {@link CustomPattern} can be applied at the start of the text.
	 * Default is true.
	 */
	public boolean isAppliesAtStart() {
		return appliesAtStart;
	}
	
	/**
	 * True, if the {@link CustomPattern} can be applied at the start of the text.
	 * Default is true.
	 */
	public CustomPattern setAppliesAtStart(boolean appliesAtStart) {
		this.appliesAtStart = appliesAtStart;
		return this;
	}
	
	/**
	 * True, if the {@link CustomPattern} can be applied at the end of the text.
	 * Default is true.
	 */
	public boolean isAppliesAtEnd() {
		return appliesAtEnd;
	}
	
	/**
	 * True, if the {@link CustomPattern} can be applied at the end of the text.
	 * Default is true.
	 */
	public CustomPattern setAppliesAtEnd(boolean appliesAtEnd) {
		this.appliesAtEnd = appliesAtEnd;
		return this;
	}
	
	/**
	 * True, if the {@link CustomPattern} can be applied in the middle of the text.
	 * Default is true.
	 */
	public boolean isAppliesInMiddle() {
		return appliesInMiddle;
	}
	
	/**
	 * True, if the {@link CustomPattern} can be applied in the middle of the text.
	 * Default is true.
	 */
	public CustomPattern setAppliesInMiddle(boolean appliesInMiddle) {
		this.appliesInMiddle = appliesInMiddle;
		return this;
	}
	
	/**
	 * @return The chinese word
	 */
	public String getZh() {
		return zh;
	}
	
	/**
	 * @return The pinyin transliteration.
	 */
	public String getPinyin() {
		return pinyin;
	}
	
}
