package de.drazil.nerdsuite.lexer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Expression implements IElement {

	private IElement[] element;
	private boolean isRequired;

	public Expression(IElement... element) {
		this(true, element);
	}

	public Expression(boolean isRequired, IElement... element) {
		this.element = element;
		this.isRequired = isRequired;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
