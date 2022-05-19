package de.drazil.nerdsuite.lexer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

public class MetaCodeLexer {

	public MetaCodeLexer() {

	}
	
	private static String content = "@set _debug=true\n@set x=1\n10 print\"hallo\":a=1\n" + "@if _debug\n"
			+ "20 poke 53280,1:poke53281,0\n" + "30 print\"das ist ein test\"\n" + "@end\n" + "40 a=1:b=2:c=3\n "
			+ "@asm name='test'\n" + "lda $#01\n" + "sta $d020\n" + "  @end   ";

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

	public static List<Token> lex(String input, int offset, Token token) {
		List<Token> result = token.getTokenList();
		CharacterIterator ci = new StringCharacterIterator(input);
		char ch = ci.first();

		while (ch != CharacterIterator.DONE) {
			switch (ch) {
			case '@': {
				result.add(new Token(Type.EXPRESSION, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex() + 1;
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end - 1);
				result.add(new Token(Type.NAME, s, start, end - 1));
				break;
			}
			case '_': {
				result.add(new Token(Type.UNDERSCORE, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex() + 1;
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end - 1);
				result.add(new Token(Type.NAME, s, start, end - 1));
				break;
			}
			case '$': {
				result.add(new Token(Type.PROPERTY, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex() + 1;
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end - 1);
				result.add(new Token(Type.NAME, s, start, end - 1));
				break;
			}
			case '=': {
				result.add(new Token(Type.EQUAL, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex() + 1;
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end - 1);
				result.add(new Token(Type.NAME, s, start, end - 1));
				break;
			}
			case ';': {
				result.add(new Token(Type.SEMICOLON, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex();
				ci.setIndex(start + 1);
				break;
			}
			case '\'': {
				result.add(new Token(Type.SINGLE_QUOTE, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex();
				String s = getQuotedContent(input, start, '\'');
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.SINGLE_QUOTE, s, start, end));
				break;
			}
			case '\"': {
				result.add(new Token(Type.DOUBLE_QUOTE, Character.toString(ch), ci.getIndex(), ci.getIndex()));
				int start = ci.getIndex();
				String s = getQuotedContent(input, start, '\"');
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.DOUBLE_QUOTE, s, start, end));
				break;
			}

			case '{': {
				int start = ci.getIndex();
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.OPEN_BRACE_CURLY, s, start, end));
				break;
			}
			case '}': {
				int start = ci.getIndex();
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.CLOSE_BRACE_CURLY, s, start, end));
				break;
			}

			}
			ch = ci.next();
		}

		return result;
	}

	public static void main(String argv[]) {
		List<Token> tokens = lex(content, 0, new Token(Type.CONTENT_BLOCK, "", 0, content.length()));
		for (Token t : tokens) {
			System.out.println(t);
		}
	}
}
