package de.drazil.nerdsuite.imaging.service;

public interface IServiceCallback {
	public void beforeRunService();

	public void onRunService(int offset, int x, int y, boolean updateCursorLocation);

	public void afterRunService();
}
