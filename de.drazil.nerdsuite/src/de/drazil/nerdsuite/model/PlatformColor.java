package de.drazil.nerdsuite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.util.SwtUtil;

@NoArgsConstructor
public class PlatformColor
{
	private Color color;

	@Getter
	@Setter
	private String value;
	@Getter
	@Setter
	private String name;

	public Color getColor()
	{
		if (color == null)
		{
			color = new Color(Display.getCurrent(), SwtUtil.toRGB(value));
		}
		return color;
	}

}
