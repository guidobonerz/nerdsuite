package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class Address {
	private String address;
	private String constName;
	private String description;

	public int getAddressValue() {
		return Integer.parseInt(address, 16);
	}

	public boolean matches(int value) {
		return getAddressValue() == value;
	}
}
