package de.drazil.nerdsuite.cpu.emulate;

import de.drazil.nerdsuite.enums.ValueType;
import de.drazil.nerdsuite.model.Value;

public class Watchpoint {
	private int address;
	private ValueType valueType;

	public Watchpoint(int address) {
		this(address, ValueType.BYTE);
	}

	public Watchpoint(int address, ValueType valueType) {
		this.address = address;
		this.valueType = valueType;
	}

	public Value getValue(int[] ram) {
		return new Value(ram[address], valueType);
	}
}
