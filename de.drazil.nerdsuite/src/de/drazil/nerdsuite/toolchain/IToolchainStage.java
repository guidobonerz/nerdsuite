package de.drazil.nerdsuite.toolchain;

public interface IToolchainStage<RESULT> {
	public void start();

	public RESULT getResult();

	public boolean isRunning();
}
