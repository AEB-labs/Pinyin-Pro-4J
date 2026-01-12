package com.aeb.pinyin.impl.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.aeb.pinyin.impl.jsonparser.JsonArrayNode;
import com.aeb.pinyin.impl.jsonparser.JsonNode;
import com.aeb.pinyin.impl.jsonparser.JsonNumberNode;
import com.aeb.pinyin.impl.jsonparser.JsonObjectNode;
import com.aeb.pinyin.impl.jsonparser.JsonParser;
import com.aeb.pinyin.impl.jsonparser.JsonStringNode;
import com.aeb.pinyin.impl.segmentation.Pattern;
import com.aeb.pinyin.impl.segmentation.Priority;

/**
 * Reader for resource files in Json format.
 * These are typically dictionaries mapping Chinese characters/words to pinyin.
 */
public class JsonReader {
	
	/**
	 * Read {@link Pattern}s from a resource like modern.json or complete.json in the resource folder..
	 * Each property of the root node in the JSON is a Chinese word (String) and each value
	 * denotes an array with 3 elements:<br>
	 * - Pinyin (String)<br>
	 * - Probability (Number)<br>
	 * - Part of Speech (String)<br>
	 * 
	 * @param resourceClass The class to use for resource loading.
	 * @param resourceName The name of the resource to load.
	 * @param priority The priority to assign to all read patterns.
	 * @return A list of Patterns read from the resource.
	 */
	public List<Pattern> readPatternDictionary(
			Class<?> resourceClass,
			String resourceName,
			Priority priority) {
		List<Pattern> result = new ArrayList<>();
		try (InputStream inputStream = resourceClass.getResourceAsStream(resourceName)) {
			JsonParser parser = new JsonParser(inputStream);
			JsonNode tree = parser.parse();
			if (tree instanceof JsonObjectNode objectNode) {
				int propindex = 0;
				for (Entry<String, JsonNode> entry : objectNode.getEntries()) {
					String zh = entry.getKey();
					if (entry.getValue() instanceof JsonArrayNode arrayNode) {
						Pattern pinyinMeta =
								convertArrayNodeToPattern(arrayNode, zh, priority, resourceName, propindex);
						result.add(pinyinMeta);
					} else {
						throw new RuntimeException(
								"Expected property " + ((propindex + 1)) + " of root node to be array node in "
										+ resourceName);
					}
					propindex++;
				}
			} else {
				throw new RuntimeException("Expected object node as root of " + resourceName);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Error reading dictionary from resource " + resourceName + ": " + e.getMessage(),
					e);
		}
		return result;
	}
	
	private Pattern convertArrayNodeToPattern(
			JsonArrayNode arrayNode,
			String zh,
			Priority priority,
			String resourceName,
			int propindex) {
		if (arrayNode.elements.size() != 3) {
			throw new RuntimeException(
					"Expected property " + ((propindex + 1)) + " of root node to be array node with 3 elements in "
							+ resourceName);
		}
		
		String pinyin;
		double rate;
		String partOfSpeech;
		
		JsonNode element0 = arrayNode.elements.get(0);
		if (element0 instanceof JsonStringNode textNode) {
			pinyin = textNode.value;
		} else {
			throw new RuntimeException(
					"Expected property " + ((propindex + 1))
							+ " of root node to be array node with first element beeing a TextNode in "
							+ resourceName);
		}
		
		JsonNode element1 = arrayNode.elements.get(1);
		if (element1 instanceof JsonNumberNode numericNode) {
			rate = numericNode.asDouble();
		} else {
			throw new RuntimeException(
					"Expected property " + ((propindex + 1))
							+ " of root node to be array node with second element beeing a number node in "
							+ resourceName);
		}
		
		JsonNode element2 = arrayNode.elements.get(2);
		if (element2 instanceof JsonStringNode textNode2) {
			partOfSpeech = textNode2.value;
		} else {
			throw new RuntimeException(
					"Expected property " + ((propindex + 1))
							+ " of root node to be array node with third element beeing a string node in "
							+ resourceName);
		}
		Pattern result = new Pattern(zh, pinyin, priority, rate, partOfSpeech);
		return result;
	}
	
	/**
	 * Read a Json dictionary from a resource where the root is an Object with
	 * properties.<br>
	 * Each property of the root node should have just a string value, which
	 * obviously maps to Map from String (property name) to String (property value).<br>
	 * An example is the dict2.json resource.
	 * @param resourceClass The class to use for resource loading.
	 * @param resourceName The name of the resource to load.
	 * @return A Map with the dictionary read from the resource.
	 */
	public Map<String, String> readSimpleDictionary(Class<?> resourceClass, String resourceName) {
		Map<String, String> dictionary = new HashMap<>();
		try (InputStream inputStream = resourceClass.getResourceAsStream(resourceName)) {
			JsonParser parser = new JsonParser(inputStream);
			JsonNode tree = parser.parse();
			if (tree instanceof JsonObjectNode objectNode) {
				int propindex = 0;
				for (Entry<String, JsonNode> entry : objectNode.getEntries()) {
					String key = entry.getKey();
					if (entry.getValue() instanceof JsonStringNode textNode) {
						dictionary.put(key, textNode.value);
					} else {
						throw new RuntimeException(
								"Expected property " + ((propindex + 1)) + " of root node to be string node in "
										+ resourceName);
					}
					propindex++;
				}
			} else {
				throw new RuntimeException("Expected object node as root of " + resourceName);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Error reading dictionary from resource " + resourceName + ": " + e.getMessage(),
					e);
		}
		return dictionary;
	}
	
	/**
	 * Read a Json dictionary from a resource where the root is an Object with
	 * properties.<br>
	 * Each property of the root node should have an array of string values.
	 * Each value in this array will then be a key in the returned Map, with the
	 * property name as the value.<br>
	 * An example is the dict1.json resource.
	 * @param resourceClass The class to use for resource loading.
	 * @param resourceName The name of the resource to load.
	 * @return A Map with the reverse dictionary read from the resource.
	 */
	public Map<String, String> readReverseDictionary(Class<?> resourceClass, String resourceName) {
		Map<String, String> dictionary = new HashMap<>();
		try (InputStream inputStream = resourceClass.getResourceAsStream(resourceName)) {
			JsonParser parser = new JsonParser(inputStream);
			JsonNode tree = parser.parse();
			if (tree instanceof JsonObjectNode objectNode) {
				int propindex = 0;
				for (Entry<String, JsonNode> entry : objectNode.getEntries()) {
					String commonValue = entry.getKey();
					if (entry.getValue() instanceof JsonArrayNode arrayNode) {
						List<String> arrayValues = getArrayValues(arrayNode, propindex, resourceName);
						arrayValues.forEach(key -> dictionary.put(key, commonValue));
					} else {
						throw new RuntimeException(
								"Expected property " + ((propindex + 1)) + " of root node to be array node in "
										+ resourceName);
					}
					propindex++;
				}
			} else {
				throw new RuntimeException("Expected object node as root of " + resourceName);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Error reading dictionary from resource " + resourceName + ": " + e.getMessage(),
					e);
		}
		return dictionary;
	}
	
	private List<String> getArrayValues(JsonArrayNode arrayNode, int propindex, String resourceName) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < arrayNode.elements.size(); i++) {
			JsonNode element = arrayNode.elements.get(i);
			if (element instanceof JsonStringNode textNode) {
				result.add(textNode.value);
			} else {
				throw new RuntimeException(
						"Expected property " + ((propindex + 1))
								+ " of root node to be array node with only string nodes in it, in "
								+ resourceName);
			}
		}
		return result;
	}
	
}
