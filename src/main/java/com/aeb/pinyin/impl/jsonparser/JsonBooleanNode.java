package com.aeb.pinyin.impl.jsonparser;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class JsonBooleanNode extends JsonNode {
	
	public static final JsonBooleanNode TRUE = new JsonBooleanNode(true);
	public static final JsonBooleanNode FALSE = new JsonBooleanNode(false);
	
	private JsonBooleanNode(boolean value) {
		this.value = value;
	}
	
	public final boolean value;
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	protected void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException {
		writer.write(value ? "true" : "false");
	}
}
