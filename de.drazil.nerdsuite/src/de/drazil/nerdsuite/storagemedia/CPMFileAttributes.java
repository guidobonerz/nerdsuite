package de.drazil.nerdsuite.storagemedia;

import lombok.Data;

@Data
public class CPMFileAttributes implements IAttributes {
	private boolean locked;
	private boolean hidden;
	private int userLevel;

}
