package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

public class ImageViewer extends ImagingWidget {

	public ImageViewer(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style, configuration);
	}

	public ImageViewer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected boolean supportsDrawCursor() {
		return true;
	}
}
