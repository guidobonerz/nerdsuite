package de.drazil.nerdsuite.imaging.actions;

public interface IImagingAction {
	public void doProcess(byte workArray[], byte bitplane[]);

	public void sendMessage(String message, Object data);

	public boolean isProcessConfirmed();
}
