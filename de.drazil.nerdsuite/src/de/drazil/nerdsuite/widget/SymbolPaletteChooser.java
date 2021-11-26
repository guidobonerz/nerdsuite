package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.CharMap;
import de.drazil.nerdsuite.model.CharObject;
import de.drazil.nerdsuite.model.PlatformColor;

public class SymbolPaletteChooser extends BaseWidget implements PaintListener {

	private int columns;
	private int rows;
	private int width;
	private int height;
	private int cx;
	private int cy;
	private int charIndex;
	private int maxChars;
	private boolean mouseIn = false;
	private CharMap charMap;
	private List<CharObject> charList;
	private List<ICharSelectionListener> charSelectionListener;
	private List<PlatformColor> colorList;

	private static final int CHAR_TILE_SIZE = 18;

	public SymbolPaletteChooser(Composite parent, int style, CharMap charMap, List<PlatformColor> colorList) {
		super(parent, style);
		setSymbolMap(charMap);
		charSelectionListener = new ArrayList<ICharSelectionListener>();
		addPaintListener(this);
		this.colorList = colorList;
	}

	public void setSymbolMap(CharMap charMap) {
		this.charMap = charMap;
		this.charList = charMap.getCharMap().stream().filter(e -> e.isUpper() == true).collect(Collectors.toList());
		columns = charList.size() / 16;
		rows = (charList.size() / columns);
		width = columns * CHAR_TILE_SIZE;
		height = rows * CHAR_TILE_SIZE;
		setSize(width, height + 20);
		maxChars = charList.size() - 1;
	}

	public void setSelectedColor(int index) {
		cx = index % columns;
		cy = index / columns;
		charIndex = index;
		redraw();
	}

	@Override
	public void paintControl(PaintEvent e) {
		int thickness = 2;
		e.gc.setLineWidth(thickness);
		e.gc.setBackground(Constants.DARK_GREY);
		e.gc.fillRectangle(0, 0, height, width);
		for (int r = 0; r < columns; r++) {
			for (int c = 0; c < rows; c++) {
				int i = (c + (r * columns));
				e.gc.setFont(Constants.C64_Pro_Mono_FONT_12);
				e.gc.setForeground(Constants.WHITE);

				CharObject cm = charList.get(charMap.getUpperIndexOrderList().get(i));
				if (!cm.isColor() && !cm.isControl()) {
					e.gc.drawString(String.valueOf(cm.getUnicode()), c * CHAR_TILE_SIZE, r * CHAR_TILE_SIZE);
				} else if (cm.isColor()) {
					e.gc.setBackground(colorList.get(Integer.valueOf(cm.getCustomValue())).getColor());
					e.gc.fillRectangle(1 + c * CHAR_TILE_SIZE, 1 + r * CHAR_TILE_SIZE, CHAR_TILE_SIZE - thickness,
							CHAR_TILE_SIZE - thickness);
				} else if (cm.isControl()) {
					e.gc.setFont(Constants.GoogleMaterials_12);
					e.gc.drawString(String.valueOf(cm.getAltUnicode()), c * CHAR_TILE_SIZE, r * CHAR_TILE_SIZE);
				}

				e.gc.setBackground(Constants.DARK_GREY);
				if (c == cx && r == cy) {
					e.gc.setForeground(Constants.BRIGHT_ORANGE);
					e.gc.drawRectangle(1 + cx * CHAR_TILE_SIZE, 1 + cy * CHAR_TILE_SIZE, CHAR_TILE_SIZE - thickness,
							CHAR_TILE_SIZE - thickness);
				}
			}
		}
		e.gc.setForeground(Constants.DARK_GREY);
		e.gc.fillRectangle(0, height, width, 20);
		e.gc.setFont(Constants.RobotoMonoBold_FONT);
		e.gc.setForeground(Constants.WHITE);
		CharObject cm = charList.get(charMap.getUpperIndexOrderList().get(charIndex));
		e.gc.drawString(String.format("$%02X(%03d) - %s", cm.getId(), cm.getId(), cm.getName()), 5, height);
	}

	@Override
	protected void leftMouseButtonClickedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		close();
		fireCharSelected(charIndex);
		charSelectionListener.clear();
	}

	@Override
	protected void leftMouseButtonPressedInternal(int modifierMask, int x, int y) {
		if (!mouseIn) {
			close();
		}
	}

	@Override
	protected void mouseEnterInternal(int modifierMask, int x, int y) {
		mouseIn = true;
	}

	@Override
	protected void mouseExitInternal(int modifierMask, int x, int y) {
		mouseIn = false;
	}

	@Override
	protected void mouseMoveInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		redraw();
	}

	private void close() {
		Object o = getParent().getParent();
		if (o instanceof Shell) {
			((Shell) o).close();
		}
	}

	public void addCharSelectionListener(ICharSelectionListener listener) {
		charSelectionListener.add(listener);
	}

	public void removeCharSelectionListener(ICharSelectionListener listener) {
		charSelectionListener.remove(listener);
	}

	private void fireCharSelected(int charIndex) {
		int index = charMap.getUpperIndexOrderList().get(charIndex);
		charSelectionListener.forEach(l -> l.charSelected(charIndex, charList.get(index).getUnicode()));
	}

	private void computeCursorPosition(int x, int y) {
		int icx = x / CHAR_TILE_SIZE;
		int icy = y / CHAR_TILE_SIZE;
		int idx = (icx + (icy * columns));
		if (idx <= maxChars) {
			charIndex = idx;
			cx = icx;
			cy = icy;
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return getSize();
	}
}
