package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import de.drazil.nerdsuite.disassembler.ReferenceType;
import de.drazil.nerdsuite.disassembler.Type;
import de.drazil.nerdsuite.disassembler.Value;

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
