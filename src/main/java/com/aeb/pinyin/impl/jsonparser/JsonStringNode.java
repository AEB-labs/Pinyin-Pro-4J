package com.aeb.pinyin.impl.jsonparser;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class JsonStringNode extends JsonNode {
	
	public JsonStringNode(String value) {
		super();
		this.value = value;
	}
	
	public final String value;
	
	@Override
	public boolean isString() {
		return true;
	}
	
	@Override
	protected void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException {
		writer.write('"');
		try {
			value.chars().forEach(c -> {
				try {
					if (c == '"') {
						writer.write("\\\"");
					} else if (c == '\\') {
						writer.write("\\\\");
					} else if (c == '\b') {
						writer.write("\\b");
					} else if (c == '\f') {
						writer.write("\\f");
					} else if (c == '\n') {
						writer.write("\\n");
					} else if (c == '\r') {
						writer.write("\\r");
					} else if (c == '\t') {
						writer.write("\\t");
					} else if (c < 0x20 || c > 0x7E) {
						writer.write(String.format("\\u%04x", c));
					} else {
						writer.write(c);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (RuntimeException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
		writer.write('"');
	}
}
