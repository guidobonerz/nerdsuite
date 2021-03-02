package de.drazil.nerdsuite.enums;

public enum Style {
	KEY(0, 1), KEY1_5(1, 1.5), KEY2(2, 2), KEY9(3, 9), FILLER33(4, 0.33, true), FILLER66(5, 0.66, true),
	FILLER(6, 1, true);

	private int id;
	private double size;
	private boolean isFiller;

	private Style(int id, double size) {
		this(id, size, false);

	}

	private Style(int id, double size, boolean isFiller) {
		this.id = id;
		this.size = size;
		this.isFiller = isFiller;
	}

	public int getId() {
		return id;
	}

	public double getSize() {
		return size;
	}

	public boolean isFiller() {
		return isFiller;
	}
}
