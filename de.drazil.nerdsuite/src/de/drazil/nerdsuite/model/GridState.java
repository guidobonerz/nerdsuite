package de.drazil.nerdsuite.model;

import de.drazil.nerdsuite.enums.GridType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GridState {
	public boolean enabled;
	public GridType gridStyle;
}
