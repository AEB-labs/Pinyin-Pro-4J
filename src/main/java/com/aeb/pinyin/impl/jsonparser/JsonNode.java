package com.aeb.pinyin.impl.jsonparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public abstract class JsonNode {
	
	public boolean isObject() {
		return false;
	}
	
	public boolean isArray() {
		return false;
	}
	
	public boolean isString() {
		return false;
	}
	
	public boolean isNumber() {
		return false;
	}
	
	public boolean isNull() {
		return false;
	}
	
	public boolean isBoolean() {
		return false;
	}
	
	public String toJsonString(boolean prettyPrint) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.writeJsonTo(out, prettyPrint);
		return out.toString(StandardCharsets.UTF_8);
	}
	
	/**
	 * Writes the JSON representation of this object to the OutputStream.
	 * @param out The OutputStream to write to. Will be closed after writing.
	 * @param prettyPrint Whether to pretty print the JSON output.
	 */
	public void writeJsonTo(OutputStream out, boolean prettyPrint) throws IOException {
		try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			this.writeJsonTo(writer, prettyPrint, 0);
		}
	}
	
	protected abstract void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException;
	
	protected final void writeIndent(OutputStreamWriter writer, int indentLevel) throws IOException {
		for (int i = 0; i < indentLevel; i++) {
			writer.write("  ");
		}
	}
	
}
