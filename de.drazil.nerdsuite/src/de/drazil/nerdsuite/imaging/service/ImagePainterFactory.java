package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.ScaleMode;
import de.drazil.nerdsuite.model.ProjectMetaData;
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

	public Image getImageByName(String name) {
		return imagePool.get(name);
	}

	public Image getImage(TileRepositoryService service, int tileIndex, int x, int y, int action,
			ImagingWidgetConfiguration conf, IColorPaletteProvider colorPaletteProvider, ProjectMetaData metadata) {
		Tile tile = service.getTile(tileIndex);
		String name = tile.getName();
		Image scaledImage = null;
		Image mainImage = imagePool.get(name);
		if (null == mainImage) {
			/*
			 * if (mainImage != null && checkMode(action, UPDATE)) { mainImage.dispose(); }
			 */
			mainImage = new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel);
			// mainImage = new Image(Display.getDefault(), metadata.getTileWidthPixel(),
			// metadata.getTileHeightPixel());
			mainImage.setBackground(Constants.BLACK);
			imagePool.put(name, mainImage);
		}
		if ((action & UPDATE) == UPDATE || (action & PIXEL) == PIXEL) {
			mainImage = updateImage(service, tileIndex, x, y, action, conf, mainImage, name, colorPaletteProvider);
		}

		ScaleMode scaleMode = conf.getScaleMode();

		if (conf.getScaleMode() != ScaleMode.None) {
			String sm = name + "_" + conf.getScaleMode().name();
			scaledImage = imagePool.get(sm);
			if (null == scaledImage || checkMode(action, UPDATE)) {
				if (scaledImage != null && checkMode(action, UPDATE)) {
					scaledImage.dispose();
				}
				System.out.println("new scaled image");
				int scaledWidth = scaleMode.getDirection() ? conf.fullWidthPixel << scaleMode.getScaleFactor()
						: conf.fullWidthPixel >> scaleMode.getScaleFactor();
				int scaledHeight = scaleMode.getDirection() ? conf.fullHeightPixel << scaleMode.getScaleFactor()
						: conf.fullHeightPixel >> scaleMode.getScaleFactor();
				scaledImage = new Image(Display.getDefault(),
						mainImage.getImageData().scaledTo(scaledWidth, scaledHeight));
				imagePool.put(sm, scaledImage);

				conf.setScaledTileWidth(scaledImage.getBounds().width);
				conf.setScaledTileHeight(scaledImage.getBounds().height);
			}
			mainImage = scaledImage;
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

	private Image updateImage(TileRepositoryService service, int tileIndex, int px, int py, int update,
			ImagingWidgetConfiguration conf, Image image, String imageName,
			IColorPaletteProvider colorPaletteProvider) {
		GC gc = new GC(image);
		gc.setAlpha(255);
		int width = conf.tileWidth;
		int size = service.getTileSize();
		int x = 0;
		int y = 0;
		List<Layer> layerList = service.getTile(tileIndex).getLayerList();
		if (checkMode(update, PIXEL)) {
			int offset = py * width + px;
			if (offset < size) {
				draw(gc, offset, layerList, conf, px, py, colorPaletteProvider);
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (i % width == 0 && i > 0) {
					x = 0;
					y++;
				}
				draw(gc, i, layerList, conf, x, y, colorPaletteProvider);
				x++;
			}
		}
		gc.dispose();
		return image;
	}

	private void draw(GC gc, int offset, List<Layer> layerList, ImagingWidgetConfiguration conf, int x, int y,
			IColorPaletteProvider colorPaletteProvider) {
		for (Layer l : layerList) {
			int[] content = l.getContent();
			// if (content[offset] != 0 && (!tile.isShowOnlyActiveLayer() ||
			// (tile.isShowOnlyActiveLayer() && l.isActive())
			// || tile.isShowInactiveLayerTranslucent())) {
			// gc.setAlpha(tile.isShowInactiveLayerTranslucent() && !l.isActive() ? 50 :
			// 255);
			// }

			if (referenceRepository == null) {
				gc.setBackground(colorPaletteProvider.getColorByIndex(content[offset]));
				gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);

			} else {
				gc.setBackground(colorPaletteProvider.getColorByIndex(1));
				Image img = referenceRepository.getSelectedImage();
				gc.drawImage(img, x * conf.pixelSize, y * conf.pixelSize);
			}

		}
	}
}
