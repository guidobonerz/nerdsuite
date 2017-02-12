package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class AddressingMode
{
	private String id;
	private String addressingMode;
	private String argumentTemplate;
	private int len;
}
