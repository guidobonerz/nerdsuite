package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class AsciiMap {
	private int id;
	private int ascii;
	private int screenCode;
	private String unicode;
}
