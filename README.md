# PinyinPro4J

This is a partial port of the JavaScript library [pinyin-pro](https://pinyin-pro.cn/en/) (see also [Github](https://github.com/zh-lx/pinyin-pro/) ) to Java.<br>
It provides functionality to convert Chinese characters to Pinyin with various options.<br>
The core conversion logic is adapted from the original JavaScript library:
- Big Json libraries of Chinese words with frequency data.
- Aho Corasick algorithm for efficient multi-pattern matching.
- Max-probability algorithm for disambiguation of multiple pronunciations.

However, the API and some postprocessing is designed to be idiomatic for Java developers.


## Features
- Convert Chinese text to Pinyin with tone marks, tone numbers, or without tones, with (according to the original author zh-lx) very high accuracy.
- Find all possible Pinyin conversions for single chinese characters.

## Features currently not implemented
The following features from the original pinyin-pro library are not yet implemented in this Java port:
- Support for custom dictionaries.
- Support for matching Chinese text with a Pinyin text.
- Support for initial consonants only, finals only and tone only outputs.
- Support for other segmentation algorithms than max-probability.
- Support for HTML output.

## Getting started
To use PinyinPro4J in your Java project, include the library as a maven dependency in your *pom.xml*:
```
<dependency>
	<groupId>com.aeb.pinyin</groupId>
	<artifactId>pinyin-pro-4j</artifactId>
	<version>0.1.0</version>
</dependency>  
```
PinyinPro4J requires Java 21 or higher.<br>
It does not have any additional dependencies.<br>


## Converting Chinese text to Pinyin

### Basic usage
For converting Chinese text to Pinyin you first need to create a PinyinSegmentationContext, 
which holds the necessary dictionaries:
```
PinyinSegmentationContext context = PinyinPro4J.createNewContext(AdditionalDictionary.COMPLETE);
```
Make sure to reuse the same context for multiple conversions to avoid reloading the dictionaries each time.<br>
With this context you can use the *convertToPinyin* method to convert Chinese text to Pinyin:
```
String pinyin = PinyinPro4J.convertToPinyin(
	"汉语拼音很有趣",
	new SegmentationOptions(),
	new PinyinFormatOptions(),
	context);
```
This results in the following Pinyin output: "hànyǔpīnyīn hěn yǒuqù".

### Segmentation options (surname mode)
The segementation options are the second parameter of the *convertToPinyin* method.<br>
There is currently only one segmentation option available for the surname mode, e.g.:
```
	new SegmentationOptions().setSurnameMode(SurnameMode.ON)
```
The surname mode can be set to 
- SurnameMode.ON: Always activate surname mode.
- SurnameMode.OFF: Never activate surname mode.
- SurnameMode.HEAD: Activate surname mode only for the first word.

If the surname mode is activated, a special dictionary for Chinese surnames takes precedence during segmentation.<br>
As this leads to rather inferior results for normal text, the surname mode is SurnameMode.OFF by default.<br>

### Pinyin format options
The format options are the third parameter of the *convertToPinyin* method.<br>
They allow to customize the output format of the Pinyin conversion in a fluent API style, e.g.,
```
	new PinyinFormatOptions()
	  .setSegmentSeparator(" - ")
	  .setSyllableSeparator(" ")
	  .setToneMode(PinyinToneMode.NUMBERS)
	  .setNonZhOption(NonZhOption.REMOVE)
```
- segment separator: String to separate segments (words) in the output. Default is a single space " ".
- syllable separator: String to separate syllables within a segment (word). Default is no separator "".
- tone mode: How to represent tones in the output. Possible values are:
  - PinyinToneMode.STANDARD: Use tone marks (default).
  - PinyinToneMode.NUMBERS: Use tone numbers.
  - PinyinToneMode.NUMBERS_ASCII: Use tone numbers but replace ü with v and ê with e.
  - PinyinToneMode.NONE: No tone representation.
  - PinyinToneMode.NONE_ASCII: No tone representation and replace ü with v and ê with e.
- non-Chinese option: How to handle non-Chinese characters in the output. Possible values are:
	- NonZhFormatOption.KEEP: Preserve non-Chinese characters 1 to 1.
	- NonZhFormatOption.REMOVE: Remove non-Chinese characters from the output.
	- NonZhFormatOption.TRIM: Preserve non-Chinese characters but remove any leading or trailing non-Chinese characters equal to the segment separator (thus avoiding duplicate spaces if the segment separator is a space).

## Getting segmentation details for a chinese text
For custom formatting of the output you can also get details of the segmentation result with *segmentizeText*.
E.g. you could output the original chinese text along with the pinyin for each segment:
```
List<Segment> segments = PinyinPro4J.segmentizeText(
	"汉语拼音很有趣",
	new SegmentationOptions().setSurnameMode(SurnameMode.OFF),
	context);
```

## Getting all possible Pinyin readings for a single Chinese character
If you want to get all possible Pinyin readings for a single Chinese character, you can use the *getPinyinsForCharacter* method.
This does not require a segmentation context.
```
List<String> possiblePiyins = PinyinPro4J.getPinyinsForCharacter("语");
```
This results in the following list of possible Pinyin readings: [yǔ, yù].

There is also a method to get the first reading for a single Chinese character:
```
String pinyin = PinyinPro4J.getFirstPinyinForCharacter("语");
```