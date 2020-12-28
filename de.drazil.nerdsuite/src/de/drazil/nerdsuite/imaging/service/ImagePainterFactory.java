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
import de.drazil.nerdsuite.model.Image2;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	private Map<String, Image2> imagePool = null;

	public final static int NONE = 0;
	public final static int READ = 1;
	public final static int UPDATE = 2;
	public final static int PIXEL = 4;
	public final static int SCALED = 8;

	public final static int UPDATE_PIXEL = UPDATE + PIXEL;
	public final static int UPDATE_SCALED = UPDATE + SCALED;

	public static final String IMAGE_ID = "%s%sID%03X";

	private TileRepositoryService repository = null;
	private TileRepositoryService referenceRepository = null;
	private ImagingWidgetConfiguration conf;
	private int backgroundColorIndex = 0;
	private int foregroundColorIndex = 0;
	private IColorPaletteProvider colorProvider;
	private final static Map<String, ImagePainterFactory> cache = new HashMap<String, ImagePainterFactory>();

	public ImagePainterFactory(String name, IColorPaletteProvider colorProvider, ImagingWidgetConfiguration conf) {
		imagePool = new HashMap<>();
		this.repository = ServiceFactory.getService(name, TileRepositoryService.class);
		if (repository.hasReference()) {
			referenceRepository = repository.getReferenceRepository();
		}
		this.conf = conf;
		this.colorProvider = colorProvider;
		cache.put(name, this);
	}

	public final static ImagePainterFactory getImageFactory(String name) {
		return cache.get(name);
	}

	public void resetCache() {
		for (Image2 i : imagePool.values()) {
			i.getImage().dispose();
		}
		imagePool.clear();
	}

	public ImagingWidgetConfiguration getConfiguration() {
		return conf;
	}

	public void setForegroundColorIndex(int index) {
		foregroundColorIndex = index;
	}

	public int getForegroundColorIndex() {
		return foregroundColorIndex;
	}

	public void setBackgroundColorIndex(int index) {
		backgroundColorIndex = index;
	}

	public int setBackgroundColorIndex() {
		return backgroundColorIndex;
	}

	public void drawScaledImage(GC gc, Tile tile, String imageId, int maxWidth, int x, int y) {
		Image2 i2 = tile.getActiveLayer().getImage(imageId);
		Image i = i2.getImage();
		if (conf.getZoomFactor() > 1) {
			ImageData original = i.getImageData();
			ImageData scaled = original.scaledTo(original.width * conf.getZoomFactor(), original.height * conf.getZoomFactor());
			// scaled.transparentPixel =
			// original.palette.getPixel(Constants.TRANSPARENT_COLOR.getRGB());
			Image scaledImage = new Image(Display.getCurrent(), scaled);
			gc.drawImage(scaledImage, x, y);
			scaledImage.dispose();
		} else {
			gc.drawImage(i, x, y);
		}
	}

	public Image2 getGridLayer() {
		String name = conf.gridStyle.toString();
		Image2 imageInternal = imagePool.get(name);
		if (null == imageInternal) {
			imageInternal = createLayer();
			GC gc = new GC(imageInternal.getImage());
			gc.setForeground(conf.gridStyle == GridType.Line ? Constants.LINE_GRID_COLOR : Constants.PIXEL_GRID_COLOR);
			for (int x = 0; x <= conf.iconWidth * conf.tileColumns; x++) {
				for (int y = 0; y <= conf.iconHeight * conf.tileRows; y++) {
					if (conf.gridStyle == GridType.Line) {
						gc.drawLine(x * conf.pixelPaintWidth * conf.getZoomFactor(), 0, x * conf.pixelPaintWidth * conf.getZoomFactor(), conf.tileHeightPixel * conf.getZoomFactor());
						gc.drawLine(0, y * conf.pixelPaintHeight * conf.getZoomFactor(), conf.tileWidthPixel * conf.getZoomFactor(), y * conf.pixelPaintHeight * conf.getZoomFactor());
					} else {
						gc.drawPoint(x * conf.pixelPaintWidth * conf.getZoomFactor(), y * conf.pixelPaintHeight * conf.getZoomFactor());
					}
				}
			}
			gc.dispose();
			imagePool.put(name, imageInternal);
		}
		return imageInternal;
	}

	public Image2 createOrUpdateBaseImage(String name, Color color) {
		return createOrUpdateBaseImage(name, color, conf.tileWidthPixel * conf.getZoomFactor(), conf.tileHeightPixel * conf.getZoomFactor());
	}

	public Image2 createOrUpdateBaseImage(String name, Color color, int width, int height) {
		String internalName = String.format("%s_BASEIMAGE", name);
		Image2 imageInternal = imagePool.get(internalName);
		if (imageInternal == null) {
			imageInternal = new Image2(new Image(Display.getDefault(), width, height), true);
			GC gc = new GC(imageInternal.getImage());
			gc.setBackground(color);
			gc.fillRectangle(0, 0, width, height);
			gc.dispose();
			imagePool.put(internalName, imageInternal);
		}
		return imageInternal;
	}

	public Image2 createLayer() {
		return createLayer(conf.tileWidthPixel, conf.tileHeightPixel);
	}

	public Image2 createLayer(int width, int height) {
		Image2 imageInternal = new Image2(new Image(Display.getDefault(), width, height), true);
		GC gc = new GC(imageInternal.getImage());
		gc.setBackground(Constants.TRANSPARENT_COLOR);
		gc.fillRectangle(0, 0, width, height);
		gc.dispose();
		ImageData imageData = imageInternal.getImage().getImageData();
		imageInternal.getImage().dispose();
		imageData.transparentPixel = imageData.palette.getPixel(Constants.TRANSPARENT_COLOR.getRGB());
		imageInternal.setImage(new Image(Display.getDefault(), imageData));
		return imageInternal;
	}

	public Image2 createOrUpdateTilePixel(String id, Layer layer, int x, int y) {
		return createOrUpdateTilePixel(id, layer, x, y, false);
	}

	public Image2 createOrUpdateTilePixel(String id, Layer layer, int x, int y, boolean isDirty) {
		if (repository.hasReference()) {
			return _createOrUpdateTilePixelFromReference(id, layer, x, y, isDirty);
		} else {
			return _createOrUpdateTilePixel(id, layer, x, y, isDirty);
		}
	}

	private Image2 _createOrUpdateTilePixel(String id, Layer layer, int x, int y, boolean isDirty) {
		Image2 imageInternal = layer.getImage(id);
		GC gc = new GC(imageInternal.getImage());
		gc.setForeground(colorProvider.getColorByIndex(foregroundColorIndex));
		int x1 = x * (conf.isMulticolor() ? 2 : 1);
		gc.drawPoint(x1, y);
		if (conf.isMulticolor()) {
			gc.drawPoint(x1 + 1, y);
		}
		gc.dispose();
		return imageInternal;
	}

	private Image2 _createOrUpdateTilePixelFromReference(String id, Layer layer, int x, int y, boolean isDirty) {
		Image2 imageInternal = layer.getImage(id);
		GC gc = new GC(imageInternal.getImage());
		ImagePainterFactory ipf = ImagePainterFactory.getImageFactory(referenceRepository.getMetadata().getId());
		ImagingWidgetConfiguration conf = ipf.getConfiguration();
		int i = this.conf.tileWidth * y + x;
		int ci = layer.getContent()[i];
		int bi = layer.getBrush()[i];
		Tile pixelTile = referenceRepository.getTile(bi, true);
		Layer pixelLayer = pixelTile.getActiveLayer();
		String pixelId = String.format(IMAGE_ID, String.format("T%03X", bi), layer.getId(), ci);
		ipf.setForegroundColorIndex(ci);
		Image image = ipf.createOrUpdateLayer(pixelId, pixelLayer, isDirty).getImage();
		gc.drawImage(image, x * conf.tileWidthPixel, y * conf.tileHeightPixel);
		gc.dispose();
		return imageInternal;
	}

	public Image2 createOrUpdateLayer(String id, Layer layer, boolean isDirty) {
		Image2 image = null;
		if (repository.hasReference()) {
			image = _createOrUpdateLayerFromReference(id, layer, isDirty);
		} else {
			image = _createOrUpdateLayer(id, layer, isDirty);
		}
		return image;
	}

	private Image2 _createOrUpdateLayer(String id, Layer layer, boolean isDirty) {
		Image2 imageInternal = layer.getImage(id);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				layer.removeImage(id);
			}
			imageInternal = createLayer();
			imageInternal.setDirty(isDirty);
			GC gc = new GC(imageInternal.getImage());
			int x = 0;
			int y = 0;
			for (int i = 0; i < conf.getTileSize(); i++) {
				if (i % conf.tileWidth == 0 && i > 0) {
					x = 0;
					y++;
				}
				int ci = layer.getContent()[i];
				Color c = colorProvider.getColorByIndex(ci > 0 ? foregroundColorIndex : 0);
				gc.setForeground(c);
				
				gc.drawPoint(x, y);
				
				x++;
			}
			gc.dispose();
			layer.putImage(id, imageInternal);
		}
		return imageInternal;
	}

	private Image2 _createOrUpdateLayerFromReference(String id, Layer layer, boolean isDirty) {

		// String id = String.format(IMAGE_ID, tile.getId(), layer.getId(), colorIndex);
		Image2 imageInternal = layer.getImage(id);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				layer.removeImage(id);
			}
			imageInternal = createLayer();
			imageInternal.setDirty(isDirty);
			GC gc = new GC(imageInternal.getImage());
			int x = 0;
			int y = 0;
			for (int i = 0; i < conf.getTileSize(); i++) {
				if (i % conf.tileWidth == 0 && i > 0) {
					x = 0;
					y++;
				}
				int ci = layer.getContent()[i];
				int bi = layer.getBrush()[i];

				Tile pixelTile = referenceRepository.getTile(bi, true);
				Layer pixelLayer = pixelTile.getActiveLayer();
				ImagePainterFactory ipf = ImagePainterFactory.getImageFactory(referenceRepository.getMetadata().getId());
				ImagingWidgetConfiguration conf = ipf.getConfiguration();
				String pixelId = String.format(IMAGE_ID, String.format("T%03X", bi), layer.getId(), ci);
				ipf.setForegroundColorIndex(ci);
				gc.drawImage(ipf.createOrUpdateLayer(pixelId, pixelLayer, isDirty).getImage(), x * conf.tileWidthPixel, y * conf.tileHeightPixel);
				x++;
			}
			gc.dispose();
			layer.putImage(id, imageInternal);
		}
		return imageInternal;
	}

	public Image2 createOrUpdateTileMap(boolean isDirty) {
		String repositoryName = repository.getOwner();
		Image2 mapImageInternal = imagePool.get(repositoryName);
		if (mapImageInternal == null) {
			mapImageInternal = new Image2(
					createLayer(conf.tileWidthPixel * conf.columns + ((conf.columns - 1) * conf.tileGap), conf.tileHeightPixel * conf.rows + ((conf.rows - 1) * conf.tileGap)).getImage(), false);
			GC gc = new GC(mapImageInternal.getImage());
			for (int i = 0; i < repository.getSize(); i++) {
				Tile tile = repository.getTile(i);
				Layer layer = tile.getActiveLayer();
				String id = String.format(IMAGE_ID, tile.getId(), layer.getId(), foregroundColorIndex);
				Image2 imageInternal = tile.getImage(id);
				if (imageInternal == null || isDirty) {
					if (isDirty && imageInternal != null) {
						imageInternal.getImage().dispose();
						imagePool.remove(id);
					}

					imageInternal = createOrUpdateLayer(id, layer, false);
					imageInternal.setDirty(isDirty);
					tile.putImage(id, imageInternal);
				}
				int y = (i / conf.columns) * (conf.tileHeightPixel + conf.tileGap);
				int x = (i % conf.columns) * (conf.tileWidthPixel + conf.tileGap);
				gc.drawImage(imageInternal.getImage(), x, y);
			}
			gc.dispose();

			imagePool.put(repositoryName, mapImageInternal);
		}
		return mapImageInternal;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

}