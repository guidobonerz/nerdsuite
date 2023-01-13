package de.drazil.nerdsuite.cpu.emulate;

import de.drazil.nerdsuite.enums.ValueType;
import de.drazil.nerdsuite.model.Value;

public class Watch {
	private int address;
	private ValueType valueType;

	public Watch(int address) {
		this(address, ValueType.BYTE);
	}

	public Watch(int address, ValueType valueType) {
		this.address = address;
		this.valueType = valueType;
	}

	public Value getValue(int[] ram) {
		return new Value(ram[address], valueType);
	}
}
