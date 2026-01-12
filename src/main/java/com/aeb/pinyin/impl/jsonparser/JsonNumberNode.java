package com.aeb.pinyin.impl.jsonparser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonNumberNode extends JsonNode {
	
	public JsonNumberNode(String value) {
		this.value = value;
	}
	
	public JsonNumberNode(BigDecimal value) {
		this.value = value.toString();
	}
	
	private final String value;
	
	@Override
	public boolean isNumber() {
		return true;
	}
	
	public BigDecimal asBigDecimal() {
		return new BigDecimal(value);
	}
	
	public BigInteger asBigInteger() {
		return asBigDecimal().toBigInteger();
	}
	
	public double asDouble() {
		return asBigDecimal().doubleValue();
	}
	
	public long asLong() {
		return asBigDecimal().longValue();
	}
	
	public int asInt() {
		return asBigDecimal().intValue();
	}
	
	public float asFloat() {
		return asBigDecimal().floatValue();
	}
	
	@Override
	protected void writeJsonTo(OutputStreamWriter writer, boolean prettyPrint, int i) throws IOException {
		writer.write(value);
	}
	
}
