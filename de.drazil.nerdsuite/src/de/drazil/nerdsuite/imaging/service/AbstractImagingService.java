package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.enums.LayerAction;
import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.model.GraphicMetadata;
import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public abstract class AbstractImagingService extends AbstractExecutableService implements IImagingService {

	@Setter
	protected ImagingWidgetConfiguration conf = null;
	protected IServiceCallback serviceCallback = null;
	@Setter
	protected int navigationOffset = 0;

	protected List<Integer> selectedTileIndexList = null;
	protected IConfirmable confirmable;
	@Setter
	protected LayerAction layerAction = LayerAction.Active;

	protected TileRepositoryService service = null;

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsConfirmation() {
		return false;
	}

	@Override
	public void sendResponse(String message, Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() {
		execute(null);
	}

	public void execute(IConfirmable confirmable) {
		execute(-1, confirmable);
	}

	@Override
	public void execute(int action) {
		execute(action, null);
	}

	@Override
	public void execute(int action, IConfirmable confirmable) {
		execute(action, confirmable, null);
	}

	@Override
	public void execute(int action, IConfirmable confirmable, IServiceCallback serviceCallback) {
		this.confirmable = confirmable;
		this.serviceCallback = serviceCallback;
		service = ServiceFactory.getService(owner, TileRepositoryService.class);
		selectedTileIndexList = service.getSelectedTileIndexList();
		if (needsConfirmation() && isProcessConfirmed(true) || !needsConfirmation()) {
			GraphicMetadata metadata = service.getMetadata();
			selectedTileIndexList.forEach(i -> {
				Tile tile = service.getTile(i);
				each(action, i, tile, service, null);
				tile.setDirty(true);
			});
			service.redrawTileViewer(selectedTileIndexList, ImagePainterFactory.UPDATE, false);
		}

		/*
		 * int width = imagingWidgetConfiguration.width *
		 * imagingWidgetConfiguration.tileColumns; int height =
		 * imagingWidgetConfiguration.height * imagingWidgetConfiguration.tileRows;
		 * callback.beforeRunService(); for (int i = 0; i < tileSelectionList.size();
		 * i++) { int x = tileSelectionList.get(i).x; int y =
		 * tileSelectionList.get(i).y; byte workArray[] = null; if (needsConversion()) {
		 * workArray = createWorkArray(); convert(workArray, bitplane, x, y,
		 * ConversionMode.toWorkArray); } int ofs =
		 * imagingWidgetConfiguration.computeTileOffset(x, y, navigationOffset);
		 * workArray = each(action, tileSelectionList.get(i),
		 * imagingWidgetConfiguration, ofs, bitplane, workArray, width, height); if
		 * (needsConversion()) { convert(workArray, bitplane, x, y,
		 * ConversionMode.toBitplane); } } callback.afterRunService();
		 */

	}

	// private void convert(byte workArray[], byte bitplane[], int x, int y,
	// ConversionMode mode) {
	/*
	 * int iconSize = imagingWidgetConfiguration.getIconSize(); int tileSize =
	 * imagingWidgetConfiguration.getTileSize(); int tileOffset =
	 * imagingWidgetConfiguration.computeTileOffset(x, y, navigationOffset); int bc
	 * = imagingWidgetConfiguration.pixelConfig.bitCount; int mask =
	 * imagingWidgetConfiguration.pixelConfig.mask; for (int si = 0, s = 0; si <
	 * tileSize; si += imagingWidgetConfiguration.bytesPerRow, s +=
	 * imagingWidgetConfiguration.bytesPerRow) { s = (si % (iconSize)) == 0 ? 0 : s;
	 * int xo = ((si / iconSize) & (imagingWidgetConfiguration.tileColumns - 1))
	 * imagingWidgetConfiguration.width; int yo = (si / (iconSize *
	 * imagingWidgetConfiguration.tileColumns)) * imagingWidgetConfiguration.height
	 * imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns;
	 * int ro = ((s / imagingWidgetConfiguration.bytesPerRow) *
	 * imagingWidgetConfiguration.width) imagingWidgetConfiguration.tileColumns; int
	 * wai = ro + xo + yo;
	 * 
	 * for (int i = 0; i < imagingWidgetConfiguration.bytesPerRow; i++) {
	 * bitplane[tileOffset + si + i] = mode == ConversionMode.toBitplane ? 0 :
	 * bitplane[tileOffset + si + i]; for (int m = 7, ti = 0; m >= 0; m -= bc, ti++)
	 * { if (mode == ConversionMode.toWorkArray) { workArray[wai + (8 * i) + ti] =
	 * (byte) ((bitplane[tileOffset + si + i] >> m) & mask); } else if (mode ==
	 * ConversionMode.toBitplane) { (bitplane[tileOffset + si + i]) |=
	 * (workArray[wai + (8 * i) + ti] << m); } } } }
	 */
	// }

	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {

	}

}
