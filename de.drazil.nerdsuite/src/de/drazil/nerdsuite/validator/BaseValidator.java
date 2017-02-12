package de.drazil.nerdsuite.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.eclipse.core.databinding.validation.IValidator;

@AllArgsConstructor
public abstract class BaseValidator implements IValidator
{

	@Getter
	@Setter
	private String fieldName;

}
