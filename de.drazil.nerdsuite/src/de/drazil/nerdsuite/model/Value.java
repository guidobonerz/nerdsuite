package de.drazil.nerdsuite.model;

import de.drazil.nerdsuite.util.NumericConverter;
import lombok.Getter;
import lombok.Setter;

public class Value {
	public final static int DWORD = 1;
	public final static int WORD = 2;
	public final static int HIGHWORD = 21;
	public final static int LOWWORD = 22;
	public final static int BYTE = 3;
	public final static int HIGHBYTE = 31;
	public final static int LOWBYTE = 32;
	public final static int HIGHNIBBLE = 33;
	public final static int LOWNIBBLE = 34;

	@Getter
	@Setter
	private int value;
	private int mode;

	public Value(String hexValue) {
		this(hexValue, WORD);
	}

	public Value(String hexValue, int mode) {
		this(Integer.parseInt(hexValue, 16));
	}

	public Value(int value) {
		this(value, WORD);
	}

	public Value(int value, int mode) {
		setValue(value);
		setMode(mode);
	}

	public Value add(int x) {
		return new Value(value + x);
	}

	public Value sub(int x) {
		return new Value(value - x);
	}

	public Value sub(Value x) {
		return sub(x.getValue());
	}

	public Value add(Value x) {
		return add(x.getValue());
	}

	public boolean matches(Value x) {
		return value == x.getValue();
	}

	public void clear() {
		value = 0;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getLowByte() {
		return value & 0xff;
	}

	public int getHighByte() {
		return ((value >> 8) & 0xff);
	}

	public int getLowNibble() {
		return value & 0x0f;
	}

	public int getHighNibble() {
		return ((value >> 4) & 0x0f);
	}

	public int getLowWord() {
		return value & 0xffff;
	}

	public int getHighWord() {
		return ((value >> 16) & 0xffff);
	}

	@Override
	public String toString() {
		String value = "";
		switch (mode) {
		case DWORD:
			value = NumericConverter.toHexString(this.value, 8);
			break;
		case WORD:
			value = NumericConverter.toHexString(this.value, 4);
			break;
		case BYTE:
			value = NumericConverter.toHexString(this.value, 2);
			break;
		case HIGHBYTE:
			value = "HighByte of " + NumericConverter.toHexString(this.value, 4);
			break;
		case LOWBYTE:
			value = "LowByte of " + NumericConverter.toHexString(this.value, 4);
			break;
		}
		return value;
	}
}
