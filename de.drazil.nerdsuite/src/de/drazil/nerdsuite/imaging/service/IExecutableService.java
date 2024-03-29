package de.drazil.nerdsuite.imaging.service;

public interface IExecutableService extends IService {
	

	public boolean needsConfirmation();

	public void sendResponse(String message, Object data);

	public void execute();

	public void execute(IConfirmable confirmable);

	public void execute(int action);

	public void execute(int action, IConfirmable confirmable);

	public void execute(int action, IConfirmable confirmable, IServiceCallback serviceCallback);
}
