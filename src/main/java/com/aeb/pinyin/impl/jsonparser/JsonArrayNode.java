package com.aeb.pinyin.impl.jsonparser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonArrayNode extends JsonNode {
	
	public final List<JsonNode> elements = new ArrayList<>();
	
	@Override
	public boolean isArray() {
		return true;
	}
	
	@Override
	protected void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException {
		writer.write("[");
		int currentElement = 0;
		for (JsonNode entry : elements) {
			currentElement++;
			if (prettyPrint) {
				writer.write("\n");
				writeIndent(writer, i + 1);
			} else {
				writer.write(" ");
			}
			
			entry.writeJsonTo(writer, prettyPrint, i + 1);
			if (currentElement < elements.size()) {
				writer.write(",");
			}
		}
		if (prettyPrint) {
			writer.write("\n");
			writeIndent(writer, i);
		} else {
			writer.write(" ");
		}
		writer.write("]");
	}
	
}
