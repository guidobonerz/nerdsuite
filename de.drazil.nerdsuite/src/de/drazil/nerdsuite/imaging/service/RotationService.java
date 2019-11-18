package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class RotationService extends AbstractImagingService {
	public final static int CW = 1;
	public final static int CCW = 2;

	@Override
	public boolean needsConfirmation() {
		return checkIfSquareBase();
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		return confirmable.isConfirmed(
				"Tile does not have a square base\nDo you really want to rotate this tile?\n\nTo prevent data loss click No");
	}

	private boolean checkIfSquareBase() {
		Rectangle r = selectedTile.getSelection();
		return r.width != r.height;
	}

	@Override
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration, TileAction tileAction) {
		Rectangle r = tile.getSelection();
		int[] content = tile.getActiveLayer().getContent();
		int[] contentSelection = new int[r.width * r.height];
		int[] targetContentSelection = new int[r.width * r.height];

		for (int x = r.x, cx = 0; x < r.x + r.width; x++, cx++) {
			for (int y = r.y, cy = 0; y < r.y + r.height; y++, cy++) {
				contentSelection[cx + cy * r.width] = content[x + y * configuration.tileWidth];
			}
		}

		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				int b = contentSelection[x + (y * r.width)];
				int o = 0;
				if (action == CCW) {
					o = (targetContentSelection.length) - (r.width) - (r.width * x) + y;
				} else if (action == CW) {
					o = (r.width) - y - 1 + (x * r.width);
				}
				if (o >= 0 && o < (targetContentSelection.length)) {
					targetContentSelection[o] = b;
				}
			}
		}

		for (int x = r.x, cx = 0; x < r.x + r.width; x++, cx++) {
			for (int y = r.y, cy = 0; y < r.y + r.height; y++, cy++) {
				content[x + y * configuration.tileWidth] = targetContentSelection[cx + cy * r.width];
			}
		}
	}
}
