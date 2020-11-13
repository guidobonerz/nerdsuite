package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;

public class ImagePainterFactory {

	public static interface IPainter {
		public void paint(GC gc);
	}

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
	private final static Map<String, ImagePainterFactory> cache = new HashMap<String, ImagePainterFactory>();

	public ImagePainterFactory(String name, ImagingWidgetConfiguration conf, IColorPaletteProvider colorProvider) {
		imagePool = new HashMap<>();
		this.conf = conf;
		this.colorProvider = colorProvider;
		cache.put(name, this);
	}

	public final static ImagePainterFactory getImageFactory(String name) {
		return cache.get(name);
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
		if (null == image) {
			image = createTransparentLayer(conf.fullWidthPixel, conf.fullHeightPixel, new ImagePainterFactory.IPainter() {
				@Override
				public void paint(GC gc) {
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
				}
			});
			imagePool.put(name, image);
		}
		return image;
	}

	private Image createTransparentLayer(int width, int height, IPainter painter) {
		Image image = new Image(Display.getDefault(), conf.fullWidthPixel, conf.fullHeightPixel);
		GC gc = new GC(image);
		gc.setBackground(Constants.TRANSPARENT_COLOR);
		gc.fillRectangle(0, 0, width, height);
		painter.paint(gc);
		gc.dispose();
		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(Constants.TRANSPARENT_COLOR.getRGB());
		image = new Image(Display.getDefault(), imageData);
		return image;
	}

	public Image drawSelectedTile(TileRepositoryService service, TileRepositoryService referenceService) {
		return drawTile(service, referenceService, service.getSelectedTileIndex(), 255, true);
	}

	public Image drawTile(TileRepositoryService service, TileRepositoryService refService, int index, int colorIndex, boolean useColorIndex) {
		int x = 0;
		int y = 0;

		String name = service.getTileName(index);
		String internalName = String.format("%s_C%d", name, colorIndex);
		Layer layer = service.getActiveLayerFromTile(index);
		int content[] = layer.getContent();
		Image image = imagePool.get(internalName);
		if (image == null) {
			image = createBaseImage(colorProvider.getColorByIndex(0), conf.tileWidthPixel, conf.tileHeightPixel);
			GC gc = new GC(image);
			for (int i = 0; i < service.getTileSize(); i++) {
				if (i % conf.tileWidth == 0 && i > 0) {
					x = 0;
					y++;
				}
				if (refService != null) {
					int brushIndex = 0;
					if (layer.getBrush() != null) {
						brushIndex = layer.getBrush()[i];
					}
					ImagePainterFactory factory = ImagePainterFactory.getImageFactory(service.getMetadata().getReferenceRepositoryId());
					Image refImage = factory.drawTile(refService, null, brushIndex, content[i], useColorIndex);
					gc.drawImage(refImage, x * conf.currentPixelWidth, y * conf.currentPixelHeight);
				} else {
					int ci = content[i];
					if (useColorIndex & ci > 0) {
						ci = colorIndex;
					}
					gc.setBackground(colorProvider.getColorByIndex(ci));
					gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
				}
				x++;
			}
			gc.dispose();
			imagePool.put(internalName, image);
		}
		return image;
	}

	public Image drawPixel(TileRepositoryService service, TileRepositoryService refService, int x, int y) {
		String name = service.getSelectedTileName() + "_C255";
		Layer layer = service.getActiveLayerFromSelectedTile();
		int content[] = layer.getContent();
		Image image = imagePool.get(name);
		if (image == null) {
			image = createBaseImage(colorProvider.getColorByIndex(layer.getSelectedColorIndex()), conf.fullWidthPixel, conf.fullHeightPixel);
			imagePool.put(name, image);
		}
		GC gc = new GC(image);
		int offset = conf.tileWidth * y + x;
		if (refService != null) {
			int brushIndex = layer.getBrush()[offset];
			int colorIndex = layer.getContent()[offset];
			ImagePainterFactory factory = ImagePainterFactory.getImageFactory(service.getMetadata().getReferenceRepositoryId());
			Image refImage = factory.drawTile(refService, null, brushIndex, colorIndex, true);
			gc.drawImage(refImage, x * conf.currentPixelWidth, y * conf.currentPixelHeight);
		} else {
			gc.setBackground(colorProvider.getColorByIndex(content[offset]));
			gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
		}
		gc.dispose();

		return image;
	}

	public Image drawTileMap(TileRepositoryService service, TileRepositoryService refService, int tileGap, Color color, boolean naturalOrder) {
		conf.computeSizes();
		Image baseImage = createBaseImage(color, conf.fullWidthPixel + (conf.columns * tileGap), conf.fullHeightPixel + (conf.rows * tileGap));
		GC gc = new GC(baseImage);
		for (int i = 0; i < service.getSize(); i++) {
			Image tileImage = drawTile(service, refService, i, 1, true);
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