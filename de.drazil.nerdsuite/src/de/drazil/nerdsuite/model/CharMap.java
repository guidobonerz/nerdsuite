package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.drazil.nerdsuite.json.StringToUnicode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CharMap {
	private int id;
	private int ascii;
	private int screencode;
	@JsonDeserialize(converter = StringToUnicode.class)
	private char unicode;
	private String name;

}
