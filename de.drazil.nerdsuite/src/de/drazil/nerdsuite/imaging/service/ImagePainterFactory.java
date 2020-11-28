package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.model.Image2;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.PlatformFactory;
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
						gc.drawLine(x * conf.pixelPaintWidth, 0, x * conf.pixelPaintWidth, conf.tileHeightPixel);
						gc.drawLine(0, y * conf.pixelPaintHeight, conf.tileWidthPixel, y * conf.pixelPaintHeight);
					} else {
						gc.drawPoint(x * conf.pixelPaintWidth, y * conf.pixelPaintHeight);
					}
				}
			}
			gc.dispose();
			imagePool.put(name, imageInternal);
		}
		return imageInternal;
	}

	public Image2 createOrUpdateBaseImage(String name, Color color) {
		return createOrUpdateBaseImage(name, color, conf.tileWidthPixel, conf.tileHeightPixel);
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

	public Image2 createOrUpdateTilePixel(Tile tile, int colorIndex, int x, int y) {
		return createOrUpdateTilePixel(tile, colorIndex, x, y, tile.isDirty());
	}

	public Image2 createOrUpdateTilePixel(Tile tile, int colorIndex, int x, int y, boolean isDirty) {
		if (repository.hasReference()) {
			return _createOrUpdateTilePixelFromReference(tile, colorIndex, x, y, isDirty);
		} else {
			return _createOrUpdateTilePixel(tile, colorIndex, x, y, isDirty);
		}
	}

	private Image2 _createOrUpdateTilePixel(Tile tile, int colorIndex, int x, int y, boolean isDirty) {
		Layer layer = tile.getActiveLayer();
		String name = String.format(IMAGE_ID, tile.getId(), layer.getId(), colorIndex);
		Image2 imageInternal = tile.getImage(name);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				tile.removeImage(name);
			}
			imageInternal = createLayer();
			tile.putImage(name, imageInternal);
		}
		imageInternal.setDirty(isDirty);
		if (colorIndex != 0) {
			GC gc = new GC(imageInternal.getImage());
			gc.setBackground(colorProvider.getColorByIndex(colorIndex));
			gc.fillRectangle(x * conf.pixelPaintWidth, y * conf.pixelPaintHeight, conf.pixelPaintWidth, conf.pixelPaintHeight);
			gc.dispose();
		}
		return imageInternal;
	}

	private Image2 _createOrUpdateTilePixelFromReference(Tile tile, int colorIndex, int x, int y, boolean isDirty) {
		Layer layer = tile.getActiveLayer();
		String id = String.format(IMAGE_ID, tile.getId(), layer.getId(), colorIndex);
		Image2 imageInternal = tile.getImage(id);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				tile.removeImage(id);
			}
			imageInternal = createLayer();
			tile.putImage(id, imageInternal);
		}

		imageInternal.setDirty(isDirty);
		GC gc = new GC(imageInternal.getImage());

		ImagePainterFactory ipf = ImagePainterFactory.getImageFactory(referenceRepository.getMetadata().getId());
		ImagingWidgetConfiguration conf = ipf.getConfiguration();
		int i = this.conf.tileWidth * y + x;
		int ci = layer.getContent()[i];
		int bi = layer.getBrush()[i];
		Image image = ipf.createOrUpdateTile(referenceRepository.getTile(bi, true), ci, isDirty).getImage();
		Rectangle dimesion = image.getBounds();
		gc.setBackground(Constants.TRANSPARENT_COLOR);
		gc.fillRectangle(x * conf.tileWidthPixel, y * conf.tileHeightPixel, dimesion.width, dimesion.height);
		gc.drawImage(image, x * conf.tileWidthPixel, y * conf.tileHeightPixel);
		gc.dispose();

		return imageInternal;
	}

	public Image2 createOrUpdateTile(Tile tile, int colorIndex) {
		return createOrUpdateTile(tile, colorIndex, tile.isDirty());
	}

	public Image2 createOrUpdateTile(Tile tile, int colorIndex, boolean isDirty) {
		Image2 image = null;
		if (repository.hasReference()) {
			image = _createOrUpdateTileFromReference(tile, colorIndex, isDirty);
		} else {
			image = _createOrUpdateTile(tile, colorIndex, isDirty);
		}
		return image;
	}

	private Image2 _createOrUpdateTile(Tile tile, int colorIndex, boolean isDirty) {
		Color color = PlatformFactory.getPlatformColors(repository.getMetadata().getPlatform()).get(colorIndex).getColor();
		Layer layer = tile.getActiveLayer();
		String id = String.format(IMAGE_ID, tile.getId(), layer.getId(), colorIndex);
		Image2 imageInternal = tile.getImage(id);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				tile.removeImage(id);
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
				Color c = null;
				if (colorIndex != -1) {
					c = ci > 0 ? color : Constants.TRANSPARENT_COLOR;
				} else {
					if (ci == 0) {
						c = colorProvider.getColorByIndex(ci);
					} else {
						c = Constants.TRANSPARENT_COLOR;
					}
				}

				gc.setBackground(c);
				gc.fillRectangle(x * conf.pixelPaintWidth, y * conf.pixelPaintHeight, conf.pixelPaintWidth, conf.pixelPaintHeight);

				x++;
			}
			gc.dispose();
			tile.putImage(id, imageInternal);
		}
		return imageInternal;
	}

	private Image2 _createOrUpdateTileFromReference(Tile tile, int colorIndex, boolean isDirty) {
		Layer layer = tile.getActiveLayer();
		String id = String.format(IMAGE_ID, tile.getId(), layer.getId(), colorIndex);
		Image2 imageInternal = tile.getImage(id);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				tile.removeImage(id);
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

				ImagePainterFactory ipf = ImagePainterFactory.getImageFactory(referenceRepository.getMetadata().getId());
				ImagingWidgetConfiguration conf = ipf.getConfiguration();
				gc.drawImage(ipf.createOrUpdateTile(referenceRepository.getTile(bi, true), ci, isDirty).getImage(), x * conf.tileWidthPixel, y * conf.tileHeightPixel);
				x++;
			}
			gc.dispose();
			tile.putImage(id, imageInternal);
		}
		return imageInternal;
	}

	public Image2 createOrUpdateTileMap(int colorIndex, boolean isDirty) {
		String repositoryName = repository.getOwner();
		Image2 mapImageInternal = imagePool.get(repositoryName);
		if (mapImageInternal == null) {
			mapImageInternal = new Image2(
					createLayer(conf.tileWidthPixel * conf.columns + ((conf.columns - 1) * conf.tileGap), conf.tileHeightPixel * conf.rows + ((conf.rows - 1) * conf.tileGap)).getImage(), false);
			GC gc = new GC(mapImageInternal.getImage());
			for (int i = 0; i < repository.getSize(); i++) {
				Tile tile = repository.getTile(i);
				Layer layer = tile.getActiveLayer();
				String name = String.format(IMAGE_ID, tile.getId(), layer.getId(), colorIndex);
				Image2 imageInternal = tile.getImage(name);
				if (imageInternal == null || isDirty) {
					if (isDirty && imageInternal != null) {
						imageInternal.getImage().dispose();
						imagePool.remove(name);
					}
					imageInternal = createOrUpdateTile(tile, colorIndex, false);
					imageInternal.setDirty(isDirty);
					tile.putImage(name, imageInternal);
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