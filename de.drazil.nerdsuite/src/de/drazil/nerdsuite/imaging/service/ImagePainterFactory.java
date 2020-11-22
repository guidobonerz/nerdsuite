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
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	public static interface IPainter {
		public void paint(GC gc);
	}

	private Map<String, Image2> imagePool = null;

	public final static int NONE = 0;
	public final static int READ = 1;
	public final static int UPDATE = 2;
	public final static int PIXEL = 4;
	public final static int SCALED = 8;

	public final static int UPDATE_PIXEL = UPDATE + PIXEL;
	public final static int UPDATE_SCALED = UPDATE + SCALED;

	private TileRepositoryService repository = null;
	private ImagingWidgetConfiguration conf;
	private IColorPaletteProvider colorProvider;
	private final static Map<String, ImagePainterFactory> cache = new HashMap<String, ImagePainterFactory>();

	public ImagePainterFactory(String name, IColorPaletteProvider colorProvider, ImagingWidgetConfiguration conf) {
		imagePool = new HashMap<>();
		this.repository = ServiceFactory.getService(name, TileRepositoryService.class);
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

	public Image2 getImage(String name) {
		return imagePool.get(name);
	}

	public Image2 getGridLayer() {
		String name = conf.gridStyle.toString();
		Image2 imageInternal = imagePool.get(name);
		if (null == imageInternal) {
			imageInternal = createLayer();
			GC gc = new GC(imageInternal.getImage());
			gc.setForeground(conf.gridStyle == GridType.Line ? Constants.LINE_GRID_COLOR : Constants.PIXEL_GRID_COLOR);
			for (int x = 0; x <= conf.width * conf.tileColumns; x++) {
				for (int y = 0; y <= conf.height * conf.tileRows; y++) {
					if (conf.gridStyle == GridType.Line) {
						gc.drawLine(x * conf.pixelPaintWidth, 0, x * conf.pixelPaintWidth, conf.tileHeightPixel);
						gc.drawLine(0, y * conf.pixelHeight, conf.tileWidthPixel, y * conf.pixelHeight);
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

	public Image2 createOrUpdateMergedImage(String name, Color color, int width, int height) {
		String internalName = String.format("%s_MERGED", name);
		Image2 imageInternal = imagePool.get(internalName);
		if (imageInternal == null) {
			imageInternal = new Image2(new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel), false);
			GC gc = new GC(imageInternal.getImage());
			gc.setBackground(color);
			gc.fillRectangle(0, 0, conf.tileWidthPixel, conf.tileHeightPixel);
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
		gc.dispose();
		return imageInternal;
	}

	public Image2 createOrUpdateTileFull(Tile tile, int colorIndex) {
		return createOrUpdateTileFull(tile, colorIndex, tile.isDirty());
	}

	public Image2 createOrUpdateTileFull(Tile tile, int colorIndex, boolean isDirty) {
		Color color = PlatformFactory.getPlatformColors(repository.getMetadata().getPlatform()).get(colorIndex).getColor();
		Layer layer = tile.getActiveLayer();
		String name = String.format("%s_%s_C%d", tile.getName(), layer.getName(), colorIndex);
		Image2 imageInternal = imagePool.get(name);
		if (imageInternal == null || isDirty) {
			if (isDirty && imageInternal != null) {
				imageInternal.getImage().dispose();
				imagePool.remove(name);
			}
			imageInternal = createLayer();
			System.out.println("paint tile cursor");
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
					c = ci > 0 ? color : colorProvider.getColorByIndex(0);
				} else {
					colorProvider.getColorByIndex(ci);
				}
				gc.setBackground(c);
				gc.fillRectangle(x * conf.pixelPaintWidth, y * conf.pixelPaintHeight, conf.pixelPaintWidth, conf.pixelPaintHeight);
				x++;
			}
			gc.dispose();
			imagePool.put(name, imageInternal);
		}
		return imageInternal;
	}

	public Image2 createOrUpdateTileMap(int colorIndex, boolean isDirty) {
		String repositoryName = repository.getOwner() + "_REPOSITORY";
		Image2 mapImageInternal = imagePool.get(repositoryName);
		if (mapImageInternal == null) {
			mapImageInternal = new Image2(
					createLayer(conf.tileWidthPixel * conf.columns + ((conf.columns - 1) * conf.tileGap), conf.tileHeightPixel * conf.rows + ((conf.rows - 1) * conf.tileGap)).getImage(), false);
			GC gc = new GC(mapImageInternal.getImage());
			for (int i = 0; i < repository.getSize(); i++) {
				Tile tile = repository.getTile(i);
				Layer layer = tile.getActiveLayer();
				String name = String.format("%s_%s_C%d", tile.getName(), layer.getName(), colorIndex);
				Image2 imageInternal = imagePool.get(name);
				if (imageInternal == null || isDirty) {
					if (isDirty && imageInternal != null) {
						imageInternal.getImage().dispose();
						imagePool.remove(name);
					}
					imageInternal = createOrUpdateTileFull(tile, colorIndex, false);
					System.out.println("paint tile cursor");
					imageInternal.setDirty(isDirty);
					int y = (i / conf.columns) * (conf.tileHeightPixel + conf.tileGap);
					int x = (i % conf.columns) * (conf.tileWidthPixel + conf.tileGap);
					gc.drawImage(imageInternal.getImage(), x, y);
					imagePool.put(name, imageInternal);
				}
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