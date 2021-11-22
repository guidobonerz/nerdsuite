package de.drazil.nerdsuite.toolchain;

public interface IToolchainItem<RESULT> {
	public void start();

	public void stop();

	public RESULT getResult();

	public boolean isRunning();
}
