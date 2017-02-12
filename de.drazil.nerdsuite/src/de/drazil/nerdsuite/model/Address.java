package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class Address
{
	private int address;
	private String constName;
	private String description;

	public boolean matches(int value)
	{
		return address == value;
	}
}
