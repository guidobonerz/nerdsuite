package de.drazil.nerdsuite.basic.encode;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.CharObject;

public abstract class AbstractTokenEncoder implements ITokenEncoder {

	enum Mode {
		READ_LINENUMBER, READ_INSTRUCTIONS, READ_STRING, READ_BLOCK_COMMENT, READ_LINE_COMMENT, READ_DEBUG_BLOCK;
	};

	enum LastRead {
		NUMERIC, ALPHANUMERIC, LETTER, WHITESPACE, NONE;
	}

	protected Pattern directivePattern = Pattern.compile("(@[a-zA-Z]*)\\s*([a-zA-Z]*)?");
	protected CharacterIterator ci;
	protected boolean isInDataLine = false;
	protected boolean isInDebugMode = false;
	protected boolean isInIfBlock = false;
	protected boolean isInElseBlock = false;

	protected byte[] mapUniCodeCharacters(StringBuilder sb, List<CharObject> charMap) {

		byte[] ba = new byte[sb.length()];
		CharacterIterator ci = new StringCharacterIterator(sb.toString());
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			boolean found = false;
			for (CharObject cm : charMap) {
				int i = Character.getType(ch);
				if (ch == cm.getUnicode() && i == Character.PRIVATE_USE) {
					ba[ci.getIndex()] = (byte) cm.getId();
					found = true;
					break;
				}
			}
			if (!found) {
				ba[ci.getIndex()] = (byte) ch;
			}
		}
		return ba;
	}

	protected void processMetaDirective(String content, int startIndex, boolean lineOnly) {

		Matcher matcher = directivePattern.matcher(content);
		if (lineOnly) {
			int end = content.indexOf('\r', startIndex);
			matcher.region(startIndex, end != -1 ? end : content.length() - 1);
		} else {
			matcher.region(startIndex, content.length());
		}
		if (matcher.find()) {
			int count = matcher.groupCount();
			String m1 = matcher.group(1);
			String m2 = matcher.group(2);
			if (count == 2 && m2.equals("")) {
				if (matcher.group(1).equalsIgnoreCase("@end")) {
					ci.setIndex(matcher.start() + matcher.group(1).length() + 1);
					if (!isInIfBlock) {
						Console.println("@END without @IF");
						return;
					} else {
						isInIfBlock = false;
						isInElseBlock = false;
					}
				}
				if (matcher.group(1).equalsIgnoreCase("@else")) {
					ci.setIndex(matcher.start() + matcher.group(1).length() + 1);
					if (!isInIfBlock) {
						Console.println("@ELSE without @IF");
						return;
					} else {
						isInElseBlock = true;
						if (isInDebugMode) {
							processMetaDirective(content, ci.getIndex(), false);
						}
					}
				}
			} else if (count == 2 && !m1.equals(matcher) && !m2.equals(matcher)) {
				if (matcher.group(1).equalsIgnoreCase("@if") && matcher.group(2).equalsIgnoreCase("debug")) {
					isInIfBlock = true;
					ci.setIndex(startIndex + matcher.group().length() + 1);
					if (!isInDebugMode) {
						processMetaDirective(content, ci.getIndex(), false);
					}
				}
			}
		}

	}

}
