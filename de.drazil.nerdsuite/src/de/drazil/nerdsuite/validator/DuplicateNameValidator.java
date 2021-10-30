package de.drazil.nerdsuite.validator;

import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public abstract class DuplicateNameValidator<MODEL> extends BaseValidator {
	private String messagePrefix;
	private List<MODEL> list;

	public DuplicateNameValidator(String messagePrefix, List<MODEL> list) {
		super("");
		this.messagePrefix = messagePrefix;
		this.list = list;
	}

	@Override
	public IStatus validate(Object value) {
		IStatus validationStatus = ValidationStatus.ok();
		if (exists(list, value)) {
			validationStatus = ValidationStatus.error(messagePrefix + " already exist.");
		}
		return validationStatus;
	}

	protected abstract boolean exists(List<MODEL> list, Object value);
}
