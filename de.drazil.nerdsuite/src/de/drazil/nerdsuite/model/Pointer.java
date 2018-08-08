package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Pointer
{
	private Value address;
	private Type type;
	private ReferenceType referenceType;

	public boolean matches(Value value)
	{
		return value.matches(value);
	}

}
