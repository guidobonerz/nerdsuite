package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

import lombok.Getter;

public abstract class BaseImagingWidget extends BaseWidget {

	@Getter
	protected ImagingWidgetConfiguration conf = null;

	private final static class DefaultImageingWidgetConfiguration extends ImagingWidgetConfiguration {

	}

	public BaseImagingWidget(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style);
		conf = configuration == null ? new DefaultImageingWidgetConfiguration() : configuration;
	}
}
