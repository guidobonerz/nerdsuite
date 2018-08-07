package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidget.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidget.WidgetMode;
import net.miginfocom.swt.MigLayout;

public class IconEditor {

	private ImagingWidget painter;
	private ImagingWidget selector;
	private Composite parent;
	private Button multicolor;
	private Button startAnimation;
	private Button stopAnimation;
	private Button clearMemory;
	private Composite controls;
	private Combo formatSelector;
	private byte binaryData[] = null;
	boolean multiColorMode = false;

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		parent.setLayout(new MigLayout());

		getPainter().setLayoutData("cell 0 0");
		getSelector().setLayoutData("cell 0 1 2 1");
		controls = new Composite(parent, SWT.BORDER);
		controls.setLayout(new MigLayout("fill"));
		controls.setLayoutData("cell 1 0");

		getMultiColor().setLayoutData("cell 0 0 2 1");
		getClearMemory().setLayoutData("cell 0 1 2 1");
		getFormatSelector().setLayoutData("cell 0 2 2 1");
		getStartAnimation().setLayoutData("cell 0 3 1 1");
		getStopAnimation().setLayoutData("cell 1 3  1 1");

		Menu popup = new Menu(getSelector());
		MenuItem cut = new MenuItem(popup, SWT.NONE);
		cut.setText("Cut");
		cut.addListener(SWT.Selection, e -> {
			getSelector().clipboardAction(ImagingWidget.ClipboardAction.Cut);
		});
		MenuItem copy = new MenuItem(popup, SWT.NONE);
		copy.setText("Copy");
		copy.addListener(SWT.Selection, e -> {
			getSelector().clipboardAction(ImagingWidget.ClipboardAction.Copy);
		});
		MenuItem paste = new MenuItem(popup, SWT.NONE);
		paste.setText("Paste");
		paste.addListener(SWT.Selection, e -> {
			getSelector().clipboardAction(ImagingWidget.ClipboardAction.Paste);
		});
		MenuItem separator = new MenuItem(popup, SWT.SEPARATOR);
		MenuItem clear = new MenuItem(popup, SWT.NONE);
		clear.setText("Clear");
		MenuItem flip = new MenuItem(popup, SWT.NONE);
		flip.setText("Flip");
		getSelector().setMenu(popup);

		MenuItem swapTiles = new MenuItem(popup, SWT.NONE);
		swapTiles.setText("Swap Selected Tiles");
		swapTiles.addListener(SWT.Selection, e -> {
			getSelector().swapTiles();
		});
		MenuItem swapTarget = new MenuItem(popup, SWT.NONE);
		swapTarget.setText("Mark As Swap Target");
		swapTarget.addListener(SWT.Selection, e -> {
			getSelector().markAsSwapTarget();
		});
		MenuItem deleteSwapTargets = new MenuItem(popup, SWT.NONE);
		deleteSwapTargets.setText("Delete Swap Targets");
		deleteSwapTargets.addListener(SWT.Selection, e -> {
			getSelector().clearSwapBuffer();
		});

