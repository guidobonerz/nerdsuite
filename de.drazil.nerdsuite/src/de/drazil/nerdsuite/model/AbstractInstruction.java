package de.drazil.nerdsuite.model;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractInstruction
{
	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String description;

	public abstract int getIconIndex();
}
