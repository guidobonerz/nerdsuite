package de.drazil.nerdsuite.model;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.cpu.decode.MemorySnippet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MemoryBlock {
	private Value startAdress;
	private Range range;
	private boolean dirty;
	private String color;
	private String label;
	private RangeType rangeType = RangeType.Unspecified;
	private List<MemorySnippet> memorySnippetList = null;

	public MemoryBlock(Value startAdress, Range range, RangeType rangeType) {
		this.startAdress = startAdress;
		this.range = range;
		this.rangeType = rangeType;
		this.dirty = false;
		resetSnippets();
	}

	public void resetSnippets() {
		this.memorySnippetList = new ArrayList<>();
		this.memorySnippetList.add(new MemorySnippet(startAdress, range));
	}
}
