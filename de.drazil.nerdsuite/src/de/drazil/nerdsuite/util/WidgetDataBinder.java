package de.drazil.nerdsuite.util;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Text;

import de.drazil.nerdsuite.validator.IValidatable;
import lombok.Getter;

public class WidgetDataBinder {
	@Getter
	private DataBindingContext dataBindingContext = null;
	private IValidatable validatable;

	public WidgetDataBinder(final IValidatable validatable) {
		this.validatable = validatable;
		dataBindingContext = new DataBindingContext();
		/*
		 * AggregateValidationStatus aggregateStatus = new
		 * AggregateValidationStatus(widgetDataBinder.getDataBindingContext().
		 * getBindings(), AggregateValidationStatus.MERGED);
		 * aggregateStatus.addValueChangeListener(new IValueChangeListener() {
		 * 
		 * @Override public void handleValueChange(ValueChangeEvent event) { IStatus
		 * status = AggregateValidationStatus.getStatusMaxSeverity(widgetDataBinder.
		 * getDataBindingContext().getValidationStatusProviders());
		 * setValidated(status.isOK()); } });
		 */

	}

	public final <MODEL> void bind(Text widget, MODEL model, String propertyName, IValidator validator) {
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(validator);
		// Binding bindValue =
		// dataBindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(widget),
		// PojoProperties.value(propertyName).observe(model), strategy, null);
		// ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);

	}
}
