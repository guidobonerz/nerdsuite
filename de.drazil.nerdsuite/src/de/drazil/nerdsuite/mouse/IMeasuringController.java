package de.drazil.nerdsuite.mouse;

public interface IMeasuringController {
	public void start();

	public void stop();

	public void setTriggerMillis(long... millis);
}
