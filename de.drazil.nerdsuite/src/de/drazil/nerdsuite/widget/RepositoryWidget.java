package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.model.SelectionRange;
import de.drazil.nerdsuite.mouse.AbstractMeasuringController;
import de.drazil.nerdsuite.mouse.IMeasuringListener;

public class RepositoryWidget extends BaseImagingWidget implements IMeasuringListener {

	private AbstractMeasuringController mc;
	private boolean tileSelectionStarted = false;
	private SelectionRange tileSelectionRange = null;
	private List<Integer> selectedTileIndexList = null;

	public RepositoryWidget(Composite parent, int style) {
		super(parent, style);
		mc = new AbstractMeasuringController();
		mc.setTriggerMillis(1500);
		mc.addMeasuringListener(this);
		tileSelectionRange = new SelectionRange();
		selectedTileIndexList = new ArrayList<>();

	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		if (supportsSingleSelection() || supportsMultiSelection()) {
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = computeTileIndex(tileX, tileY);
			// computeTileSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			computeTileSelection(tileX, tileY, 1);
			if (selectedTileIndex < tileRepositoryService.getSize()) {
				tileRepositoryService.setSelectedTileIndex(selectedTileIndex);
			} else {
				System.out.println("tile selection outside range...");
			}
			// fireSetSelectedTile(ImagingWidget.this, tile);

			doDrawAllTiles();
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {
		if (supportsMultiSelection()) {
			// computeTileSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			computeTileSelection(tileX, tileY, 1);
			doDrawAllTiles();
		}
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
		if (supportsMultiSelection() && selectedTileIndexList.size() > 1) {
			tileSelectionStarted = false;
			tileSelectionRange.reset();
			tileRepositoryService.setSelectedTileIndexList(selectedTileIndexList);
		}
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		if (supportsSingleSelection()) {
			resetSelectionList();
		}
		if (supportsMultiSelection() || supportsSingleSelection()) {
			computeTileSelection(tileX, tileY, 0);
			// System.out.printf("tile x:%2d tile y:%2d\n", tileX, tileY);
		}
	}

	@Override
	protected void mouseMove(int modifierMask, int x, int y) {
		if (supportsSingleSelection() || supportsMultiSelection()) {
			if (oldTileX != tileX || oldTileY != tileY) {
				oldTileX = tileX;
				oldTileY = tileY;
				doDrawAllTiles();
			}
		}
		// System.out.printf("%10s x:%2d y:%2d\n", conf.widgetName, tileCursorX,
		// tileCursorY);
	}

	@Override
	public void onTimeReached(long triggerTime) {
		// TODO Auto-generated method stub

	}

	private int computeTileIndex(int x, int y) {
		return (x + (y * conf.columns));
	}

	private void computeTileSelection(int tileX, int tileY, int mode) {
		if (mode == 0) {
			tileSelectionStarted = false;
			tileSelectionRange.setFrom(tileX);
			tileSelectionRange.setTo(tileY);
		} else if (mode == 1) {
			int index = computeTileIndex(tileX, tileY);
			if (!tileSelectionStarted) {
				tileSelectionRange.setFrom(index);
				tileSelectionStarted = true;
			}
			tileSelectionRange.setTo(index);
			selectedTileIndexList.clear();

			int from = tileSelectionRange.getFrom();
			int to = tileSelectionRange.getTo();
			if (from > to) {
				int d = from;
				from = to;
				to = d;
			}

			for (int i = from; i <= to; i++) {
				selectedTileIndexList.add(i);
			}
		}
	}

	private void resetSelectionList() {
		selectedTileIndexList = new ArrayList<>();
	}

	public void selectAll() {
		if (supportsMultiSelection()) {
			resetSelectionList();
			// computeSelection(true, false);
			doDrawAllTiles();
		}
	}
}
