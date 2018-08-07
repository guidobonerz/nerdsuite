package de.drazil.nerdsuite.disassembler;

import java.io.File;

public class Binary2SourceConverter {
	public final static int CHARSET = 1;
	public final static int CHARSET_DOUBLE_X = 2;
	public final static int CHARSET_DOUBLE_Y = 4;
	public final static int SPRITE = 8;
	public final static int MULTICOLOR = 16;

	public final static int AS_BINARY_VALUE = 10;
	public final static int AS_HEX_VALUE = 20;
	public final static int AS_DECIMAL_VALUE = 30;

	public final static char COLOR0 = '.';
	public final static char COLOR1 = 'o';
	public final static char COLOR2 = 'O';
	public final static char COLOR3 = 'A';

	public static StringBuilder convertBinaryToText(File file, int sourceFormat, int valueOutputFormat,
			boolean skipLocation, boolean writeComment) {
		checkFormatSettings(sourceFormat);
		StringBuilder sb = new StringBuilder();
		byte byteArray[] = BinaryFileReader.readFile(file, 0);
		int index = 0;
		int width = 1;
		int height = 8;
		int step = 1;
		if ((sourceFormat & CHARSET + CHARSET_DOUBLE_X + CHARSET_DOUBLE_Y) == CHARSET + CHARSET_DOUBLE_X
				+ CHARSET_DOUBLE_Y) {

		} else if ((sourceFormat & CHARSET + CHARSET_DOUBLE_Y) == CHARSET + CHARSET_DOUBLE_Y) {

		} else if ((sourceFormat & CHARSET + CHARSET_DOUBLE_X) == CHARSET + CHARSET_DOUBLE_X) {
			step = 8;
		} else if ((sourceFormat & CHARSET) == CHARSET) {

		} else if ((sourceFormat & SPRITE) == SPRITE) {
			width = 3;
			height = 21;
		}

		int rows = 0;
		while (index < byteArray.length) {
			byte b = byteArray[index];
			if (index == 0 && !skipLocation) {
				sb.append(".pc $");
				NumericConverter.toHexString(byteArray[1], sb);
				NumericConverter.toHexString(byteArray[0], sb);
				sb.append(" \"" + file.getName() + "\"\n");
				index = +2;
			}

			if (index > 1) {
				sb.append(".byte ");
				if (valueOutputFormat == AS_HEX_VALUE) {
					NumericConverter.toHexString(byteArray, index, width, step, sb);
				} else if (valueOutputFormat == AS_BINARY_VALUE) {
					NumericConverter.toBinaryString(byteArray, index, width, step, sb);
				} else if (valueOutputFormat == AS_DECIMAL_VALUE) {
					NumericConverter.toDecimalString(byteArray, index, width, step, sb);
				}

				if (writeComment) {
					sb.append(" //");
					toCommentString(byteArray, index, width, step, sourceFormat, sb);
				}
				sb.append('\n');
				index += width;
				rows++;
				if (rows >= height) {
					index++;
					rows = 0;
					sb.append("//----------------------------------------------------------\n");
				}
			}
		}
		return sb;
	}

	private static void checkFormatSettings(int format) {
		int source = CHARSET + SPRITE;
		if ((format & source) == source) {
			throw new RuntimeException("please CHARSET OR SPRITE format only");
		}
	}

	private static void toCommentString(byte b, int format, StringBuilder sb) {
		toCommentString(new byte[] { b }, 0, 1, 1, format, sb);
	}

	private static void toCommentString(byte byteArray[], int offset, int len, int step, int format, StringBuilder sb) {
		for (int i = offset; i < offset + len; i++) {
			byte cb = 1;
			char c[] = { COLOR0, COLOR0, COLOR0, COLOR0, COLOR0, COLOR0, COLOR0, COLOR0 };
			if ((format & MULTICOLOR) == MULTICOLOR) {
				byte mcByte = byteArray[i];
				for (int j = 8; j > 0; j -= 2) {
					byte x = (byte) (mcByte & 3);
					char colorSymbol = COLOR0;
					if (x == 1) {
						colorSymbol = COLOR1;
					} else if (x == 2) {
						colorSymbol = COLOR2;
					} else if (x == 3) {
						colorSymbol = COLOR3;
					}
					c[j - 1] = colorSymbol;
					c[j - 2] = colorSymbol;
					mcByte >>= 2;
				}
			} else {
				for (int j = 1; j <= 8; j++) {
					if ((byteArray[i] & cb) == cb) {
						c[8 - j] = COLOR2;
					}
					cb <<= 1;
				}
			}
			sb.append(String.valueOf(c));
		}
	}

	public static void main(String args[]) {
		StringBuilder sb = convertBinaryToText(new File("/Users/drazil/Downloads/gi-joe_major_bludd.spr"),
				SPRITE + MULTICOLOR, AS_HEX_VALUE, false, true);
		System.out.println(sb);
	}
}
