package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.drazil.nerdsuite.util.ImageFactory;
import lombok.Data;

public class ImageViewWidget extends Canvas implements PaintListener {

	private List<ID> buffer;
	private PaletteData paletteData;
	private int scaledWidth;
	private int scaledHeight;
	private boolean showDummy;

	private int skipCount;

	@Data
	private class ID {

		private boolean processed;
		private ImageData imageData;

		public ID(ImageData imageData) {
			this.imageData = imageData;
			processed = false;
		}
	}

	public ImageViewWidget(Composite parent, int style, PaletteData paletteData) {
		super(parent, style);

		showDummy = true;
		this.paletteData = paletteData;
		buffer = new ArrayList<ID>();
		addPaintListener(this);
		getParent().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {

				scaledHeight = getParent().getSize().y;
				scaledWidth = (int) (getParent().getSize().y * 1.411);
				getParent().layout(true, true);
			}
		});
	}

	public void paintControl(PaintEvent e) {
		try {
			if (!showDummy) {
				if (skipCount > 0) {
					skipCount--;
					buffer.remove(0);
				}
				if (!buffer.isEmpty() && buffer.size() > 10 && skipCount == 0) {
					ID id = buffer.get(0);
					if (!id.isProcessed()) {
						ImageData imageData = id.getImageData();
						for (int i = 0; i < imageData.data.length; i++) {
							imageData.data[i] = (byte) ((imageData.data[i] & 0x0F) << 4
									| (imageData.data[i] & 0xF0) >> 4);
						}

						double ratio = ((double) getParent().getSize().y / (double) id.getImageData().height);
						scaledWidth = (int) (id.getImageData().width * ratio);
						scaledHeight = getParent().getSize().y;

						Image image = new Image(getDisplay(), id.getImageData().scaledTo(scaledWidth, scaledHeight));

						e.gc.drawImage(image, 0, 0);
						image.dispose();
						buffer.remove(0);
						id.setProcessed(true);
					}
				}
			} else {
				skipCount = 20;
				ImageData imageData = ImageFactory.createImage("images/FuBK-Testbild.png").getImageData();
				double ratio = ((double) getParent().getSize().y / (double) imageData.height);
				scaledWidth = (int) (imageData.width * ratio);
				scaledHeight = getParent().getSize().y;
				Image image = new Image(getDisplay(), imageData.scaledTo(scaledWidth, scaledHeight));
				e.gc.drawImage(image, 0, 0);
				image.dispose();
			}
		} catch (

		Exception e1) {
		}
	}

	public void addImageData(byte[] data) {
		buffer.add(new ID(new ImageData(384, 272, 4, paletteData, 1, data)));
	}

	public void drawImage(boolean showDummy) {
		if (!isDisposed()) {
			scaledWidth = getClientArea().width;
			scaledHeight = getClientArea().height;
			this.showDummy = showDummy;
			redraw(0, 0, scaledWidth, scaledHeight, false);
			update();
		}

	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(scaledWidth, scaledHeight);
	}

}
