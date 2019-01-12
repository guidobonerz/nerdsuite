package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

public class ImageRepository extends ImagingWidget2 {

	public ImageRepository(Composite parent, int style, ImagingWidgetConfiguration2 configuration) {
		super(parent, style, configuration);
	}

	public ImageRepository(Composite parent, int style) {
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

	@Override
	protected boolean supportsDrawCursor() {
		return true;
	}
}
