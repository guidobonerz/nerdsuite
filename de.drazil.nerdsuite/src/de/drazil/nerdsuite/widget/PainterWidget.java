package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.enums.CursorMode;

public class PainterWidget extends BaseImagingWidget {

	public PainterWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.Point) {
			paintTileService.setPixel(tile, cursorX, cursorY, conf);
			doDrawPixel();
			fireDoDrawTile(this);
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.Point) {
			if (oldCursorX != cursorX || oldCursorY != cursorY) {
				oldCursorX = cursorX;
				oldCursorY = cursorY;
				paintTileService.setPixel(tile, cursorX, cursorY, conf);
				doDrawPixel();
				fireDoDrawTile(this);
			}
		}
	}

	@Override
	protected void mouseEnter(int modifierMask, int x, int y) {
		doDrawTile();
	}

	@Override
	protected void mouseExit(int modifierMask, int x, int y) {
		doDrawTile();
	}
}
