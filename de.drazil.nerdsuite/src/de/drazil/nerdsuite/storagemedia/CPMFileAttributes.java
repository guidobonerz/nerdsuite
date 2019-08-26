package de.drazil.nerdsuite.storagemedia;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CPMFileAttributes implements IAttributes {
	private boolean locked;
	private boolean hidden;
	private int userLevel;

}
