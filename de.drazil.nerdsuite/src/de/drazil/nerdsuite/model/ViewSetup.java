package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewSetup {
	private String type;
	private String variant;
	private String widget;
	private int pixelSize;
	private int zoomFactor;
}
