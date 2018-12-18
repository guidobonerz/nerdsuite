package de.drazil.nerdsuite.imaging.service;

public interface IService {

	public void setAction(int action);

	public boolean needsConfirmation();

	public void sendResponse(String message, Object data);

	public void start();
}
