package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.enums.RedrawMode;

public interface IDrawListener {

	public void doRedraw(RedrawMode redrawMode, int action);

}
