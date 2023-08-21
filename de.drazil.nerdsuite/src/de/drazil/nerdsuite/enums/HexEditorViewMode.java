package de.drazil.nerdsuite.enums;

public enum HexEditorViewMode {
	ADDRESS(1), BYTE(2), ASCII(1);

	private int width;

	private HexEditorViewMode(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}
}
