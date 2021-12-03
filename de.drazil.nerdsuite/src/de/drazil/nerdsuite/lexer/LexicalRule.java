package de.drazil.nerdsuite.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LexicalRule {

	public final static LexicalRule IDENTIFIER = new LexicalRule("[a-zA-Z_][a-zA-Z_0-9]*");
	public final static LexicalRule INTEGER = new LexicalRule("[0-9]*");
	public final static LexicalRule ALPHANUMERIC = new LexicalRule("[a-zA-Z0-9]*");
	public final static LexicalRule HEX = new LexicalRule("[0-9a-fA-F]*");
	public final static LexicalRule HEX_ADRESS_BYTE = new LexicalRule("\\$[0-9a-fA-F]{2}");
	public final static LexicalRule HEX_ADRESS_WORD = new LexicalRule("\\$[0-9a-fA-F]{4}");
	public final static LexicalRule HEX_VALUE_BYTE = new LexicalRule("#\\$[0-9a-fA-F]{2}");
	public final static LexicalRule BIN_VALUE_BYTE = new LexicalRule("#\\$[0-1]{8}");
	public final static LexicalRule PROPERTY = new LexicalRule("\\$\\{[a-zA-Z]{1}([a-zA-Z0-9]*)?\\}");
	public final static LexicalRule PLUS = new LexicalRule("+");
	public final static LexicalRule MINUS = new LexicalRule("-");
	public final static LexicalRule DIV = new LexicalRule("/");
	public final static LexicalRule MUL = new LexicalRule("*");
	public final static LexicalRule MOD = new LexicalRule("%");
	public final static LexicalRule XOR = new LexicalRule("^");
	public final static LexicalRule AND = new LexicalRule("&");
	public final static LexicalRule OR = new LexicalRule("|");
	public final static LexicalRule TRUE = new LexicalRule("true");
	public final static LexicalRule FALSE = new LexicalRule("false");
	public final static LexicalRule OPEN_BRACE = new LexicalRule("[");
	public final static LexicalRule CLOSED_BRACE = new LexicalRule("]");
	public final static LexicalRule OPEN_ROUND_BRACE = new LexicalRule("(");
	public final static LexicalRule CLOSED_ROUND_BRACE = new LexicalRule(")");
	public final static LexicalRule OPEN_CURLY_BRACE = new LexicalRule("{");
	public final static LexicalRule CLOSED_CURLY_BRACE = new LexicalRule("}");
	public final static LexicalRule LESS_THAN = new LexicalRule("<");
	public final static LexicalRule LESS_THAN_EQUAL = new LexicalRule("<=");
	public final static LexicalRule GREATER_THAN = new LexicalRule(">");
	public final static LexicalRule GREATER_THAN_EQUAL = new LexicalRule("=>");
	public final static LexicalRule EQUAL = new LexicalRule("=");
	public final static LexicalRule COMPARE = new LexicalRule("==");
	public final static LexicalRule SEMI = new LexicalRule(";");
	public final static LexicalRule COLON = new LexicalRule(":");
	public final static LexicalRule COMMA = new LexicalRule(",");
	public final static LexicalRule DOUBLE_QUOTE = new LexicalRule("\"");
	public final static LexicalRule SINGLE_QUOTE = new LexicalRule("'");
	public final static LexicalRule WHITESPACE = new LexicalRule("[\n\r\t ]+");
	public final static LexicalRule DATA = new LexicalRule(".*");

	private String value;
}
