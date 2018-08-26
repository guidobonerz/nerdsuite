package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

public class ImageReferenceSelector extends ImageSelector {

	public ImageReferenceSelector(Composite parent, int style) {
		super(parent, style);
	}

	public ImageReferenceSelector(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style, configuration);
	}

	@Override
	protected boolean supportsMultiSelection() {
		return false;
	}

	@Override
	protected boolean supportsReferenceIndexSelection() {
		return true;
	}
}
