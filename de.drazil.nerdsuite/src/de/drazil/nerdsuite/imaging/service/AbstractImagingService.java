package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.enums.LayerAction;
import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public abstract class AbstractImagingService extends AbstractService implements IImagingService {

	@Setter
	protected ImagingWidgetConfiguration imagingWidgetConfiguration = null;
	protected IServiceCallback serviceCallback = null;
	@Setter
	protected int navigationOffset = 0;
	@Setter
	protected Object source = null;
	@Setter
	byte bitplane[];
	@Setter
	protected List<TileLocation> tileSelectionList = null;
	protected IConfirmable confirmable;
	@Setter
	protected LayerAction layerAction = LayerAction.Active;
	protected Tile selectedTile;

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

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
		TileRepositoryService service = ServiceFactory.getService(owner, TileRepositoryService.class);
		selectedTile = service.getSelectedTile();
		if (needsConfirmation() && isProcessConfirmed(true) || !needsConfirmation()) {
			each(action, selectedTile, imagingWidgetConfiguration, null);
			selectedTile.sendModificationNotification();
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

	private void convert(byte workArray[], byte bitplane[], int x, int y, ConversionMode mode) {
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
	}

	protected byte[] createWorkArray() {
		return new byte[imagingWidgetConfiguration.getTileSize() * (imagingWidgetConfiguration.pixelConfig.mul)];
	}

	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte[] bitplane, byte workArray[], int width, int height) {
		return null;
	}

	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration, TileAction tileAction) {

	}

	public boolean needsConversion() {
		return true;
	}

	private void printResult(byte workArray[]) {
		System.out.println("-----------------------------------------");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < workArray.length; i++) {
			if (i % (imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns) == 0) {
				sb.append("\n");
			}
			sb.append(workArray[i]);
		}
		System.out.println(sb);
	}

	public int computeTileOffset(int x, int y, int offset) {
		return imagingWidgetConfiguration.tileSize * (x + (y * imagingWidgetConfiguration.columns)) + offset;
	}
}
