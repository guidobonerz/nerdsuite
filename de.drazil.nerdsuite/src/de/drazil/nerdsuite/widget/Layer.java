package de.drazil.nerdsuite.widget;

import lombok.Data;

@Data
public class Layer {

	private byte bitplane[] = null;
	private boolean isActive = false;
	private boolean visible = true;
	private String name;

	public Layer() {
		// TODO Auto-generated constructor stub
	}
}
