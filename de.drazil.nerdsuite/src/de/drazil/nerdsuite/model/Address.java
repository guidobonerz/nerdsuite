package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Address {
	@JsonIgnore 
	private Value value;
	private String address;
	private String constName;
	private String description;

	public Address(String address, String constName, String description) {
		this.value = new Value(address);
		this.constName = constName;
		this.description = description;
	}

	public int getAddressValue() {
		return value.getValue();
	}

	public boolean matches(int value) {
		return getAddressValue() == value;
	}
}
