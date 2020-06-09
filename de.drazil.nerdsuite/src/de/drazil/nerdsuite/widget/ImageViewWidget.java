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

	public ImageViewWidget(Composite parent, int style) {
		super(parent, style);
		buffer = new ArrayList<ImageData>();
		setBackground(Constants.WHITE);
		addPaintListener(this);

	}

	public void paintControl(PaintEvent e) {
		try {
			if (!buffer.isEmpty()) {
				ImageData imageData = buffer.get(0);
				Image image = new Image(getDisplay(),
						imageData.scaledTo((int) (imageData.width * 3), (int) (imageData.height * 3)));
				e.gc.drawImage(image, 0, 0);
				image.dispose();
				buffer.remove(0);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void addImageData(byte[] data, PaletteData pd) {
		ImageData imageData = new ImageData(384, 272, 4, pd, 1, data);
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((data[i] & 0x0F) << 4 | (data[i] & 0xF0) >> 4);
		}
		buffer.add(imageData);
	}

	public void drawImage() {
		if (buffer.size() > 5) {
			redraw();
			update();
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(800, 800);
	}

}
