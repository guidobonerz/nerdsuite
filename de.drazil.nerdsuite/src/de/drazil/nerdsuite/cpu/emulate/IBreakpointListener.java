package de.drazil.nerdsuite.cpu.emulate;

public interface IBreakpointListener {

	public void breakpointAdded(Breakpoint breakpoint);

	public void breakpointRemoved(Breakpoint breakpoint);

	public void breakpointToggled(Breakpoint breakpoint);

	public void breakpointDisableAll();

	public void breakpointEnableAll();

	public void breakReached();

}
