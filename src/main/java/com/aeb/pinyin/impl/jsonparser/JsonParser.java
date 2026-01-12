package com.aeb.pinyin.impl.jsonparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * A minimal JSON parser for internal data loading, avoiding external dependencies.<br>
 * Only supports as result a tree of {@link JsonNode} instances (no mapping to Java objects).<br>
 */
public class JsonParser {
	
	/**
	 * Creates a new MiniJsonParser reading from the given input string.<br>
	 * @param input The input string containing the JSON data.
	 * @throws IOException 
	 */
	public JsonParser(String input) throws IOException {
		this(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
	}
	
	/**
	 * Creates a new MiniJsonParser reading from the given input stream.<br>
	 * After this, call {@link #parse()} to parse the JSON data.
	 * @param input The input stream to read the JSON data from.<br>
	 *           Must be UTF-8 encoded and closed by the caller after calling {@link #parse()}.
	 * @throws IOException
	 */
	public JsonParser(InputStream input) throws IOException {
		Reader source = new InputStreamReader(input, StandardCharsets.UTF_8);
		scanner = new Scanner(source);
	}
	
	private final Scanner scanner;
	
	/**
	 * Parses the JSON data and returns the resulting object.<br>
	 * @return The parsed JSON object.
	 * @throws IOException
	 * @throws QueryParserException
	 */
	public JsonNode parse() throws IOException, QueryParserException {
		scanner.nextToken();
		JsonNode result = parseAbstractObject();
		if (scanner.getCurrentToken().tokenType != TokenType.EndOfInput) {
			throw new QueryParserException(
					"Unexpected token after end of JSON data",
					scanner.getCurrentToken().pos);
		}
		return result;
	}
	
	private JsonNode parseAbstractObject() throws IOException, QueryParserException {
		final Token currentToken = scanner.getCurrentToken();
		return switch (currentToken.tokenType) {
			case CurlyOpen -> parseObject();
			case SquareOpen -> parseArray();
			case String -> parseSingleTokenObject(new JsonStringNode(currentToken.tokenValue));
			case Number -> parseSingleTokenObject(new JsonNumberNode(currentToken.tokenValue));
			case Boolean -> parseSingleTokenObject(
					"true".equals(currentToken.tokenValue) ? JsonBooleanNode.TRUE : JsonBooleanNode.FALSE);
			case Null -> parseSingleTokenObject(JsonNullNode.INSTANCE);
			default -> throw new QueryParserException(
					"Unexpected token type: " + currentToken.tokenType,
					currentToken.pos);
		};
	}
	
	private JsonNode parseSingleTokenObject(JsonNode result) throws QueryParserException,
			IOException {
		scanner.nextToken();
		return result;
	}
	
	private JsonArrayNode parseArray() throws IOException, QueryParserException {
		expectAndEatToken(TokenType.SquareOpen, "Expected '['");
		JsonArrayNode array = new JsonArrayNode();
		
		if (scanner.getCurrentToken().tokenType == TokenType.SquareClose) {
			// empty array
			scanner.nextToken();
			return array;
		}
		
		while (true) {
			
			JsonNode value = parseAbstractObject();
			array.elements.add(value);
			
			if (scanner.getCurrentToken().tokenType == TokenType.Comma) {
				scanner.nextToken();
			} else {
				expectAndEatToken(TokenType.SquareClose, "Expected ']'");
				return array;
			}
		}
	}
	
	private JsonObjectNode parseObject() throws IOException, QueryParserException {
		expectAndEatToken(TokenType.CurlyOpen, "Expected '{'");
		JsonObjectNode obj = new JsonObjectNode();
		
		if (scanner.getCurrentToken().tokenType == TokenType.CurlyClose) {
			// empty object
			scanner.nextToken();
			return obj;
		}
		
		while (true) {
			String key = expectAndEatToken(TokenType.String, "Expected string as object key");
			
			expectAndEatToken(TokenType.Colon, "Expected ':' after object key");
			
			JsonNode value = parseAbstractObject();
			
			obj.put(key, value);
			
			if (scanner.getCurrentToken().tokenType == TokenType.Comma) {
				scanner.nextToken();
			} else {
				expectAndEatToken(TokenType.CurlyClose, "Expected '}'");
				return obj;
			}
		}
	}
	
	private String expectAndEatToken(TokenType expectedType, String message) throws IOException, QueryParserException {
		final Token currentToken = scanner.getCurrentToken();
		if (currentToken.tokenType != expectedType) {
			throw new QueryParserException(
					message + " (but found '" + (currentToken.tokenValue == null
							? currentToken.tokenType.name()
							: currentToken.tokenValue) + "')",
					currentToken.pos);
		}
		scanner.nextToken();
		return currentToken.tokenValue;
	}
	
	public static class QueryParserException extends Exception {
		
		private static String computeMessage(String msg, TextPosition pos) {
			return msg + " (at line " + pos.line + ", column " + pos.column + ")";
		}
		
		public QueryParserException(String basicMessage, TextPosition pos) {
			super(computeMessage(basicMessage, pos));
			this.basicMessage = basicMessage;
			this.pos = pos;
		}
		
		final public String basicMessage;
		final public TextPosition pos;
		
	}
	
	enum TokenType {
		String,
		CurlyOpen,
		CurlyClose,
		SquareOpen,
		SquareClose,
		Comma,
		Colon,
		Number,
		Boolean,
		Null,
		EndOfInput
	}
	
	static class TextPosition {
		
		TextPosition(int line, int column) {
			this.line = line;
			this.column = column;
		}
		
		final int line;
		final int column;
	}
	
	static class Token {
		
		Token(TokenType tokenType, String tokenValue, TextPosition pos) {
			this.tokenType = tokenType;
			this.tokenValue = tokenValue;
			this.pos = pos;
		}
		
		final TokenType tokenType;
		final String tokenValue;
		final TextPosition pos;
		
	}
	
	static class Scanner {
		
		private static final int END_OF_INPUT = -1;
		
		private static boolean isWhitespace(int c) {
			return Character.isWhitespace(c);
		}
		
		Scanner(Reader source) throws IOException {
			this.source = source;
			nextChar();
			nextChar();
		}
		
		private int currentChar = 0;
		private int nextChar = 0;
		
		private Token currentToken;
		
		private int line = 1;
		private int column = -1;
		private final Reader source;
		
		Token getCurrentToken() {
			return currentToken;
		}
		
		TextPosition getCurrentPosition() {
			return new TextPosition(line, column);
		}
		
		/**
		 * Advances to the next token, which can then be fetched with {@link #getCurrentToken()}.
		 */
		protected void nextToken() throws QueryParserException, IOException {
			currentToken = null;
			while (isWhitespace(currentChar)) {
				nextChar();
			}
			switch (currentChar) {
				case END_OF_INPUT ->
					createSingleCharToken(TokenType.EndOfInput);
				case '{' ->
					createSingleCharToken(TokenType.CurlyOpen);
				case '}' ->
					createSingleCharToken(TokenType.CurlyClose);
				case '[' ->
					createSingleCharToken(TokenType.SquareOpen);
				case ']' ->
					createSingleCharToken(TokenType.SquareClose);
				case ',' ->
					createSingleCharToken(TokenType.Comma);
				case ':' ->
					createSingleCharToken(TokenType.Colon);
				case 't' ->
					scanIdentifier(TokenType.Boolean, "true");
				case 'f' ->
					scanIdentifier(TokenType.Boolean, "false");
				case 'n' ->
					scanIdentifier(TokenType.Null, "null");
				case '"' ->
					scanString();
				default ->
					scanNumber();
			}
		}
		
		private void createSingleCharToken(TokenType type) throws IOException {
			String ch = (currentChar == END_OF_INPUT) ? "EOF" : Character.toString((char) currentChar);
			currentToken = new Token(type, ch, new TextPosition(line, column));
			nextChar();
		}
		
		private void scanString() throws QueryParserException, IOException {
			final TextPosition startPosition = new TextPosition(line, column);
			StringBuilder buffer = new StringBuilder();
			nextChar(); // consume opening " 
			while (true) {
				if (currentChar == END_OF_INPUT) {
					throw new QueryParserException(
							"Unexpected end of input in string literal",
							new TextPosition(line, column));
				} else if (currentChar == '"') {
					nextChar(); // consume closing "
					break;
				} else if (currentChar == '\\') {
					// escape sequence
					nextChar();
					switch (currentChar) {
						case '"' -> buffer.append('"');
						case '\\' -> buffer.append('\\');
						case '/' -> buffer.append('/');
						case 'b' -> buffer.append('\b');
						case 'f' -> buffer.append('\f');
						case 'n' -> buffer.append('\n');
						case 'r' -> buffer.append('\r');
						case 't' -> buffer.append('\t');
						case 'u' -> {
							StringBuilder hex = new StringBuilder();
							for (int i = 0; i < 4; i++) {
								nextChar();
								if (!((currentChar >= '0' && currentChar <= '9') ||
										(currentChar >= 'a' && currentChar <= 'f') ||
										(currentChar >= 'A' && currentChar <= 'F'))) {
									throw new QueryParserException(
											"Invalid unicode escape sequence in string literal",
											new TextPosition(line, column));
								}
								hex.append((char) currentChar);
							}
							try {
								int codePoint = Integer.parseInt(hex.toString(), 16);
								buffer.append(Character.toString(codePoint));
							} catch (IllegalArgumentException e) {
								throw new QueryParserException(
										"Invalid unicode escape sequence in string literal",
										new TextPosition(line, column));
							}
						}
						default -> throw new QueryParserException(
								"Invalid escape sequence in string literal",
								new TextPosition(line, column));
					}
				} else {
					buffer.append((char) currentChar);
				}
				nextChar();
			}
			
			currentToken = new Token(TokenType.String, buffer.toString(), startPosition);
		}
		
		private void scanNumber() throws QueryParserException, IOException {
			final TextPosition startPosition = new TextPosition(line, column);
			StringBuilder buffer = new StringBuilder();
			
			if (currentChar == '-') {
				buffer.append((char) currentChar);
				nextChar();
			}
			
			if (!isDigit(currentChar)) {
				throw new QueryParserException(
						"Unexpected character: Expecting digit in number literal",
						new TextPosition(line, column));
			}
			while (isDigit(currentChar)) {
				buffer.append((char) currentChar);
				nextChar();
			}
			
			if (currentChar == '.') {
				buffer.append((char) currentChar);
				nextChar();
				
				if (!isDigit(currentChar)) {
					throw new QueryParserException(
							"Unexpected character: Expecting digit in number literal after decimal point",
							new TextPosition(line, column));
				}
				while (isDigit(currentChar)) {
					buffer.append((char) currentChar);
					nextChar();
				}
			}
			
			if (currentChar == 'e' || currentChar == 'E') {
				buffer.append((char) currentChar);
				nextChar();
				
				if (currentChar == '+' || currentChar == '-') {
					buffer.append((char) currentChar);
					nextChar();
				}
				
				if (!isDigit(currentChar)) {
					throw new QueryParserException(
							"Unexpected character: Expecting digit in number literal after exponent",
							new TextPosition(line, column));
				}
				while (isDigit(currentChar)) {
					buffer.append((char) currentChar);
					nextChar();
				}
			}
			currentToken = new Token(TokenType.Number, buffer.toString(), startPosition);
		}
		
		private boolean isDigit(int c) {
			return c >= '0' && c <= '9';
		}
		
		private void scanIdentifier(TokenType tokenType, String expected) throws QueryParserException, IOException {
			final TextPosition startPosition = new TextPosition(line, column);
			for (int i = 0; i < expected.length(); i++) {
				if (currentChar != expected.charAt(i)) {
					throw new QueryParserException(
							"Unexpected identifier: Expecting " + expected,
							startPosition);
				}
				nextChar();
			}
			currentToken = new Token(tokenType, expected, startPosition);
		}
		
		private void nextChar() throws IOException {
			if (currentChar == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
			currentChar = nextChar;
			nextChar = source.read();
		}
		
	}
	
}
