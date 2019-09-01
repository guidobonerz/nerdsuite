package de.drazil.nerdsuite.storagemedia;

import lombok.Data;

@Data
public class CBMFileAttributes implements IAttributes {
	private boolean locked;
	private boolean closed;
}
