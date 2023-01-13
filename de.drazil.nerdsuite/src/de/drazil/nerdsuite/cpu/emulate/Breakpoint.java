package de.drazil.nerdsuite.cpu.emulate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Breakpoint {
	@Getter
	@Setter
	private int pc;

	private boolean enabled;
	@Getter
	@Setter
	private int enabledAfterCount;
	@Getter
	@Setter
	private int count;

	public Breakpoint(int pc) {
		this(pc, true);
	}

	public Breakpoint(int pc, boolean enabled) {
		this.pc = pc;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		if (enabledAfterCount == 0 || (enabledAfterCount > 0 && count > enabledAfterCount)) {
			return enabled;
		} else {
			return false;
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void reset() {
		count = 0;
	}
}
