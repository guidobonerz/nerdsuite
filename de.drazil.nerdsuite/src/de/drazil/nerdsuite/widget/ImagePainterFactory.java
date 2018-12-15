package de.drazil.nerdsuite.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
	}

	public Image getImage(Display display, int index, boolean needsUpdate) {
		Image image = imagePool.get("ICON-" + index);
		if (null == image || needsUpdate) {
			image = createOrUpdateImage(display, index);
			imagePool.put("ICON-" + index, image);
			System.out.println("create new ICON-" + index);
		}
		return image;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image createOrUpdateImage(Display display, int index) {
		Image image = new Image(display, 10, 10);
		// ImageData id = image.getImageData().scaledTo(10, 10);

		GC gc = new GC(image);
		gc.setBackground(new Color(Display.getCurrent(), 255 - index, 127+(index/2), index));
		gc.fillRectangle(0, 0, 10, 10);
		// gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		// gc.drawString(String.valueOf(index), 0, 0);
		gc.dispose();
		return image;
	}
}
