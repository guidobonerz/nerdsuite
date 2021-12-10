package de.drazil.nerdsuite.lexer;

public enum Type {
	OPEN_BRACE_ROUNDED, OPEN_BRACE_CURLY, OPEN_BRACE_SQUARED, CLOSE_BRACE_ROUNDED, CLOSE_BRACE_CURLY,
	CLOSE_BRACE_SQUARED, PLUS, MINUS, MUL, DIV, MOD, AND, OR, XOR, TRUE, FALSE, INT, HEX, BIN, BYTE, WORD, GREATER,
	LESS, GREATER_THAN, LESS_THAN, EQUAL, COMPARE, SEMI, COMMA, COLON, EXPRESSION, CONTENT_BLOCK, EOL, SINGLE_QUOTE,
	DOUBLE_QUOTE, PROPERTY, IDENTIFIER
}