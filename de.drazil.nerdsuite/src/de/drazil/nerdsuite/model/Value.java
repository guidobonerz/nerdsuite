package de.drazil.nerdsuite.model;

import de.drazil.nerdsuite.enums.ValueType;
import de.drazil.nerdsuite.util.NumericConverter;
import lombok.Getter;
import lombok.Setter;

public class Value {

	@Getter
	@Setter
	private int value;
	private ValueType valueType;

	public Value(String hexValue) {
		this(hexValue, ValueType.WORD);
	}

	public Value(String hexValue, ValueType valueType) {
		this(Integer.parseInt(hexValue, 16));
	}

	public Value(int value) {
		this(value, ValueType.WORD);
	}

	public Value(int value, ValueType valueType) {
		setValue(value);
		setValueType(valueType);
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

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
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

		switch (valueType) {
		case DWORD:
			value = NumericConverter.toHexString(this.value, 8);
			break;
		case HIGHWORD:
			value = NumericConverter.toHexString(getHighWord(), 4);
			break;
		case LOWWORD:
			value = NumericConverter.toHexString(getLowWord(), 4);
			break;
		case WORD:
			value = NumericConverter.toHexString(this.value, 4);
			break;
		case HIGHBYTE:
			value = NumericConverter.toHexString(getHighByte(), 2);
			break;
		case LOWBYTE:
			value = NumericConverter.toHexString(getLowByte(), 2);
			break;
		case BYTE:
			value = NumericConverter.toHexString(this.value, 2);
			break;
		case HIGHNIBBLE:
			value = NumericConverter.toHexString(getHighNibble(), 2);
			break;
		case LOWNIBBLE:
			value = NumericConverter.toHexString(getLowNibble(), 2);
			break;

		}
		return value;
	}
}
