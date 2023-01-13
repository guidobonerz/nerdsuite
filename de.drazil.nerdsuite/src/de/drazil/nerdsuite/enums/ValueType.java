package de.drazil.nerdsuite.enums;

public enum ValueType {

	DWORD(4, true, false), HIGHWORD(0, false, true), LOWWORD(0, false, false), WORD(2, true, false),
	HIGHBYTE(0, false, true), LOWBYTE(0, false, false), BYTE(1, true, false), HIGHNIBBLE(0, false, true),
	LOWNIBBLE(0, false, false);

	private int size;
	private boolean complete;
	private boolean high;

	private ValueType(int size, boolean complete, boolean high) {
		this.size = size;
		this.complete = complete;
		this.high = high;
	}

	public int getSize() {
		return size;
	}

	public boolean isComplete() {
		return complete;
	}

	public boolean isHigh() {
		return high;
	}
}
