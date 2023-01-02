package de.drazil.nerdsuite.cpu.emulate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Breakpoint {
	private int pc;
	private boolean enabled;
}
