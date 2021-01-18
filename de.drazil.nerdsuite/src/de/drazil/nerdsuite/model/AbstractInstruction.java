package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractInstruction {
	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String description;

	@JsonIgnore
	public abstract int getIconIndex();
}
