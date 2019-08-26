package de.drazil.nerdsuite.storagemedia;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CBMFileAttributes implements IAttributes {
	private boolean locked;
	private boolean closed;
}
