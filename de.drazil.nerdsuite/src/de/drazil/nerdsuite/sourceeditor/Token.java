package de.drazil.nerdsuite.sourceeditor;

import lombok.Data;
import lombok.NonNull;

@Data
public class Token {

	@NonNull
	private String key;

	public Token(String key) {
		this.key = key;
	}
}
