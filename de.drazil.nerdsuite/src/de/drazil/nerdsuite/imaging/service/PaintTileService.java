package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.constants.PencilMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public class PaintTileService extends AbstractImagingService {

	@Setter
	private TileRepositoryService tileRepistoryService;
	@Setter
	private ImagePainterFactory imagePainteFactory;

	public void setPixel(Tile tile, int x, int y, ImagingWidgetConfiguration conf) {
		Layer layer = tile.getActiveLayer();

		switch (conf.paintMode) {
		case Single: {
			setPixel(layer, x, y, conf);
			break;
		}
		case VerticalMirror: {
			setPixel(layer, x, y, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diff = centerX - x - 1;
			setPixel(layer, centerX + diff, y, conf);
			break;
		}
		case HorizontalMirror: {
			setPixel(layer, x, y, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diff = centerY - y - 1;
			setPixel(layer, x, centerY + diff, conf);
			break;
		}
		case Kaleidoscope: {
			setPixel(layer, x, y, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diffX = centerX - x - 1;
			setPixel(layer, centerX + diffX, y, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diffY = centerY - y - 1;
			setPixel(layer, x, centerY + diffY, conf);
			setPixel(layer, centerX + diffX, centerY + diffY, conf);
			break;
		}
		}
	}

	private void setPixel(Layer layer, int x, int y, ImagingWidgetConfiguration conf) {
		if (x >= 0 && y >= 0 && x < conf.tileWidth && y < conf.tileHeight) {
			System.out.println(x + ":" + y);
			layer.getContent()[y * conf.tileWidth + x] = (conf.pencilMode == PencilMode.Draw)
					? layer.getSelectedColorIndex()
					: 0;
		}
	}

	public void paintPixel(GC gc, Tile tile, int x, int y, ImagingWidgetConfiguration conf) {
		gc.drawImage(imagePainteFactory.getImage(tile, x, y, true, conf), 0, 0);
	}

	public void paintTile(GC gc, Tile tile, ImagingWidgetConfiguration conf) {
		gc.drawImage(imagePainteFactory.getImage(tile, 0, 0, false, conf), 0, 0);
	}

	public void paintAllTiles(Composite parent, GC gc, boolean singleTilePainter, ImagingWidgetConfiguration conf) {
		System.out.println("paint all tiles");
		int x = 0;
		int y = 0;
		int parentWidth = parent.getBounds().width;
		System.out.println(parentWidth);
		if (singleTilePainter) {
			paintTile(gc, tileRepistoryService.getSelectedTile(), conf);
		} else {
			for (int i = 0; i < tileRepistoryService.getSize(); i++) {
				Tile tile = tileRepistoryService.getTile(i);
				Image image = imagePainteFactory.getImage(tile, 0, 0, false, conf);
				int imageWidth = image.getBounds().width;
				int imageHeight = image.getBounds().height;
				// image = new
				// Image(Display.getDefault(),image.getImageData().scaledTo(conf.getFullWidthPixel()
				// / 2, conf.getFullHeightPixel() / 2));
				gc.drawImage(image, x, y);
				x += imageWidth;
				System.out.println("parentwidth:" + parentWidth);
				System.out.println("imageWidth:" + imageWidth);
				int imagePerRow = (int) (parentWidth / imageWidth);

				System.out.println("image per row:" + imagePerRow);
				if ((i + 1) % imagePerRow == 0) {
					System.out.println("wrap");
					y += imageHeight;
					x = 0;
				}
			}
		}
	}
}
