package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Key {
	private int id;
	private String type;
	private Integer index;
	private boolean toggleButton;
	private boolean symbol;
	private String name;
	private double size;
	private String text;
	private String[] codeOptions;
	private String[][] displayOptions;
	@JsonIgnore
	private boolean toggleState;
	@JsonIgnore
	private int optionState;

	@JsonIgnore
	public String getDisplay() {
		String value = "";
		if (displayOptions != null) {
			int mode = 0;
			int option = 0;
			if ((optionState & 12) == 12) {
				mode = 3;
			} else if ((optionState & 8) == 8) {
				mode = 2;
			} else if ((optionState & 4) == 4) {
				mode = 1;
			}
			if ((optionState & 1) == 1) {
				option = 1;
			} else if ((optionState & 2) == 2) {
				option = 2;
			}
			value = displayOptions[option][mode];
			//if (isSymbol()) {
			//	value = text;
			//}
		}
		return value;
	}

	public int getCode() {
		int value = 0;
		if (codeOptions != null && codeOptions[0] != null) {
			int option = 0;
			if ((optionState & 1) == 1) {
				option = 1;
			} else if ((optionState & 2) == 2) {
				option = 2;
			}
			value = Integer.parseInt(codeOptions[option]);
		}
		return value;
	}
}
