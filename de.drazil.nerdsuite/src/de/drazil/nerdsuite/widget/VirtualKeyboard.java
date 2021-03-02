package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.Style;

public class VirtualKeyboard extends Composite implements IHitKeyListener {

	private KeyboardElement[][] layout = null;
	private List<IHitKeyListener> list;

	public VirtualKeyboard(Composite parent, int style, PaletteData paletteData) {
		super(parent, style);
		list = new ArrayList<IHitKeyListener>();
		setBackground(Constants.WHITE);
		initLayout();
	}

	public void initLayout() {
		setLayout(null);
		layout = new KeyboardElement[][] { { new KeyboardElement(this, SWT.NONE, 0, Style.FILLER33),
				new KeyboardElement(this, SWT.NONE, 92, Style.KEY), new KeyboardElement(this, SWT.NONE, 49, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 50, Style.KEY), new KeyboardElement(this, SWT.NONE, 51, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 52, Style.KEY), new KeyboardElement(this, SWT.NONE, 53, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 54, Style.KEY), new KeyboardElement(this, SWT.NONE, 55, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 56, Style.KEY), new KeyboardElement(this, SWT.NONE, 57, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 48, Style.KEY), new KeyboardElement(this, SWT.NONE, 43, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 45, Style.KEY), new KeyboardElement(this, SWT.NONE, 92, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 19, Style.KEY), new KeyboardElement(this, SWT.NONE, 20, Style.KEY),
				new KeyboardElement(this, SWT.NONE, 0, Style.FILLER66),
				new KeyboardElement(this, SWT.NONE, 133, Style.KEY1_5) },
				{ new KeyboardElement(this, SWT.NONE, 0, Style.FILLER33),
						new KeyboardElement(this, SWT.NONE, 0, Style.KEY1_5, true, 1),
						new KeyboardElement(this, SWT.NONE, 81, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 87, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 69, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 82, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 84, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 89, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 85, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 73, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 79, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 80, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 64, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 42, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 94, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 0, Style.KEY1_5),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER66),
						new KeyboardElement(this, SWT.NONE, 134, Style.KEY1_5) },
				{ new KeyboardElement(this, SWT.NONE, 0, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 0, Style.KEY, true, 2),
						new KeyboardElement(this, SWT.NONE, 65, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 83, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 68, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 70, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 71, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 72, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 74, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 75, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 76, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 58, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 59, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 61, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 13, Style.KEY2),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER66),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER33),
						new KeyboardElement(this, SWT.NONE, 135, Style.KEY1_5) },
				{ new KeyboardElement(this, SWT.NONE, 0, Style.KEY, true, 3),
						new KeyboardElement(this, SWT.NONE, 0, Style.KEY1_5, true, 2),
						new KeyboardElement(this, SWT.NONE, 90, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 88, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 67, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 86, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 66, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 78, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 77, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 44, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 46, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 47, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 0, Style.KEY1_5, true, 2),
						new KeyboardElement(this, SWT.NONE, 17, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 29, Style.KEY),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER66),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER33),
						new KeyboardElement(this, SWT.NONE, 136, Style.KEY1_5) },
				{ new KeyboardElement(this, SWT.NONE, 0, Style.FILLER),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER),
						new KeyboardElement(this, SWT.NONE, 0, Style.FILLER),
						new KeyboardElement(this, SWT.NONE, 32, Style.KEY9) } };

		int y = 0;
		int s = 40;
		for (KeyboardElement[] row : layout) {
			int x = 0;
			for (KeyboardElement element : row) {
				element.addHitKeyListener(this);
				Point p = element.getDimension(); //
				element.setBounds(x, y, p.x, p.y);
				x += (element.getDimension().x + 2);
			}
			y += (s + 2);
		}

		getParent().layout();
		redraw();
	}

	@Override
	public void keyPressed(int controlType, int key) {
		
		fireHitKey(controlType, key);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(800, 200);
	}

	public void addHitKeyListener(IHitKeyListener listener) {
		list.add(listener);
	}

	public void removeHitKeyListener(IHitKeyListener listener) {
		list.remove(listener);
	}

	private void fireHitKey(int controlType, int keyCode) {
		list.forEach(k -> k.keyPressed(controlType, keyCode));
	}

}
