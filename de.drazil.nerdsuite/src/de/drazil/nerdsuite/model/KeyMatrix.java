package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class KeyMatrix {
	private int maxOptions;
	private List<KeyRow> keyRows;
}
