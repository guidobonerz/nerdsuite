package de.drazil.nerdsuite.lexer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

public class MetaCodeLexer {

	public MetaCodeLexer() {

	}

	private static String content = "@set ${x}=1\n@set x=1\n10 print\"hallo\":a=1\n" + "@if ${debug}\n"
			+ "20 poke 53280,1:poke53281,0\n" + "30 print\"das ist ein test\"\n" + "@end\n" + "40 a=1:b=2:c=3\n "
			+ "@asm name='test'\n" + "lda $#01\n" + "sta $d020\n" + "  @end   ";

	public static String getAtom(String s, int i) {
		int j = i;
		for (; j < s.length();) {
			if (Character.isLetter(s.charAt(j)) || Character.isDigit(s.charAt(j))) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	public static String getPartition(String s, int start, char endMarker) {
		int j = start;
		for (; j < s.length();) {
			if (s.charAt(j) != endMarker) {
				j++;
			} else {
				return s.substring(start, j);
			}
		}
		return s.substring(start, j);
	}

	public static List<Token> lex(String input, int offset, Token token) {
		List<Token> result = token.getTokenList();
		CharacterIterator ci = new StringCharacterIterator(input);
		char ch = ci.first();

		while (ch != CharacterIterator.DONE) {
			switch (ch) {
			case '@': {
				int start = ci.getIndex() + 1;
				String s = getPartition(input, start, '\n');
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.EXPRESSION, s, start, end));
				break;
			}
			case '=': {
				int start = ci.getIndex();
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.EQUAL, s, start, end));
				break;
			}
			case '\'': {
				int start = ci.getIndex();
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.SINGLE_QUOTE, s, start, end));
				break;
			}
			case '\"': {
				int start = ci.getIndex();
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.SINGLE_QUOTE, s, start, end));
				break;
			}
			case '$': {
				int start = ci.getIndex();
				String s = getAtom(input, start);
				int end = start + s.length();
				ci.setIndex(end);
				result.add(new Token(Type.PROPERTY, s, start, end));
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
			default: {
				if (token.getType() == Type.EXPRESSION) {
					if (!Character.isWhitespace(ch)) {
						int start = ci.getIndex();
						String s = getAtom(input, start);
						int end = start + s.length() - 1;
						ci.setIndex(end);
						result.add(new Token(Type.IDENTIFIER, s, start, end));
					}
				} else if (!Character.isWhitespace(ch)) {
					int start = ci.getIndex();
					String s = getPartition(input, start, '@');
					int end = start + s.length() - 1;
					ci.setIndex(end);
					result.add(new Token(Type.CONTENT_BLOCK, s, start, end));
				}
				break;
			}
			}
			ch = ci.next();
		}

		// result = result.stream().filter(e -> e.getType() ==
		// Type.EXPRESSION).collect(Collectors.toList());
		for (Token t : result) {
			if (t.getType() == Type.EXPRESSION) {
				lex(t.getContent(), t.getStart(), t);
			}
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
