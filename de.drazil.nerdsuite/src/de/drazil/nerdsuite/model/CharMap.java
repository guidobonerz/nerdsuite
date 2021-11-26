package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CharMap {
	private boolean defaultCaseState;
	@JsonProperty(value = "characterMap")
	private List<CharObject> charMap;
	@JsonProperty(value = "upperCharIndexOrder")
	private List<Integer> upperIndexOrderList = null;
	@JsonProperty(value = "lowerCharIndexOrder")
	private List<Integer> lowerIndexOrderList = null;

}
