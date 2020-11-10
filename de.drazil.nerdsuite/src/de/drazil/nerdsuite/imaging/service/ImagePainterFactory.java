package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public final static int NONE = 0;
	public final static int READ = 1;
	public final static int UPDATE = 2;
	public final static int PIXEL = 4;
	public final static int SCALED = 8;

	public final static int UPDATE_PIXEL = UPDATE + PIXEL;
	public final static int UPDATE_SCALED = UPDATE + SCALED;

	public TileRepositoryService referenceRepository;

	public ImagePainterFactory(TileRepositoryService referenceRepository) {
		this.referenceRepository = referenceRepository;
		imagePool = new HashMap<>();
	}

	public Image getGridLayer(ImagingWidgetConfiguration conf) {
		String name = conf.getGridStyle().toString();
		Image image = imagePool.get(name);
		RGB transparentColor = new RGB(0, 0, 0);
		if (null == image) {
			image = new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel);
			GC gc = new GC(image);
			gc.setBackground(new Color(new RGB(0, 0, 0)));
			gc.fillRectangle(0, 0, conf.tileWidthPixel, conf.tileHeightPixel);
			for (int x = 0; x <= conf.width * conf.tileColumns; x++) {
				for (int y = 0; y <= conf.height * conf.tileRows; y++) {
					gc.setForeground(Constants.PIXEL_GRID_COLOR);
					if (conf.gridStyle == GridType.Line) {
						gc.drawLine(x * conf.pixelSize, 0, x * conf.pixelSize,
								conf.height * conf.pixelSize * conf.tileRows);
						gc.drawLine(0, y * conf.pixelSize, conf.width * conf.pixelSize * conf.tileColumns,
								y * conf.pixelSize);
					} else {
						gc.drawPoint(x * conf.pixelSize, y * conf.pixelSize);
					}
				}
			}
			gc.dispose();
			ImageData id = image.getImageData();

			id.transparentPixel = id.palette.getPixel(transparentColor);
			image = new Image(Display.getDefault(), id);
			imagePool.put(name, image);
		}
		return image;
	}

	public Image drawSelectedTile(TileRepositoryService service, IColorPaletteProvider colorProvider,
			ImagingWidgetConfiguration conf) {

		int x = 0;
		int y = 0;
		Layer layer = service.getActiveLayer();
		int content[] = layer.getContent();
		Image image = getSelectedImage(service, colorProvider, conf);
		GC gc = new GC(image);
		for (int i = 0; i < service.getTileSize(); i++) {
			if (i % conf.width == 0 && i > 0) {
				x = 0;
				y++;
			}
			gc.setBackground(colorProvider.getColorByIndex(content[i]));
			gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
			x++;
		}
		gc.dispose();
		return image;
	}

	public Image drawPixel(TileRepositoryService service, int x, int y, IColorPaletteProvider colorProvider,
			ImagingWidgetConfiguration conf) {
		// System.out.println("draw pixel");

		Layer layer = service.getActiveLayer();
		int content[] = layer.getContent();
		Image image = getSelectedImage(service, colorProvider, conf);
		GC gc = new GC(image);
		int offset = conf.tileWidth * y + x;
		gc.setBackground(colorProvider.getColorByIndex(content[offset]));
		gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
		gc.dispose();
		return image;
	}

	public Image getSelectedImage(TileRepositoryService service, IColorPaletteProvider colorProvider,
			ImagingWidgetConfiguration conf) {
		Tile tile = service.getSelectedTile();
		String name = tile.getName();
		Image mainImage = imagePool.get(name);
		if (null == mainImage) {
			mainImage = new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel);

			GC gc = new GC(mainImage);
			gc.setBackground(colorProvider.getColorByIndex(tile.getBackgroundColorIndex()));
			gc.fillRectangle(0, 0, conf.tileWidthPixel, conf.tileHeightPixel);
			gc.dispose();
			imagePool.put(name, mainImage);
		}
		return mainImage;
	}

	private boolean checkMode(int update, int value) {
		return (update & value) == value;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

}