package de.drazil.nerdsuite.cpu.emulate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Breakpoint {

	private int pc;

	@Getter
	@Setter
	private boolean enabled;

	@Getter
	@Setter
	private int cycleCount;

	private int count;

	public Breakpoint(int pc) {
		this(pc, true);
	}

	public Breakpoint(int pc, boolean enabled) {
		this.pc = pc;
		this.enabled = enabled;
	}

	public Breakpoint(int pc, boolean enabled, int cycleCount) {
		this.pc = pc;
		this.enabled = enabled;
		this.cycleCount = cycleCount;
	}

	public boolean checkBreakpoint(int pc) {
		return (this.pc == pc && enabled && (count > cycleCount || cycleCount == 0));
	}

	public void reset() {
		count = 0;
	}
}
