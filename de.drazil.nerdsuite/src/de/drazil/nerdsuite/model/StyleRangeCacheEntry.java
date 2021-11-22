package de.drazil.nerdsuite.model;

import java.util.List;

import org.eclipse.swt.custom.StyleRange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StyleRangeCacheEntry {
	private int lineIndex;
	private int lineOffset;
	private boolean isDirty;
	private List<StyleRange> styleRangeList;
}
