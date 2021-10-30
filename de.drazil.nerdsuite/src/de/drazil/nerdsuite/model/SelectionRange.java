package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SelectionRange {
	private int from = 0;
	private int to = 0;

	public void reset() {
		from = 0;
		to = 0;
	}
}
