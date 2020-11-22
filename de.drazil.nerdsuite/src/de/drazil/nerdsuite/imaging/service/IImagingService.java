package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public interface IImagingService extends IExecutableService {

	public void setConf(ImagingWidgetConfiguration configuration);

	public void sendResponse(String message, Object data);

	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration);

	public boolean isProcessConfirmed(boolean confirmAnyProcess);
}
