package de.drazil.nerdsuite.cpu.emulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimerTask;

import de.drazil.nerdsuite.enums.ValueType;
import de.drazil.nerdsuite.util.NumericConverter;


public abstract class AbstractCPU implements ICPU {
	protected int pc = 0;
	protected int[] registers = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private Map<String, Breakpoint> breakpoints;
	private Map<String, Watchpoint> watchpoints;
	private Map<String, Integer> labels;
	private List<IBreakpointListener> breakpointListenerList;
	private IPlatform platform;
	private boolean debug = false;
	private boolean terminate = false;
	protected Stack<Integer> stack;
	private ExecutionState executionState = ExecutionState.RUN;

	protected class Task extends TimerTask {
		   private String name;
		   public Task(String name) {
		       this.name = name;
		   }
		   public void run() {
		      System.out.println("[" + new Date() + "] " + name + ": task executed!");
		   }
		}
	
	protected AbstractCPU(IPlatform platform) {
		this.platform = platform;
		((AbstractPlatform) this.platform).cpu = this;
		stack = new Stack<Integer>();
		breakpoints = new HashMap<String, Breakpoint>();
		breakpointListenerList = new ArrayList<IBreakpointListener>();
		watchpoints = new HashMap<String, Watchpoint>();
		labels = new HashMap<String, Integer>();
	}

	public IPlatform getPlatform() {
		return platform;
	}

	@Override
	public void setExecutionState(ExecutionState executionState) {
		this.executionState = executionState;
	}

	@Override
	public ExecutionState getExecutionState() {
		return this.executionState;
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

	public Breakpoint getBreakpoint(int pc) {
		return breakpoints.get(NumericConverter.toHexString(pc, 8));
	}

	@Override
	public void addBreakpoint(int address) {
		addBreakpoint(address, true);
	}

	@Override
	public void addBreakpoint(int address, boolean enabled) {
		addBreakpoint(address, enabled, 0);
	}

	public void addBreakpoint(int address, boolean enabled, int cycleCount) {
		String _address = NumericConverter.toHexString(address, 8);
		Breakpoint bp = new Breakpoint(pc, enabled, cycleCount);
		breakpoints.put(_address, bp);
		fireBreakpointAdded(bp);
	}

	@Override
	public Collection<Breakpoint> getBreakpoints() {
		return breakpoints.values();
	}

	@Override
	public void removeBreakpoint(int address) {
		String _address = NumericConverter.toHexString(address, 8);
		Breakpoint bp = breakpoints.get(_address);
		if (null != bp) {
			breakpoints.remove(_address);
			fireBreakpointRemoved(bp);
		}
	}

	@Override
	public void setBreakpointEnabled(int address, boolean enabled) {
		String _address = NumericConverter.toHexString(address, 8);
		Breakpoint bp = breakpoints.get(_address);
		if (null != bp) {
			bp.setEnabled(enabled);
			fireBreakpointToggled(bp);
		}
	}

	@Override
	public void toggleBreakpoint(int address) {
		String _address = NumericConverter.toHexString(address, 8);
		Breakpoint bp = breakpoints.get(_address);
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

	@Override
	public void addLabel(String label, int address) {
		labels.put(label, address);
	}

	@Override
	public void removeLabel(String label) {
		labels.remove(label);
	}

	@Override
	public void addWatchpoint(int address) {
		String _address = NumericConverter.toHexString(address, 2);
		watchpoints.put(_address, new Watchpoint(address, ValueType.BYTE));

	}

	public void addWatchpoint(int address, ValueType valueType) {
		String _address = NumericConverter.toHexString(address, valueType.getSize() * 2);
		watchpoints.put(_address, new Watchpoint(address, valueType));
	}

	@Override
	public void addWatchpoint(String label, ValueType valueType) {
		int address = labels.get(label);
		watchpoints.put(label, new Watchpoint(address, valueType));
	}

	@Override
	public void removeWatchpoint(int address) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeWatchpoint(String label) {
		// TODO Auto-generated method stub
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
