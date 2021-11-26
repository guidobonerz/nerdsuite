package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.drazil.nerdsuite.json.StringToUnicode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CharObject {
	private int id;
	private int screencode;
	@JsonDeserialize(converter = StringToUnicode.class)
	private char unicode;
	private String name;
	private boolean isColor;
	private boolean isControl;
	private boolean isUpper;
	private String customValue;

}
