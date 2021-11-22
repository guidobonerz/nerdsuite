package de.drazil.nerdsuite.basic;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;

public class BasicTokenizer {

	enum Mode {
		READ_LINENUMBER, READ_INSTRUCTION, READ_STRING;
	};

	enum LastRead {
		NUMERIC, ALPHANUMERIC, WHITESPACE, NONE;
	}

	private StringBuilder sb = null;
	private ICPU cpu = null;
	private Mode readMode = Mode.READ_LINENUMBER;
	private LastRead lastRead = LastRead.NONE;

	public BasicTokenizer() {
		cpu = new CPU_6510();
		sb = new StringBuilder();
		sb.append("10 for i=1to200\n");
		sb.append("20 printi\n");
		sb.append("30 next\n");

	}

	public void tokenize() {
		CharacterIterator ci = new StringCharacterIterator(sb.toString());
		StringBuilder buffer = new StringBuilder();
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			switch (readMode) {
			case READ_LINENUMBER: {
				if (Character.isWhitespace(ch) && (lastRead == LastRead.NONE || lastRead == LastRead.WHITESPACE)) {
					lastRead = LastRead.WHITESPACE;
				} else if ((Character.isWhitespace(ch) || Character.isAlphabetic(ch)) && lastRead == LastRead.NUMERIC) {
					lastRead = LastRead.WHITESPACE;
					readMode = Mode.READ_INSTRUCTION;
					System.out.println(buffer);
					buffer = new StringBuilder();
					if (!Character.isWhitespace(ch)) {
						buffer.append(ch);
					}
				} else if (Character.isDigit(ch)) {
					lastRead = LastRead.NUMERIC;
					buffer.append(ch);
				}
				break;
			}
			case READ_INSTRUCTION: {
				if (ch == '\n') {
					readMode = Mode.READ_LINENUMBER;
				} else if (ch == '\"') {
					readMode = Mode.READ_STRING;
					System.out.println(ch);
				} else if (Character.isWhitespace(ch)
						&& (lastRead == LastRead.NONE || lastRead == LastRead.WHITESPACE)) {
					lastRead = LastRead.WHITESPACE;
				} else if (!Character.isDigit(ch) && !Character.isWhitespace(ch)) {
					buffer.append(ch);
					if (buffer.toString().equals("print")) {
						System.out.println(buffer);
						buffer = new StringBuilder();
					} else if (buffer.toString().equals("goto")) {
						System.out.println(buffer);
						buffer = new StringBuilder();
					} else if (buffer.toString().equals("for")) {
						System.out.println(buffer);
						buffer = new StringBuilder();
					} else if (buffer.toString().equals("to")) {
						System.out.println(buffer);
						buffer = new StringBuilder();
					} else if (buffer.toString().equals("next")) {
						System.out.println(buffer);
						buffer = new StringBuilder();
					}else {
						System.out.println(ch);
					}
				} else {
					System.out.println(ch);
				}
				break;
			}
			case READ_STRING: {
				System.out.println(ch);
				if (ch == '\"') {
					readMode = Mode.READ_INSTRUCTION;
				}
				break;
			}
			}
		}
	}

	public static void main(String args[]) {
		BasicTokenizer bt = new BasicTokenizer();
		bt.tokenize();
	}

}
