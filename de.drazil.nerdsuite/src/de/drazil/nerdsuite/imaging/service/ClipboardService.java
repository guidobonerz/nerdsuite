package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.widget.Tile;

public class ClipboardService implements IService {

	private String owner;
	public final static int OFF = 0;
	public final static int CUT = 1;
	public final static int COPY = 2;
	public final static int PASTE = 4;
	private int initialAction;
	private List<Integer> selectionList;

	@Override
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void clipboardAction(int action) {
		TileRepositoryService service = ServiceFactory.getService(owner, TileRepositoryService.class);
		if (action == CUT || action == COPY) {
			initialAction = action;
			selectionList = new ArrayList<Integer>(service.getSelectedTileIndexList());
		} else if (action == PASTE) {
			List<Integer> targetSelectionList = service.getSelectedTileIndexList();
			for (int i = 0; i < selectionList.size(); i++) {
				Tile sourceTile = service.getTile(selectionList.get(i));
				Tile targetTile = service.getTile(targetSelectionList.get(i));
				targetTile.getActiveLayer().setContent(sourceTile.getActiveLayer().getContent());
				if (initialAction == CUT) {
					sourceTile.resetActiveLayer();
				}
				service.redrawTileViewer(targetSelectionList, ImagePainterFactory.UPDATE, false);
			}
		} else {
		}
	}
}
