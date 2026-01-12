package com.aeb.pinyin.impl.jsonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.aeb.pinyin.impl.jsonparser.JsonParser.QueryParserException;

public class JsonParserTest {
	
	@Test
	public void testParseNull() throws Exception {
		parse("null", JsonNullNode.class, true);
	}
	
	@Test
	public void testParseTrue() throws Exception {
		JsonBooleanNode json = parse("true", JsonBooleanNode.class, true);
		assertTrue(json.value);
	}
	
	@Test
	public void testParseFalse() throws Exception {
		JsonBooleanNode json = parse("false", JsonBooleanNode.class, true);
		assertFalse(json.value);
	}
	
	@Test
	public void testParseString() throws Exception {
		//Not checking against writeTo here because write does not use escape
		//for /, but usess escape for unicode euro sign
		JsonStringNode json = parse("\" \\/ € \\u20ac \\u20AC \"", JsonStringNode.class, false);
		assertEquals("\" / \\u20ac \\u20ac \\u20ac \"", json.toJsonString(false));
		
		//Other escapes are 1:1
		parse("\"asdf \\\" \\\\ \\b \\f \\n \\r \\t \\u20ac \"", JsonStringNode.class, true);
		
	}
	
	@Test
	public void testParseNumber() throws Exception {
		parse("6e-11", JsonNumberNode.class, true);
		parse("3", JsonNumberNode.class, true);
		parse("-3", JsonNumberNode.class, true);
		parse("-34", JsonNumberNode.class, true);
		parse("-34.5", JsonNumberNode.class, true);
		parse("-34.56e1", JsonNumberNode.class, true);
		parse("-34.56E-1", JsonNumberNode.class, true);
		parse("-34.56E-11", JsonNumberNode.class, true);
		
		JsonNumberNode jsonNumberNode = parse("-34.56", JsonNumberNode.class, true);
		BigDecimal bigDecimal = jsonNumberNode.asBigDecimal();
		assertEquals(2, bigDecimal.scale());
		
		jsonNumberNode = parse("-34.56e-1", JsonNumberNode.class, true);
		bigDecimal = jsonNumberNode.asBigDecimal();
		assertEquals(3, bigDecimal.scale());
	}
	
	@Test
	public void testParseObject() throws Exception {
		parse("{ }", JsonObjectNode.class, true);
		parse("{ \"k1\": 1 }", JsonObjectNode.class, true);
		parse("{ \"k1\": 1, \"k2\": 2 }", JsonObjectNode.class, true);
		parse("{ \"k1\": { \"subkey\": 7 }, \"k2\": 2 }", JsonObjectNode.class, true);
		parse("{ \"k1\": { \"subkey\": 7 }, \"k2\": [ 4, 5 ] }", JsonObjectNode.class, true);
	}
	
	@Test
	public void testParseArray() throws Exception {
		parse("[ ]", JsonArrayNode.class, true);
		parse("[ 1, 2 ]", JsonArrayNode.class, true);
		parse("[ [ 3, 4 ], 2 ]", JsonArrayNode.class, true);
		parse("[ [ 3, { \"k1\": 1 } ], 2 ]", JsonArrayNode.class, true);
	}
	
	@Test
	public void testExceptions() throws Exception {
		//Invalid strings
		parseInvalid("\"", 1, 2);
		parseInvalid("\"\\u1\"", 1, 5);
		parseInvalid("\"\\e\"", 1, 3);
		
		//Invalid numbers
		parseInvalid("-.3", 1, 2);
		parseInvalid("3.", 1, 3);
		parseInvalid("3.4e", 1, 5);
		parseInvalid("3.4e-", 1, 6);
		
		//Invalid identifiers
		parseInvalid("trux", 1, 1);
		parseInvalid("falsx", 1, 1);
		parseInvalid("nxll", 1, 1);
		
		//Invalid arrays
		parseInvalid("[", 1, 2);
		parseInvalid("[ 1 ", 1, 5);
		parseInvalid("[ 1 2", 1, 5);
		
		//Invalid objects
		parseInvalid("{", 1, 2);
		parseInvalid("{ 4", 1, 3);
		parseInvalid("{ \"k1\" ", 1, 8);
		parseInvalid("{ \"k1\" : ", 1, 10);
		parseInvalid("{ \"k1\" : 5 \"k2\" : 3 }", 1, 12);
		parseInvalid("{ \"k1\" : 5 , }", 1, 14);
		
		//Input remaining
		parseInvalid("1 2", 1, 3);
		
		//Line numbering
		parseInvalid("\n\"", 2, 2);
		parseInvalid("\n \n  \"", 3, 4);
	}
	
	private <T extends JsonNode> T parse(String json, Class<T> expectedClass, boolean checkAgainstWriteTo)
			throws IOException,
			QueryParserException {
		JsonParser parser = new JsonParser(json);
		@SuppressWarnings("unchecked")
		final T result = (T) parser.parse();
		System.out.println("Parsed JSON: \r\n" + result.toJsonString(true));
		assertTrue(expectedClass.isInstance(result));
		if (checkAgainstWriteTo) {
			final String writtenJson = result.toJsonString(false);
			assertEquals(json, writtenJson);
		}
		return result;
	}
	
	private void parseInvalid(String json, int line, int col) throws IOException {
		JsonParser parser = new JsonParser(json);
		try {
			parser.parse();
		} catch (QueryParserException e) {
			System.out.println("Expected exception: " + e.basicMessage);
			assertEquals(line, e.pos.line);
			assertEquals(col, e.pos.column);
			return;
		}
		throw new AssertionError("Expected exception for invalid JSON: " + json);
	}
	
}
