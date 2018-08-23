package de.drazil.nerdsuite.widget;

public interface IImagingCallback {
	public void beforeRunService();

	public void onRunService(int x, int y, int offset);

	public void afterRunService();
}