		setFormat("Char");
	}

	private ImagingWidget getPainter() {
		if (painter == null) {
			painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			painter.setWidgetName("Painter :");
			painter.setWidgetMode(WidgetMode.PAINTER);
			painter.setWidth(8);
			painter.setHeight(8);
			painter.setPixelSize(20);
			painter.setPixelGridEnabled(true);
			painter.setGridStyle(GridStyle.PIXEL);
			painter.setTileGridEnabled(true);
			painter.setTileCursorEnabled(false);
			painter.setMultiColorEnabled(multiColorMode);
			painter.setSelectedTileOffset(0);
			painter.setContent(getBinaryData());
			painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			painter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			painter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			painter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			painter.setSelectedColor(1);
			painter.addDrawListener(getSelector());
		}
		return painter;
	}

	private ImagingWidget getSelector() {
		if (selector == null) {
			selector = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
			selector.setWidgetName("Selector:");
			selector.setWidgetMode(WidgetMode.SELECTOR);
			selector.setWidth(8);
			selector.setHeight(8);
			selector.setColumns(8);
			selector.setRows(3);
			selector.setPixelSize(3);
			selector.setPixelGridEnabled(false);
			selector.setTileGridEnabled(true);
			selector.setTileSubGridEnabled(false);
			selector.setTileCursorEnabled(true);
			selector.setSeparatorEnabled(false);
			selector.setMultiColorEnabled(multiColorMode);
			selector.setSelectedTileOffset(0);
			selector.setContent(getBinaryData());
			selector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			selector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			selector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			selector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			selector.setSelectedColor(1);
			selector.addDrawListener(getPainter());
		}
		return selector;
	}

	private Button getMultiColor() {
		if (multicolor == null) {
			multicolor = new Button(controls, SWT.CHECK);
			multicolor.setText("MultiColor");
			multicolor.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					painter.setMultiColorEnabled(multicolor.getSelection());
					painter.recalc();
					selector.setMultiColorEnabled(multicolor.getSelection());
					selector.recalc();
				}
			});
		}
		return multicolor;
	}

	private Button getStartAnimation() {
		if (startAnimation == null) {
			startAnimation = new Button(controls, SWT.PUSH);
			startAnimation.setText("Start Animation");
			startAnimation.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					selector.startAnimation();
				}
			});
		}
		return startAnimation;
	}

	private Button getStopAnimation() {
		if (stopAnimation == null) {
			stopAnimation = new Button(controls, SWT.PUSH);
			stopAnimation.setText("Stop Animation");
			stopAnimation.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event event) {
					selector.stopAnimation();
				}
			});
		}
		return stopAnimation;
	}

	private Button getClearMemory() {
		if (clearMemory == null) {
			clearMemory = new Button(controls, SWT.PUSH);
			clearMemory.setText("Clear Memory");
			clearMemory.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event event) {
					selector.stopAnimation();
				}
			});
		}
		return clearMemory;
	}

	private Combo getFormatSelector() {
		if (formatSelector == null) {
			formatSelector = new Combo(controls, SWT.DROP_DOWN);
			formatSelector.setItems(new String[] { "Char", "Char 2X", "Char 2Y", "Char 2XY", "Sprite", "Sprite 2X",
					"Sprite 2Y", "Sprite 2XY" });
			formatSelector.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Combo c = ((Combo) e.getSource());
					int index = c.getSelectionIndex();
					setFormat(c.getItem(index));
				}
			});
		}
		return formatSelector;
	}

	private void setFormat(String format) {
		switch (format) {
		case "Char": {
			getPainter().setWidth(8);
			getPainter().setHeight(8);
			getPainter().setTileColumns(1);
			getPainter().setTileRows(1);
			getPainter().setPixelSize(20);
			getPainter().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(1);
			getSelector().setTileRows(1);
			getSelector().setPixelSize(3);
			getSelector().recalc();
			parent.layout();
			break;
		}
		case "Char 2X": {
			getPainter().setWidth(8);
			getPainter().setHeight(8);
			getPainter().setTileColumns(2);
			getPainter().setTileRows(1);
			getPainter().setPixelSize(20);
			getPainter().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(2);
			getSelector().setTileRows(1);
			getSelector().setPixelSize(3);
			getSelector().recalc();

			parent.layout();
			break;
		}

		case "Char 2Y": {
			getPainter().setWidth(8);
			getPainter().setHeight(8);
			getPainter().setTileColumns(1);
			getPainter().setTileRows(2);
			getPainter().setPixelSize(20);
			getPainter().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(1);
			getSelector().setTileRows(2);
			getSelector().setPixelSize(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Char 2XY": {
			getPainter().setWidth(8);
			getPainter().setHeight(8);
			getPainter().setTileColumns(2);
			getPainter().setTileRows(2);
			getPainter().setPixelSize(20);
			getPainter().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(2);
			getSelector().setTileRows(2);
			getSelector().setPixelSize(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite": {
			getPainter().setWidth(24);
			getPainter().setHeight(21);
			getPainter().setTileColumns(1);
			getPainter().setTileRows(1);
			getPainter().setPixelSize(10);
			getPainter().recalc();
			getSelector().setWidth(24);
			getSelector().setHeight(21);
			getSelector().setTileColumns(1);
			getSelector().setTileRows(1);
			getSelector().setPixelSize(2);
			getSelector().setColumns(16);
			getSelector().setRows(6);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite 2X": {
			getPainter().setWidth(24);
			getPainter().setHeight(21);
			getPainter().setTileColumns(2);
			getPainter().setTileRows(1);
			getPainter().setPixelSize(10);
			getPainter().recalc();
			getSelector().setWidth(24);
			getSelector().setHeight(21);
			getSelector().setTileColumns(2);
			getSelector().setTileRows(1);
			getSelector().setPixelSize(2);
			getSelector().setColumns(8);
			getSelector().setRows(6);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite 2Y": {
			getPainter().setWidth(24);
			getPainter().setHeight(21);
			getPainter().setTileColumns(1);
			getPainter().setTileRows(2);
			getPainter().setPixelSize(10);
			getPainter().recalc();
			getSelector().setWidth(24);
			getSelector().setHeight(21);
			getSelector().setTileColumns(1);
			getSelector().setTileRows(2);
			getSelector().setPixelSize(2);
			getSelector().setColumns(16);
			getSelector().setRows(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite 2XY": {
			getPainter().setWidth(24);
			getPainter().setHeight(21);
			getPainter().setTileColumns(2);
			getPainter().setTileRows(2);
			getPainter().setPixelSize(10);
			getPainter().recalc();
			getSelector().setWidth(24);
			getSelector().setHeight(21);
			getSelector().setTileColumns(2);
			getSelector().setTileRows(2);
			getSelector().setPixelSize(2);
			getSelector().setColumns(8);
			getSelector().setRows(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		}

	}

	private byte[] getBinaryData() {
		if (binaryData == null) {
			binaryData = new byte[0xffff];
			for (int i = 0; i < binaryData.length; i++)
				binaryData[i] = 0;
		}
		return binaryData;
	}

}
