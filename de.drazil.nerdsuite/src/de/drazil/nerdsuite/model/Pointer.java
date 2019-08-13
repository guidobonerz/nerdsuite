package de.drazil.nerdsuite.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Pointer {
	private String constName;
	private String description;
	private Value address;
	private Type type;
	private ReferenceType referenceType;

	public Pointer(String address, String constName, String description) {
		this.address = new Value(address);
		this.constName = constName;
		this.description = description;
	}

	public Pointer(Value address, Type type, ReferenceType referenceType) {
		this.address = address;
		this.type = type;
		this.referenceType = referenceType;
	}

	public boolean matches(Value value) {
		return value.matches(value);
	}

}
