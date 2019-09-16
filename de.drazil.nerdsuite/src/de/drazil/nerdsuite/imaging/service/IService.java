package de.drazil.nerdsuite.imaging.service;

public interface IService {

	public void setOwner(String owner);

	public boolean needsConfirmation();

	public void sendResponse(String message, Object data);

	public void execute(int action, IConfirmable confirmable);

	public void execute(IConfirmable confirmable);
}
