package de.drazil.nerdsuite.mouse;

public interface IMeasuringListener {
	public void onTriggerTimeReached(long triggerTime, int timerId, Object payload);
}
