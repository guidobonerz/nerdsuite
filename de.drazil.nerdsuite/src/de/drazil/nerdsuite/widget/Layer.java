package de.drazil.nerdsuite.widget;

import lombok.Data;

@Data
public class Layer {
	private int content[] = null;
	private boolean isActive = false;
	private boolean visible = true;
	private String name = "<rename me>";

	public Layer(String name) {
		this.name = null == name ? this.name : name;
	}
}
