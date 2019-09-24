package de.drazil.nerdsuite.enums;

public enum ScaleMode {
	None(0, true, ""), D2(1, false, "D2"), D4(2, false, "D4"), D8(3, false, "D8"), D16(4, false, "D16"),
	U2(1, true, "U2"), U4(2, true, "U4"), U8(3, true, "U8"), U16(4, true, "U16");

	private int scaleFactor;
	private boolean direction;
	private String name;

	private ScaleMode(int scaleFactor, boolean direction, String name) {
		this.scaleFactor = scaleFactor;
		this.direction = direction;
		this.name = name;
	}

	public int getScaleFactor() {
		return scaleFactor;
	}

	public boolean getDirection() {
		return direction;
	}

	public String getName() {
		return name;
	}
}
