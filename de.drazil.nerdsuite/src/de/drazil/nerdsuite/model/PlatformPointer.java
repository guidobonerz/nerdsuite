package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlatformPointer
{
	private int address;
	private Type type;
	private ReferenceType referenceType;

	public boolean matches(int value)
	{
		return value == address;
	}
}
