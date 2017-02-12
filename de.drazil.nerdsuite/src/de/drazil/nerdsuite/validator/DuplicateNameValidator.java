package de.drazil.nerdsuite.validator;

import java.util.Map;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class DuplicateNameValidator<MODEL> extends BaseValidator
{
	private String messagePrefix;
	private Map<String, MODEL> map;;

	public DuplicateNameValidator(String messagePrefix, Map<String, MODEL> map)
	{
		super("");
		this.messagePrefix = messagePrefix;
		this.map = map;
	}

	@Override
	public IStatus validate(Object value)
	{
		IStatus validationStatus = ValidationStatus.ok();
		if (value != null && value instanceof String)
		{
			String s = (String) value;

			if (map.get(s.toUpperCase()) != null)
			{
				validationStatus = ValidationStatus.error(messagePrefix + " already exist.");
			}
		}
		return validationStatus;
	}
}
