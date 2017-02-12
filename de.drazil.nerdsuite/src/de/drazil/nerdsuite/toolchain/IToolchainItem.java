package de.drazil.nerdsuite.toolchain;

public interface IToolchainItem<RESULT>
{
	public void execute();

	public RESULT getResult();

	public boolean isRunning();
}
