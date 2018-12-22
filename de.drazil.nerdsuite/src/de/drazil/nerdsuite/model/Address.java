package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class Address {
	private int address;
	private String constName;
	private String description;

	public int getAddressValue() {
		return address;
	}

	public boolean matches(int value) {
		return getAddressValue() == value;
	}
}
