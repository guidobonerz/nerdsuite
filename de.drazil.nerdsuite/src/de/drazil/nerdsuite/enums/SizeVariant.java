package de.drazil.nerdsuite.enums;

public enum SizeVariant {
	Standard(0, "STANDARD"), DX(1, "DX"), DY(2, "DY"), DXY(3, "DXY");

	private int id;
	private String name;

	private SizeVariant(int id, String name) {
		this.id = id;
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
