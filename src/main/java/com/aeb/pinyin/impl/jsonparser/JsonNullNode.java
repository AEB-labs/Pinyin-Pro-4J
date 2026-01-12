package com.aeb.pinyin.impl.jsonparser;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class JsonNullNode extends JsonNode {
	
	public static final JsonNullNode INSTANCE = new JsonNullNode();
	
	private JsonNullNode() {
	}
	
	@Override
	public boolean isNull() {
		return true;
	}
	
	@Override
	protected void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException {
		writer.write("null");
	}
	
}
