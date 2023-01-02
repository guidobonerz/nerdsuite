package de.drazil.nerdsuite.cpu.emulate;

import java.util.Collection;

public interface ICPU {
	public static final int REG_FLAGS = 0;

	public long getProgramCounter();

	public long getStatus();

	public void setFlag(int flag, boolean set);

	public boolean hasFlag(int flag);

	public String getPcAsString(int pc);

	public Breakpoint getBreakpoint(int pc);

	public int execute(int pc, int[] ram, int[] rom);

	public void addBreakpoint(int pc);

	public void addBreakpoint(int pc, boolean enabled);

	public void removeBreakpoint(int pc);

	public void toggleBreakpoint(int pc);

	public void setBreakpointEnabled(int pc, boolean enabled);

	public void disableAllBreakpoints();

	public void enableAllBreakpoints();

	public Collection<Breakpoint> getBreakpoints();

	public void addBreakpointListener(IBreakpointListener listener);

	public void removeBreakpointListener(IBreakpointListener listener);

}
