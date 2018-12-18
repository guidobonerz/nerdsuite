package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public interface IImagingService extends IService {

	public void setTileLocationList(List<TileLocation> tileLocationList);

	public void setImagingWidgetConfiguration(ImagingWidgetConfiguration configuration);

	public void setValue(int action, Object data);

	public void runService(int action, List<TileLocation> tileLocationList, byte bitplane[]);

	public void sendResponse(String message, Object data);

	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration);

	public boolean isProcessConfirmed(boolean confirmAnyProcess);
}
