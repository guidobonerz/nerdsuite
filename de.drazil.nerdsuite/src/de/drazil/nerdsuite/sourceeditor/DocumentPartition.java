package de.drazil.nerdsuite.sourceeditor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPartition {
	private int offset;
	private int len;
}
