package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.Key;
import de.drazil.nerdsuite.model.KeyMatrix;
import de.drazil.nerdsuite.model.KeyRow;
import de.drazil.nerdsuite.model.PlatformColor;

public class VirtualKeyboard extends Composite implements IHitKeyListener {

	private List<PlatformColor> colorList;
	private List<IHitKeyListener> list;
	private int optionState;
	private KeyMatrix matrix = null;
	private List<KeyboardElement> keyList;

	public VirtualKeyboard(Composite parent, int style, List<PlatformColor> colorList) {
		super(parent, style);
		this.colorList = colorList;
		list = new ArrayList<IHitKeyListener>();
		keyList = new ArrayList<KeyboardElement>();
		setBackground(Constants.GREY3);
		initLayout();
	}

	public void initLayout() {

		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		ObjectMapper mapper = new ObjectMapper();
		try {
			matrix = mapper.readValue(bundle.getEntry("configuration/keyboard_layout.json"), KeyMatrix.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(null);
		int y = 0;
		int s = 30;
		int index = 0;
		for (KeyRow row : matrix.getKeyRows()) {
			int x = 0;
			for (Key key : row.getKeys()) {
				key.setId(index);
				KeyboardElement ke = new KeyboardElement(this, SWT.NONE, key, colorList);
				keyList.add(ke);
				ke.addHitKeyListener(this);
				Point p = ke.getDimension();
				ke.setBounds(x, y, p.x, p.y);
				x += (ke.getDimension().x);
				index++;
			}
			y += s;
		}
	}

	@Override
	public void keyPressed(Key key) {
		fireHitKey(key);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(620, 7 * 30);
	}

	public void addHitKeyListener(IHitKeyListener listener) {
		list.add(listener);
	}

	public void removeHitKeyListener(IHitKeyListener listener) {
		list.remove(listener);
	}

	private void fireHitKey(Key key) {
		if (key.getType().equals("OPTION")) {
			if (key.isToggleState()) {
				key.setOptionState(1);
				optionState |= key.getIndex();
			} else {
				optionState &= ((key.getIndex() ^ 0b11111) & 0b11111);
				key.setOptionState(0);
			}
			// String value = String.format("%5s",
			// Integer.toBinaryString(optionState)).replace(" ", "0");
			// System.out.printf("%-15s toggleState:%-5s options:%s\n", key.getText(),
			// key.isToggleState(), value);

			for (KeyRow row : matrix.getKeyRows()) {
				for (Key k : row.getKeys()) {
					if (k.getType().equals("KEY")) {
						k.setOptionState(optionState);
						keyList.get(k.getId()).redraw();
					}
				}
			}

		}
		list.forEach(k -> k.keyPressed(key));
	}
}
