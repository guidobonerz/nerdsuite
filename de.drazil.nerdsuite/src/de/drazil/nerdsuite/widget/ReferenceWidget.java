package de.drazil.nerdsuite.widget;

import java.io.File;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;

public class ReferenceWidget extends BaseImagingWidget {

	public ReferenceWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {

	}

	@Override
	public void activeLayerChanged(int layer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void colorSelected(int id, int colorIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, boolean forceUpdate) {
		// TODO Auto-generated method stub

	}

	public void loadRepository(String fileName) {
		tileRepositoryService = TileRepositoryService.load(new File(fileName), "reference");
	}

	@Override
	public void doRedraw(RedrawMode redrawMode, PencilMode pencilMode, boolean forceUpdate) {

	}

	@Override
	public void redrawCalculatedArea() {
		// TODO Auto-generated method stub

	}
}
