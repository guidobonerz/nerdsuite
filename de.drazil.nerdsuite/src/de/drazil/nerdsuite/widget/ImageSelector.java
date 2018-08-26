package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

public class ImageSelector extends ImagingWidget {

	public ImageSelector(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style, configuration);
	}

	public ImageSelector(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected boolean supportsMultiTileView() {
		return true;
	}

	@Override
	protected boolean supportsSingleSelection() {
		return true;
	}

	@Override
	protected boolean supportsMultiSelection() {
		return true;
	}
}
