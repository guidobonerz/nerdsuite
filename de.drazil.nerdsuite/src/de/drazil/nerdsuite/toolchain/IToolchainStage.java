package de.drazil.nerdsuite.toolchain;

public interface IToolchainStage<RESULT> {
	public void start();

	public void stop();

	public RESULT getResult();

	public boolean isRunning();
}
