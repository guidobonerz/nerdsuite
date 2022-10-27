package de.drazil.nerdsuite.lexer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Token {
	@Getter
	@Setter
	private Type type;
	@Getter
	@Setter
	private int offset;
	@Getter
	@Setter
	private int length;

}
