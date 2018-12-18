package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

public class ImagePainter extends ImagingWidget {

	public ImagePainter(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style, configuration);
	}

	public ImagePainter(Composite parent, int style) {
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
