package de.drazil.nerdsuite.imaging.actions;

public interface IImagingAction {

	public boolean needsConfirmation();

	public void doProcess(byte workArray[], byte bitplane[]);

	public void sendResponse(String message, Object data);

	public boolean isProcessConfirmed(boolean confirmAnyProcess);
}
