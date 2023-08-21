package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class DisassemblingRange {
	private int offset = 0;
	private int len = 0;
	private boolean dirty;
	private String color;
	private String label;
	private RangeType rangeType = RangeType.Unspecified;

	public DisassemblingRange(int offset, int length, RangeType rangeType) {
		this.offset = offset;
		this.len = length;
		this.rangeType = rangeType;
		this.dirty = false;
	}
}
