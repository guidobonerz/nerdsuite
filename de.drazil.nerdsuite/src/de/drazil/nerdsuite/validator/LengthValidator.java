package de.drazil.nerdsuite.validator;

import java.text.MessageFormat;

import lombok.Getter;
import lombok.Setter;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class LengthValidator extends BaseValidator
{
	@Getter
	@Setter
	private int lengthA;
	@Getter
	@Setter
	private int lengthB;
	@Getter
	@Setter
	private CheckType checkType;

	public enum CheckType
	{
		Min, Max, Both
	};

	public LengthValidator(String fieldName, int length, CheckType checkType)
	{
		this(fieldName, length, 0, checkType);
	}

	public LengthValidator(String fieldName, int minLength, int maxLength)
	{
		this(fieldName, minLength, maxLength, CheckType.Both);
	}

	private LengthValidator(String fieldname, int minLength, int maxLength, CheckType checkType)
	{
		super(fieldname);
		lengthA = minLength;
		lengthB = maxLength;
		this.checkType = checkType;
	}

	@Override
	public IStatus validate(Object value)
	{
		IStatus validationStatus = ValidationStatus.ok();
		if (value != null && value instanceof String)
		{
			String s = (String) value;
			if (checkType == CheckType.Max)
			{
				if (s.length() > lengthA)
				{
					validationStatus = ValidationStatus.error(MessageFormat.format("{0} has to be a maximum length of {1}", getFieldName(), lengthA));
				}
			}
			else if (checkType == CheckType.Min)
			{
				if (s.length() < lengthA)
				{
					validationStatus = ValidationStatus.error(MessageFormat.format("{0} has to be a minimum length of {1}", getFieldName(), lengthA));
				}
			}
			else if (checkType == CheckType.Both)
			{
				if (s.length() < lengthA || s.length() > lengthB)
				{
					validationStatus = ValidationStatus.error(MessageFormat.format("{0} has to be a length between {1} and {2}", getFieldName(), lengthA, lengthB));
				}
			}
		}
		return validationStatus;
	}

}
