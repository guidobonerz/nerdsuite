package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

public class ImagePainter2 extends ImagingWidget2 {
	public ImagePainter2(Composite parent, int style, ImagingWidgetConfiguration2 configuration) {
		super(parent, style, configuration);
	}

	public ImagePainter2(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected boolean supportsPainting() {
		return true;
	}

	@Override
	protected boolean supportsDrawCursor() {
		return true;
	}
}
