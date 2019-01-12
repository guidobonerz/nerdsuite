package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration2;

public interface IImagingService extends IService {

	public void setTileSelectionList(List<TileLocation> tileSelectionList);

	public void setImagingWidgetConfiguration(ImagingWidgetConfiguration2 configuration);

	public void sendResponse(String message, Object data);

	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration2 configuration);

	public boolean isProcessConfirmed(boolean confirmAnyProcess);
}
