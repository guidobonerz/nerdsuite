package de.drazil.nerdsuite.cpu.emulate;

import java.util.Collection;

import de.drazil.nerdsuite.enums.ValueType;

public interface ICPU {
	public static final int REG_FLAGS = 0;

	public long getProgramCounter();

	public long getStatus();

	public void setFlag(int flag, boolean set);

	public boolean hasFlag(int flag);

	public Breakpoint getBreakpoint(int address);

	public int execute(int pc, boolean debug);

	public void setExecutionState(ExecutionState executionState);

	public ExecutionState getExecutionState();

	public void addBreakpoint(int address);

	public void addBreakpoint(int address, boolean enabled);

	public void addBreakpoint(int address, boolean enabled, int cycles);

	public void removeBreakpoint(int address);

	public void toggleBreakpoint(int address);

	public void setBreakpointEnabled(int address, boolean enabled);

	public void disableAllBreakpoints();

	public void enableAllBreakpoints();

	public Collection<Breakpoint> getBreakpoints();

	public void addBreakpointListener(IBreakpointListener listener);

	public void removeBreakpointListener(IBreakpointListener listener);

	public void addWatchpoint(int address);

	public void addWatchpoint(int address, ValueType valueType);

	public void addWatchpoint(String label, ValueType valueType);

	public void removeWatchpoint(int address);

	public void removeWatchpoint(String label);

	public void addLabel(String label, int address);

	public void removeLabel(String label);

}
