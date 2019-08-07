package de.drazil.nerdsuite.imaging.service;

public interface IService {

	public boolean needsConfirmation();

	public void sendResponse(String message, Object data);

	public void execute(int action);

	public void execute();
}
