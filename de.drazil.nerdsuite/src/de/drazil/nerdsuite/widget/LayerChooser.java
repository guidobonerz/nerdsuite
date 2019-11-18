package de.drazil.nerdsuite.widget;

import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.ITileSelectionListener;
import de.drazil.nerdsuite.imaging.service.PaintTileService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;

public class LayerChooser extends BaseWidget implements PaintListener, ITileSelectionListener {

	private static final int WIDTH = 180;
	private static final int HEIGHT = 300;
	private String serviceOwnerId;
	private Tile tile;
	private PaintTileService paintTileService;

	public LayerChooser(Composite parent, int style, String serviceOwnerId) {
		super(parent, style);
		this.serviceOwnerId = serviceOwnerId;
		parent.addPaintListener(this);
		paintTileService = ServiceFactory.getService(serviceOwnerId, PaintTileService.class);
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setBackground(Constants.DARK_GREY);
		e.gc.fillRectangle(0, 0, WIDTH, HEIGHT);

	}

	@Override
	public void tileIndexesSelected(List<Integer> selectedTileIndexList) {
		if (selectedTileIndexList != null && selectedTileIndexList.size() == 1) {
			TileRepositoryService repository = ServiceFactory.getService(serviceOwnerId, TileRepositoryService.class);
			this.tile = repository.getTile(selectedTileIndexList.get(0));
			redraw();
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(WIDTH, HEIGHT);
	}

}
