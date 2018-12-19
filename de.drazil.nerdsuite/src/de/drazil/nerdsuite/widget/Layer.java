package de.drazil.nerdsuite.widget;

import lombok.Data;

@Data
public class Layer {
	private byte bitplane[] = null;
	private boolean isActive = false;
	private boolean visible = true;
	private String name = "rename me";
}
