package de.drazil.nerdsuite.widget;

public interface IImagingCallback {
	public void beforeRunService();

	public void onRunService(int offset, int x, int y, boolean updateCursorLocation);

	public void afterRunService();
}
