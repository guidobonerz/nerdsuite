package de.drazil.nerdsuite.lexer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class MetaCodeLexer {

	public MetaCodeLexer() {

	}

	private static String content = "    10 print\"hallo\":a=1\n" + "@if ${debug}\n" + "20 poke 53280,1:poke53281,0\n"
			+ "30 print\"das ist ein test\"\n" + "@end\n" + "40 a=1:b=2:c=3\n " + "@asm name='test'\n" + "lda $#01\n"
			+ "sta $d020\n" + "  @end   ";

	public static String getAtom(String s, int i) {
		int j = i;
		for (; j < s.length();) {
			if (Character.isLetter(s.charAt(j))) {
				j++;
			} else {
				return s.substring(i, j);
			}
		}
		return s.substring(i, j);
	}

	public static String getLineToEOL(String s, int start) {
		int j = start;
		for (; j < s.length();) {
			if (s.charAt(j) != '\n') {
				j++;
			} else {
				return s.substring(start, j);
			}
		}
		return s.substring(start, j);
	}

	public static List<Token> lex(String input) {
		return lex(input, 0, input.length());
	}

	public static List<Token> lex(String input, int start, int length) {
		List<Token> result = new ArrayList<Token>();
		CharacterIterator ci = new StringCharacterIterator(input.substring(start, start + length));
		char ch = ci.first();

		while (ch != CharacterIterator.DONE) {
			switch (ch) {
			case '@': {
				int index = ci.getIndex();
				String s = getLineToEOL(input, ci.getIndex());
				result.add(new Token(Type.CODE, s));
				ci.setIndex(index + s.length());
				break;
			}
			default: {
				if (!Character.isWhitespace(ch)) {
					int index = ci.getIndex();
					String atom = getLineToEOL(input, index);
					ci.setIndex(index + atom.length());
					result.add(new Token(Type.CONTENT_BLOCK, atom));
				}
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
			System.out.println(t);
		}
	}
}
