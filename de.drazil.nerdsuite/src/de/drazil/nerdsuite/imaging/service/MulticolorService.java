package de.drazil.nerdsuite.imaging.service;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class MulticolorService extends AbstractImagingService {

	public static final int MC_ON = 1;
	public static final int MC_OFF = 0;

	@Override
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration, TileAction tileAction) {
		int[] content = tile.getActiveLayer().getContent();
		for (int i = 0; i < content.length; i += 2) {
			if (action == MC_ON) {
				if (content[i] > 0 && content[i + 1] > 0) {
					content[i] = content[i + 1] = 3;
				} else if (content[i] > 0 && content[i + 1] == 0) {
					content[i] = content[i + 1] = 2;
				} else if (content[i] == 0 && content[i + 1] > 0) {
					content[i] = content[i + 1] = 1;
				} else {
					content[i] = content[i + 1] = 0;
				}
			} else {
				if (content[i] + content[i + 1] == 6) {
					content[i] = content[i + 1] = 1;
				} else if (content[i] + content[i + 1] == 4) {
					content[i] = 1;
					content[i + 1] = 0;
				} else if (content[i] + content[i + 1] == 2) {
					content[i] = 0;
					content[i + 1] = 1;
				} else {
					content[i] = content[i + 1] = 0;
				}
			}
		}
	}
}
