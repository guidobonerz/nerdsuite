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

	private ImagingWidgetConfiguration conf;
	private IColorPaletteProvider colorProvider;

	public ImagePainterFactory(ImagingWidgetConfiguration conf, IColorPaletteProvider colorProvider) {
		imagePool = new HashMap<>();
		this.conf = conf;
		this.colorProvider = colorProvider;
	}

	public void resetCache() {
		for (Image i : imagePool.values()) {
			i.dispose();
		}
		imagePool.clear();
	}

	public Image getGridLayer(ImagingWidgetConfiguration conf) {
		String name = conf.getGridStyle().toString();
		Image image = imagePool.get(name);
		RGB transparentColor = new RGB(0, 0, 0);
		if (null == image) {
			image = new Image(Display.getDefault(), conf.fullWidthPixel, conf.fullHeightPixel);
			GC gc = new GC(image);
			gc.setBackground(new Color(new RGB(0, 0, 0)));
			gc.fillRectangle(0, 0, conf.tileWidthPixel, conf.tileHeightPixel);
			gc.setForeground(conf.gridStyle == GridType.Line ? Constants.LINE_GRID_COLOR : Constants.PIXEL_GRID_COLOR);
			for (int x = 0; x <= conf.width * conf.tileColumns; x++) {
				for (int y = 0; y <= conf.height * conf.tileRows; y++) {

					if (conf.gridStyle == GridType.Line) {
						gc.drawLine(x * conf.pixelSize, 0, x * conf.pixelSize, conf.height * conf.pixelSize * conf.tileRows);
						gc.drawLine(0, y * conf.pixelSize, conf.width * conf.pixelSize * conf.tileColumns, y * conf.pixelSize);
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

	public Image drawSelectedTile(TileRepositoryService service, TileRepositoryService referenceService) {
		return drawTile(service, referenceService, null, service.getSelectedTileIndex());
	}

	public Image drawTile(TileRepositoryService service, TileRepositoryService referenceService, ImagePainterFactory referenceImageFactory, int index) {
		int x = 0;
		int y = 0;
		Tile tile = service.getTile(index);
		String name = tile.getName();
		Layer layer = service.getActiveLayer(index);
		int content[] = layer.getContent();
		Image image = imagePool.get(name);
		if (image == null) {
			image = createBaseImage(colorProvider.getColorByIndex(0), conf.tileWidthPixel, conf.tileHeightPixel);
			GC gc = new GC(image);
			for (int i = 0; i < service.getTileSize(); i++) {
				if (i % conf.tileWidth == 0 && i > 0) {
					x = 0;
					y++;
				}
				if (referenceImageFactory != null) {
					int bi = layer.getBrush()[i];
					Image refImage = referenceImageFactory.drawTile(referenceService, null, referenceImageFactory, bi);
					gc.drawImage(refImage, x * conf.currentPixelWidth, y * conf.currentPixelHeight);
				} else {
					gc.setBackground(colorProvider.getColorByIndex(content[i]));
					gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
				}
				x++;
			}
			gc.dispose();
			imagePool.put(name, image);
		}
		return image;
	}

	public Image drawPixel(TileRepositoryService service, TileRepositoryService referenceService, ImagePainterFactory referenceImageFactory, int x, int y) {
		return drawPixel(service, referenceService, service.getSelectedTile(), referenceImageFactory, x, y);
	}

	public Image drawPixel(TileRepositoryService service, TileRepositoryService referenceService, Tile tile, ImagePainterFactory referenceImageFactory, int x, int y) {
		String name = tile.getName();
		Layer layer = service.getActiveLayer(tile);
		int content[] = layer.getContent();
		Image image = imagePool.get(name);
		if (image == null) {
			image = createBaseImage(colorProvider.getColorByIndex(layer.getSelectedColorIndex()), conf.fullWidthPixel, conf.fullHeightPixel);
			imagePool.put(name, image);
		}
		GC gc = new GC(image);
		int offset = conf.tileWidth * y + x;
		if (referenceImageFactory != null) {
			int index = layer.getBrush()[offset];
			Image refImage = referenceImageFactory.drawTile(referenceService, null, referenceImageFactory, index);
			gc.drawImage(refImage, x * conf.currentPixelWidth, y * conf.currentPixelHeight);
		} else {
			gc.setBackground(colorProvider.getColorByIndex(content[offset]));
			gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
		}
		gc.dispose();

		return image;
	}

	public Image drawTileMap(TileRepositoryService service, ImagePainterFactory referenceImageFactory, int tileGap, Color color, boolean naturalOrder) {
		conf.computeSizes();
		Image baseImage = createBaseImage(color, conf.fullWidthPixel + (conf.columns * tileGap), conf.fullHeightPixel + (conf.rows * tileGap));
		GC gc = new GC(baseImage);
		for (int i = 0; i < service.getSize(); i++) {
			Image tileImage = drawTile(service, null, null, i);
			int y = (i / conf.columns) * (conf.tileHeightPixel + tileGap);
			int x = (i % conf.columns) * (conf.tileWidthPixel + tileGap);
			gc.drawImage(tileImage, x, y);
		}
		gc.dispose();
		return baseImage;
	}

	private Image createBaseImage(Color color, int width, int height) {
		Image image = new Image(Display.getDefault(), width, height);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRectangle(0, 0, width, height);
		gc.dispose();
		return image;
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