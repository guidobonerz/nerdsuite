package de.drazil.nerdsuite.widget;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class ImageViewWidget extends Canvas implements PaintListener {

	private ImageData imageData;

	public ImageViewWidget(Composite parent, int style) {
		super(parent, style);
		setBackground(Constants.WHITE);
		addPaintListener(this);

	}

	public void paintControl(PaintEvent e) {
		if (imageData != null) {
			try {
				imageData = imageData.scaledTo((int) (imageData.width * 3), (int) (imageData.height * 3));
				Image image = new Image(getDisplay(), imageData);
				e.gc.drawImage(image, 0, 0);
				image.dispose();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void setImage(ImageData imageData) {
		this.imageData = imageData;
		redraw();
		//update();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(800, 800);
	}

}
