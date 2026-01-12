package com.aeb.pinyin.impl.jsonparser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JsonObjectNode extends JsonNode {
	
	private final Map<String, JsonNode> values = new LinkedHashMap<>();
	
	public List<String> getKeys() {
		return new ArrayList<>(values.keySet());
	}
	
	public Set<Entry<String, JsonNode>> getEntries() {
		return values.entrySet();
	}
	
	public JsonNode get(String key) {
		return values.get(key);
	}
	
	public void put(String key, JsonNode value) {
		values.put(key, value);
	}
	
	@Override
	public boolean isObject() {
		return true;
	}
	
	@Override
	protected void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException {
		writer.write("{");
		final Set<Entry<String, JsonNode>> entrySet = values.entrySet();
		int entryCount = entrySet.size();
		int currentEntry = 0;
		for (Map.Entry<String, JsonNode> entry : entrySet) {
			currentEntry++;
			if (prettyPrint) {
				writer.write("\n");
				writeIndent(writer, i + 1);
			} else {
				writer.write(" ");
			}
			new JsonStringNode(entry.getKey()).writeJsonTo(writer, prettyPrint, 0);
			writer.write(": ");
			
			entry.getValue().writeJsonTo(writer, prettyPrint, i + 1);
			if (currentEntry < entryCount) {
				writer.write(",");
			}
		}
		if (prettyPrint) {
			writer.write("\n");
			writeIndent(writer, i);
		} else {
			writer.write(" ");
		}
		writer.write("}");
	}
	
}
