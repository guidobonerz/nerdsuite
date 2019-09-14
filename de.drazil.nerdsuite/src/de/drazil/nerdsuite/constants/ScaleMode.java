package de.drazil.nerdsuite.constants;

public enum ScaleMode {
	None(0, true), D2(1, false), D4(2, false), D8(3, false), D16(4, false), U2(1, true), U4(2, true), U8(3, true),
	U16(4, true);

	private int scaleFactor;
	private boolean direction;

	private ScaleMode(int scaleFactor, boolean direction) {
		this.scaleFactor = scaleFactor;
		this.direction = direction;
	}

	public int getScaleFactor() {
		return scaleFactor;
	}

	public boolean getDirection() {
		return direction;
	}
}
