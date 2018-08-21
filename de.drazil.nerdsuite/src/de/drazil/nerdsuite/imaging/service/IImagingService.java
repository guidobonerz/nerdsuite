package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.IImagingCallback;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public interface IImagingService {

	public boolean needsConfirmation();

	public boolean needsConversion();

	public void runService(int action, List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration,
			IImagingCallback callback, int offset, byte bitplane[]);

	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte bitplane[], byte workArray[], int width, int height);

	public void sendResponse(String message, Object data);

	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration);

	public boolean isProcessConfirmed(boolean confirmAnyProcess);
}
