package de.drazil.nerdsuite.cpu.emulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class AbstractCPU implements ICPU {
	protected int pc = 0;
	protected int[] registers = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private Map<String, Breakpoint> breakpoints;
	private List<IBreakpointListener> breakpointListenerList;

	protected Stack<Integer> stack;

	protected AbstractCPU() {
		stack = new Stack<Integer>();
		breakpoints = new HashMap<String, Breakpoint>();
		breakpointListenerList = new ArrayList<IBreakpointListener>();
	}

	@Override
	public long getProgramCounter() {
		return pc;
	}

	@Override
	public long getStatus() {
		return registers[REG_FLAGS];
	}

	@Override
	public void setFlag(int flag, boolean set) {
		if (set) {
			registers[REG_FLAGS] = registers[REG_FLAGS] | flag;
		} else {
			registers[REG_FLAGS] = registers[REG_FLAGS] &= ~flag;
		}
	}

	@Override
	public boolean hasFlag(int flag) {
		return (registers[REG_FLAGS] & flag) == flag;
	}

	private String _getPC(int pc) {
		return Integer.toString(pc, 16);
	}

	@Override
	public void addBreakpoint(int pc) {
		addBreakpoint(pc, true);
	}

	@Override
	public void addBreakpoint(int pc, boolean enabled) {
		String _pc = _getPC(pc);
		Breakpoint bp = new Breakpoint(pc, enabled);
		breakpoints.put(_pc, bp);
		fireBreakpointAdded(bp);
	}

	@Override
	public Collection<Breakpoint> getBreakpoints() {
		return breakpoints.values();
	}

	@Override
	public void removeBreakpoint(int pc) {
		String _pc = _getPC(pc);
		Breakpoint bp = breakpoints.get(_pc);
		if (null != bp) {
			breakpoints.remove(_pc);
			fireBreakpointRemoved(bp);
		}
	}

	@Override
	public void setBreakpointEnabled(int pc, boolean enabled) {
		String _pc = _getPC(pc);
		Breakpoint bp = breakpoints.get(_pc);
		if (null != bp) {
			bp.setEnabled(enabled);
			fireBreakpointToggled(bp);
		}
	}

	@Override
	public void toggleBreakpoint(int pc) {
		String _pc = _getPC(pc);
		Breakpoint bp = breakpoints.get(_pc);
		if (null != bp) {
			bp.setEnabled(!bp.isEnabled());
			fireBreakpointToggled(bp);
		}
	}

	@Override
	public void disableAllBreakpoints() {
		breakpoints.values().stream().forEach(e -> e.setEnabled(false));
		fireBreakpointDisableAll();
	}

	@Override
	public void enableAllBreakpoints() {
		breakpoints.values().stream().forEach(e -> e.setEnabled(true));
		fireBreakpointEnableAll();
	}

	public void addBreakpointListener(IBreakpointListener listener) {
		breakpointListenerList.add(listener);
	}

	public void removeBreakpointListener(IBreakpointListener listener) {
		breakpointListenerList.remove(listener);
	}

	private void fireBreakpointToggled(Breakpoint breakpoint) {
		breakpointListenerList.stream().forEach(e -> e.breakpointToggled(breakpoint));
	}

	private void fireBreakpointDisableAll() {
		breakpointListenerList.stream().forEach(e -> e.breakpointDisableAll());
	}

	private void fireBreakpointEnableAll() {
		breakpointListenerList.stream().forEach(e -> e.breakpointEnableAll());
	}

	private void fireBreakpointAdded(Breakpoint breakpoint) {
		breakpointListenerList.stream().forEach(e -> e.breakpointAdded(breakpoint));
	}

	private void fireBreakpointRemoved(Breakpoint breakpoint) {
		breakpointListenerList.stream().forEach(e -> e.breakpointRemoved(breakpoint));
	}
}
