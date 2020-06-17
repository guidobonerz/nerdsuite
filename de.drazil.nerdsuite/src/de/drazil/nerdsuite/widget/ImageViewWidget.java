package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class ImageViewWidget extends Canvas implements PaintListener {

	private List<ImageData> buffer;
	private PaletteData paletteData;
	private int scaledWidth;
	private int scaledHeight;

	public ImageViewWidget(Composite parent, int style, PaletteData paletteData) {
		super(parent, style);
		this.paletteData = paletteData;
		buffer = new ArrayList<ImageData>();
		setBackground(Constants.WHITE);
		addPaintListener(this);
	}

	public void paintControl(PaintEvent e) {
		try {
			if (!buffer.isEmpty()) {
				ImageData imageData = buffer.get(0);
				for (int i = 0; i < imageData.data.length; i++) {
					imageData.data[i] = (byte) ((imageData.data[i] & 0x0F) << 4 | (imageData.data[i] & 0xF0) >> 4);
				}
				double ratio = ((double) getClientArea().width / (double) imageData.width);
				scaledWidth = getClientArea().width;
				scaledHeight = (int) (imageData.height * ratio);
				Image image = new Image(getDisplay(), imageData.scaledTo(scaledWidth, scaledHeight));

				e.gc.drawImage(image, 0, 0);
				image.dispose();
				buffer.remove(0);
			}
		} catch (Exception e1) {
		}
	}

	public void addImageData(byte[] data) {
		buffer.add(new ImageData(384, 272, 4, paletteData, 1, data));
	}

	public void drawImage() {
		if (buffer.size() > 2) {
			redraw(0, 0, scaledWidth, scaledHeight, false);
			update();
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(800, 800);
	}

}
