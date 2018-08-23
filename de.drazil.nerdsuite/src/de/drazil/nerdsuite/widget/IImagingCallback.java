package de.drazil.nerdsuite.widget;

public interface IImagingCallback {
	public void beforeRunService();

	public void onRunService(int offset);

	public void afterRunService();
}
