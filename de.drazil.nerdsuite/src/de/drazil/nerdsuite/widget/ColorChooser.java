package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.model.PlatformColor;

public class ColorChooser extends BaseWidget implements PaintListener {

	private int id;
	private int columns;
	private int rows;
	private int width;
	private int height;
	private List<PlatformColor> platformColorList;
	private List<IColorSelectionListener> colorSelectionListener;

	private static final int COLOR_TILE_SIZE = 25;

	public ColorChooser(Composite parent, int style, int id) {
		super(parent, style);
		this.id = id;
		colorSelectionListener = new ArrayList<IColorSelectionListener>();
		addPaintListener(this);
		
	}

	public void setPlatformColors(List<PlatformColor> platformColorList) {
		this.platformColorList = platformColorList;
		columns = platformColorList.size() / 4;
		rows = (platformColorList.size() / columns) ;
		width = columns * COLOR_TILE_SIZE;
		height = rows * COLOR_TILE_SIZE;
		setSize(width, height);
 	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setLineWidth(2);

		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				e.gc.setBackground(platformColorList.get(c * columns + r).getColor());
				e.gc.fillRectangle(c * COLOR_TILE_SIZE, r * COLOR_TILE_SIZE, COLOR_TILE_SIZE, COLOR_TILE_SIZE);
			}
		}
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		setVisible(false);
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {

	}

	public void addColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.add(listener);
	}

	public void removeColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.remove(listener);
	}

	private void fireColorSelected(int colorIndex) {
		colorSelectionListener.forEach(l -> l.colorSelected(id, colorIndex));
	}

	
}
