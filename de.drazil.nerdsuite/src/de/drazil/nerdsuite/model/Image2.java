package de.drazil.nerdsuite.model;

import org.eclipse.swt.graphics.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image2 {
	private Image image;
	private boolean dirty = true;

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}
}
