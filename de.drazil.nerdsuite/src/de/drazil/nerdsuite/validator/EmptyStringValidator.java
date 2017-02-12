package de.drazil.nerdsuite.validator;

import java.text.MessageFormat;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class EmptyStringValidator extends BaseValidator
{
	public EmptyStringValidator(String fieldName)
	{
		super(fieldName);
	}

	@Override
	public IStatus validate(Object value)
	{
		IStatus validationStatus = ValidationStatus.ok();
		if (value != null && value instanceof String)
		{
			String s = (String) value;
			if (s.length() == 0)
			{
				validationStatus = ValidationStatus.error(MessageFormat.format("{1} MUST NOT be empty", getFieldName()));
			}
		}
		return validationStatus;
	}
}
