package de.drazil.nerdsuite.imaging;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import de.drazil.nerdsuite.widget.ImagingWidget.PaintMode;
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
	private Combo paintModeSelector;
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
		getPaintModeSelector().setLayoutData("cell 0 3 2 1");
		getStartAnimation().setLayoutData("cell 0 4 1 1");
		getStopAnimation().setLayoutData("cell 1 4  1 1");

		ImageDescriptor upId = null;
		ImageDescriptor downId = null;
		ImageDescriptor leftId = null;
		ImageDescriptor rightId = null;
		ImageDescriptor cutId = null;
		ImageDescriptor copyId = null;
		ImageDescriptor pasteId = null;
		ImageDescriptor rotateCWId = null;
		ImageDescriptor rotateCCWId = null;
		ImageDescriptor flipHorizontalId = null;
		ImageDescriptor flipVerticalId = null;
		ImageDescriptor swapId = null;
		ImageDescriptor removeSwapMarkerId = null;
		try {
			cutId = ImageDescriptor.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/cut.png"));
			copyId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/page_white_copy.png"));
			pasteId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/paste_plain.png"));
			rotateCWId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_rotate_clockwise.png"));
			rotateCCWId = ImageDescriptor.createFromURL(
					new URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_rotate_anticlockwise.png"));
			flipHorizontalId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_flip_horizontal.png"));
			flipVerticalId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_flip_vertical.png"));
			upId = ImageDescriptor.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_up.png"));
			downId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_down.png"));
			leftId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_left.png"));
			rightId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_right.png"));
			swapId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_switch.png"));
			removeSwapMarkerId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/cross.png"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Menu popup = new Menu(getSelector());
		MenuItem cut = new MenuItem(popup, SWT.NONE);
		cut.setText("Cut");
		cut.setImage(cutId.createImage());
		cut.addListener(SWT.Selection, e -> {
			getSelector().clipboardAction(ImagingWidget.ClipboardAction.Cut);
		});
		MenuItem copy = new MenuItem(popup, SWT.NONE);
		copy.setText("Copy");
		copy.setImage(copyId.createImage());
		copy.addListener(SWT.Selection, e -> {
			getSelector().clipboardAction(ImagingWidget.ClipboardAction.Copy);
		});
		MenuItem paste = new MenuItem(popup, SWT.NONE);
		paste.setText("Paste");
		paste.setImage(pasteId.createImage());
		paste.addListener(SWT.Selection, e -> {
			getSelector().clipboardAction(ImagingWidget.ClipboardAction.Paste);
		});
		MenuItem clear = new MenuItem(popup, SWT.NONE);
		clear.setText("Clear");
		clear.addListener(SWT.Selection, e -> {
			getSelector().clearTile();
		});
		MenuItem separator1 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem flipHorizontal = new MenuItem(popup, SWT.NONE);
		flipHorizontal.setText("Flip Horizontal");
		flipHorizontal.setImage(flipHorizontalId.createImage());

		MenuItem flipVertical = new MenuItem(popup, SWT.NONE);
		flipVertical.setText("Flip Vertical");
		flipVertical.setImage(flipVerticalId.createImage());

		MenuItem separator2 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem rotateCW = new MenuItem(popup, SWT.NONE);
		rotateCW.setText("Rotate CW");
		rotateCW.setImage(rotateCWId.createImage());

		MenuItem rotateCCW = new MenuItem(popup, SWT.NONE);
		rotateCCW.setText("Rotate CCW");
		rotateCCW.setImage(rotateCCWId.createImage());

		MenuItem separator3 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem shiftUp = new MenuItem(popup, SWT.NONE);
		shiftUp.setText("Shift Up");
		shiftUp.setImage(upId.createImage());

		MenuItem shiftDown = new MenuItem(popup, SWT.NONE);
		shiftDown.setText("Shift Down");
		shiftDown.setImage(downId.createImage());

		MenuItem shiftLeft = new MenuItem(popup, SWT.NONE);
		shiftLeft.setText("Shift Left");
		shiftLeft.setImage(leftId.createImage());

		MenuItem shiftRight = new MenuItem(popup, SWT.NONE);
		shiftRight.setText("Shift Right");
		shiftRight.setImage(rightId.createImage());

		MenuItem separator4 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem swapTiles = new MenuItem(popup, SWT.NONE);
		swapTiles.setText("Swap Selected Tiles");
		swapTiles.setImage(swapId.createImage());
		swapTiles.addListener(SWT.Selection, e -> {
			getSelector().swapTiles();
		});
		MenuItem swapTarget = new MenuItem(popup, SWT.NONE);
		swapTarget.setText("Mark As Swap Target");
		swapTarget.addListener(SWT.Selection, e -> {
			getSelector().markAsSwapTarget();
		});
		MenuItem removeSwapMarkers = new MenuItem(popup, SWT.NONE);
		removeSwapMarkers.setText("Delete Swap Targets");
		removeSwapMarkers.setImage(removeSwapMarkerId.createImage());
		removeSwapMarkers.addListener(SWT.Selection, e -> {
			getSelector().removeSwapMarker();
		});

		setPaintFormat("Char");
		setPaintMode("Pixel");
		getFormatSelector().select(0);
		getPaintModeSelector().select(0);
		getSelector().setMenu(popup);
	}

	private ImagingWidget getPainter() {
		if (painter == null) {
			painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			painter.setWidgetName("Painter :");
			painter.setWidgetMode(WidgetMode.PAINTER);
			painter.setWidth(8);
			painter.setHeight(8);
			painter.setPixelGridEnabled(true);
			painter.setGridStyle(GridStyle.PIXEL);
			painter.setTileGridEnabled(true);
			painter.setTileCursorEnabled(false);
			painter.setMultiColorEnabled(multiColorMode);
			painter.setSelectedTileOffset(0);
			painter.setBitlane(getBinaryData());
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
			selector = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL) {
				@Override
				protected boolean isClearSwapBufferConfirmed() {
					return MessageDialog.openQuestion(parent.getShell(), "Question",
							"Do really want to clear the SwapBuffer?");
				}

				@Override
				protected boolean isClearTileConfirmed() {
					return MessageDialog.openQuestion(parent.getShell(), "Question",
							"Do you really want to clear the tile?");
				}
			};
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
			selector.setBitlane(getBinaryData());
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
					// selector.stopAnimation();
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
					setPaintFormat(c.getItem(index));
				}
			});
		}
		return formatSelector;
	}

	private Combo getPaintModeSelector() {
		if (paintModeSelector == null) {
			paintModeSelector = new Combo(controls, SWT.DROP_DOWN);
			paintModeSelector.setItems(new String[] { "Pixel", "VerticalMirror", "HorizontalMirror", "Kaleidoscope" });
			paintModeSelector.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Combo c = ((Combo) e.getSource());
					int index = c.getSelectionIndex();
					setPaintMode(c.getItem(index));
				}
			});
		}
		return paintModeSelector;
	}

	private void setPaintMode(String paintMode) {
		switch (paintMode) {
		case "Pixel": {
			getPainter().setPaintMode(PaintMode.Pixel);
			break;
		}
		case "VerticalMirror": {
			getPainter().setPaintMode(PaintMode.VerticalMirror);
			break;
		}
		case "HorizontalMirror": {
			getPainter().setPaintMode(PaintMode.HorizontalMirror);
			break;
		}
		case "Kaleidoscope": {
			getPainter().setPaintMode(PaintMode.Kaleidoscope);
			break;
		}
		}
	}

	private void setPaintFormat(String format) {
		switch (format) {
		case "Char": {
			getPainter().setWidth(8);
			getPainter().setHeight(8);
			getPainter().setTileColumns(1);
			getPainter().setTileRows(1);
			getPainter().setPixelSize(40);
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
