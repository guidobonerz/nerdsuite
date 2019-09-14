package de.drazil.nerdsuite.constants;

public enum SizeVariant {
	Standard(0, "STANDARD"), DX(1, "2X"), DY(2, "2Y"), DXY(3, "2XY");

	private int id;
	private String name;

	private SizeVariant(int id, String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static SizeVariant getSizeVariantByName(String name) {
		SizeVariant result = null;
		for (SizeVariant sv : values()) {
			if (sv.getName().equalsIgnoreCase(name)) {
				result = sv;
				break;
			}
		}
		return result;
	}
}
