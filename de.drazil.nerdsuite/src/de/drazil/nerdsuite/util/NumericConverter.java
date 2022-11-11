package de.drazil.nerdsuite.util;

import de.drazil.nerdsuite.cpu.Endianness;

public class NumericConverter {
	public static void toDecimalString(byte b, StringBuilder sb) {
		toHexString(new byte[] { b }, 0, 1, 1, sb);
	}

	public static void toDecimalString(byte byteArray[], int offset, int len, int step, StringBuilder sb) {
		for (int i = offset; i < offset + len; i++) {
			sb.append(String.format("%03d", (byteArray[i] & 0xff)));
			if (i < offset + len - 1) {
				sb.append(',');
			}
		}
	}

	public static int toInt(byte b) {
		return (int) (b & 0xff);
	}

	public static void toHexString(byte b, StringBuilder sb) {
		toHexString(new byte[] { b }, 0, 1, 1, sb);
	}

	public static void toHexString(byte byteArray[], int offset, int len, int step, StringBuilder sb) {
		for (int i = offset; i < offset + len; i++) {
			sb.append("$" + String.format("%02x", byteArray[i]));
			if (i < offset + len - 1) {
				sb.append(',');
			}
		}
	}

	public static String toHexString(int value, int len) {
		return String.format("%0" + len + "x", value);
	}

	public static void toBinaryString(byte b, StringBuilder sb) {
		toBinaryString(new byte[] { b }, 0, 1, 1, sb);
	}

	public static void toBinaryString(byte byteArray[], int offset, int len, int step, StringBuilder sb) {
		for (int i = offset; i < offset + len; i++) {
			char c[] = { '0', '0', '0', '0', '0', '0', '0', '0' };
			byte cb = 1;
			for (int j = 1; j <= 8; j++) {
				if ((byteArray[i] & cb) == cb) {
					c[8 - j] = '1';
				}
				cb <<= 1;
			}
			sb.append("%" + String.valueOf(c));
			if (i < offset + len - 1) {
				sb.append(',');
			}
		}
	}

	public static String toBinaryString(byte byteArray[], int offset, int len) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			sb.append(String.format("%8s", Integer.toBinaryString((byteArray[offset + i] & 0xff))).replace(" ", "0"));
		}
		return sb.toString();
	}

	public static int getByteAsInt(byte byteArray[], int offset) {
		return (int) (getBytes(byteArray, offset, 1)[0] & 0xff);
	}

	public static int getWordAsInt(byte byteArray[], int offset) {
		return getWordAsInt(byteArray, offset, Endianness.LittleEndian);
	}

	public static int getWordAsInt(byte byteArray[], int offset, Endianness endianess) {
		int value = 0;
		byte[] bytes = getWord(byteArray, offset, endianess);
		value |= ((int) bytes[0] << 8);
		value |= ((int) bytes[1] & 0xff);
		value &= 0xffff;
		return value;
	}

	public static byte[] getLongWord(int value) {
		byte lo[] = getWord(value >> 16, Endianness.LittleEndian);
		byte high[] = getWord(value & 0xffff, Endianness.LittleEndian);
		return new byte[] { high[0], high[1], lo[0], lo[1] };
	}

	public static byte[] getWord(int value) {
		return getWord(value, Endianness.LittleEndian);
	}

	public static byte[] getWord(int value, Endianness endianess) {
		byte[] result = new byte[2];
		result[0] = (byte) (value >> 8 & 0xff);
		result[1] = (byte) (value & 0xff);
		if (endianess == Endianness.LittleEndian) {
			byte x = result[0];
			result[0] = result[1];
			result[1] = x;
		}
		return result;
	}

	public static byte[] getWord(byte byteArray[], int offset) {
		return getWord(byteArray, offset, Endianness.LittleEndian);
	}

	public static byte[] getWord(byte byteArray[], int offset, Endianness endianess) {
		byte[] value = getBytes(byteArray, offset, 2);
		if (endianess == Endianness.LittleEndian) {
			byte x = value[0];
			value[0] = value[1];
			value[1] = x;
		}
		return value;
	}

	public static byte getByte(byte byteArray[], int offset) {

		return getByte(byteArray, offset, Endianness.LittleEndian);
	}

	public static byte getByte(byte byteArray[], int offset, Endianness endianess) {
		if (endianess == Endianness.LittleEndian) {
			return (byte) ((byteArray[offset] & 0x0F) << 4 | (byteArray[offset] & 0xF0) >> 4);
		}
		return byteArray[offset];
	}

	public static byte[] getBytes(byte byteArray[], int offset, int len) {
		int l = len;
		int diff = offset + len - byteArray.length;
		byte bytes[] = new byte[l];
		if (diff < 0) {
			System.arraycopy(byteArray, (int) offset, bytes, 0, len);
		} else {
			for (int i = 0; i < diff; i++) {
				bytes[i] = 0;
			}
		}
		return bytes;
	}
}
