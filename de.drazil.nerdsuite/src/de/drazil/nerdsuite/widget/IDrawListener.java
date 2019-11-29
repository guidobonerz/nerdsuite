package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;

public interface IDrawListener {

	//public void doDrawPixel(BaseImagingWidget source, int x, int y, PencilMode pencilMode);

	//public void doDrawTile(boolean forceUpdate);

	//public void doDrawAllTiles();

	//public void doDrawSelectedTiles();

	public void doRedraw(RedrawMode redrawMode, PencilMode pencilMode,boolean forceUpdate);

}
