package de.drazil.nerdsuite.viewer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RowDescriber
{
	private int start;
	private int offset;
	private int length;
	private byte binaryData[];
	private boolean odd;
}
