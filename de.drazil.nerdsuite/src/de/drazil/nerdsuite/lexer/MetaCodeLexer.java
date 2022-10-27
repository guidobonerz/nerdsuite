package de.drazil.nerdsuite.lexer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class MetaCodeLexer {

	public MetaCodeLexer() {

	}

	private static String content = "@set _debug=true\n@set x=1\n10 print\"hallo\":a=1\n" + "@if _debug\n"
			+ "20 poke 53280,1:poke 53281,0\n" + "30 print\"das ist ein test\"\n" + "@end\n"
			+ "40 aff_34=1:   b=2:c=3\n " + "@asm name='test'\n" + "lda #$01\n" + "sta$d020\n" + "  @end   ";

	public static String getAtom(String s, int i) {
		int j = i;
		for (; j < s.length();) {

			if (Character.isLetter(s.charAt(j)) || Character.isDigit(s.charAt(j))) {
				j++;
			} else {
				System.out.println(">" + s.substring(i, j) + "<");
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	public static String getContent(String s, int i) {
		int j = i;
		for (; j < s.length();) {
			if (Character.isLetter(s.charAt(j)) || Character.isDigit(s.charAt(j))
					|| Character.isWhitespace(s.charAt(j))) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	public static String getQuotedContent(String s, int i, char quote) {
		int j = i;
		for (; j < s.length();) {
			if (s.charAt(j) != quote) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	private static void addElement(List<Token> result, String input, int start, int end) {
		if (end > start) {
			int length = end - start;
			String s = input.substring(start, start + length);
			if (s.matches(LexicalRule.HEX_VALUE_BYTE.getValue())) {
				result.add(new Token(Type.HEX, start, length));
			} else if (s.matches(LexicalRule.HEX_ADRESS_WORD.getValue())) {
				result.add(new Token(Type.HEX, start, length));
			} else if (s.matches(LexicalRule.HEX_ADRESS_BYTE.getValue())) {
				result.add(new Token(Type.HEX, start, length));
			} else if (s.matches(LexicalRule.IDENTIFIER.getValue())) {
				result.add(new Token(Type.IDENTIFIER, start, length));
			} else if (s.matches(LexicalRule.INT_VALUE_BYTE.getValue())) {
				result.add(new Token(Type.INT, start, length));
			} else if (s.matches(LexicalRule.BIN_VALUE_BYTE.getValue())) {
				result.add(new Token(Type.BIN, start, length));
			} else if (s.matches(LexicalRule.INTEGER.getValue())) {
				result.add(new Token(Type.INT, start, length));
			}
		}
	}

	public static List<Token> lex(String input) {
		int lastIndex = 0;
		List<Token> result = new ArrayList<>();
		CharacterIterator ci = new StringCharacterIterator(input);
		char ch = ci.first();

		while (ch != CharacterIterator.DONE) {
			switch (ch) {
			case '@': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.AT, i, 1));
				break;
			}
			case ':': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.COLON, i, 1));
				break;
			}
			case ',': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.COMMA, i, 1));
				break;
			}
			case '.': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.POINT, i, 1));
				break;
			}
			/*
			 * case '#': { addElement(result, input, lastIndex, ci.getIndex()); int i =
			 * ci.getIndex(); lastIndex = i + 1; result.add(new Token(Type.HASH, i, 1));
			 * break; }
			 */
			case ' ':
			case '\t':
			case '\n':
			case '\r': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.WHITESPACE, i, 1));
				break;
			}
			/*
			 * case '$': { addElement(result, input, lastIndex, ci.getIndex()); int i =
			 * ci.getIndex(); lastIndex = i + 1; result.add(new Token(Type.DOLLAR, i, 1));
			 * break; }
			 */
			case '=': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.EQUAL, i, 1));
				break;
			}
			case ';': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.SEMICOLON, i, 1));
				break;
			}
			case '\'': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.SINGLE_QUOTE, i, 1));
				break;
			}
			case '\"': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.DOUBLE_QUOTE, i, 1));
				break;
			}

			case '{': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.OPEN_BRACE_CURLY, i, 1));
				break;
			}
			case '}': {
				addElement(result, input, lastIndex, ci.getIndex());
				int i = ci.getIndex();
				lastIndex = i + 1;
				result.add(new Token(Type.CLOSE_BRACE_CURLY, i, 1));
				break;
			}

			}
			ch = ci.next();
		}

		return result;
	}

	public static void main(String argv[]) {
		List<Token> tokens = lex(content);
		for (Token t : tokens) {
			System.out.println(t + " > " + content.substring(t.getOffset(), t.getOffset() + t.getLength()));
		}
	}
}
