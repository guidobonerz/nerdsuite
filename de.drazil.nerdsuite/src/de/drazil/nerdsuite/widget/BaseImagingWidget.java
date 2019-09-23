package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

import lombok.Getter;

public abstract class BaseImagingWidget extends BaseWidget {

	@Getter
	protected ImagingWidgetConfiguration conf = null;

	public BaseImagingWidget(Composite parent, int style) {
		super(parent, style);
		conf = new ImagingWidgetConfiguration();
	}
}
