package de.drazil.nerdsuite.imaging;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidget.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidget.PaintMode;
import de.drazil.nerdsuite.widget.ImagingWidget.TransformationMode;
import de.drazil.nerdsuite.widget.ImagingWidget.TransformationType;
import de.drazil.nerdsuite.widget.ImagingWidget.WidgetMode;
import net.miginfocom.swt.MigLayout;

public class IconEditor {

	private ImagingWidget painter;
	private ImagingWidget previewer;
	private ImagingWidget selector;
	private Composite parent;
	private Button multicolor;
	private Button startAnimation;
	private Text notification;
	private Composite controls;
	private Combo formatSelector;
	private Combo paintModeSelector;
	private byte binaryData[] = null;
	boolean multiColorMode = false;

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		parent.setLayout(new MigLayout());
		// parent.setBackground(Constants.BLACK);

		controls = new Composite(parent, SWT.BORDER);
		controls.setLayout(new MigLayout("fillx"));
		controls.setLayoutData("cell 1 0");

		getMultiColor().setLayoutData("cell 0 0 2 1");
		getFormatSelector().setLayoutData("cell 0 1 2 1");
		getPaintModeSelector().setLayoutData("cell 0 2 2 1");
		getStartAnimation().setLayoutData("cell 0 3 1 1");
		getNotification().setLayoutData("cell 0 4 2 1");

		getPainter().setLayoutData("cell 0 0");
		getPreviewer().setLayoutData("cell 1 0");
		getSelector().setLayoutData("cell 0 1 2 1");

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
			getSelector().clearTiles((e.stateMask & SWT.SHIFT) == SWT.SHIFT);
		});
		MenuItem separator1 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem flipHorizontal = new MenuItem(popup, SWT.NONE);
		flipHorizontal.setText("Flip Horizontal");
		flipHorizontal.setImage(flipHorizontalId.createImage());
		flipHorizontal.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Flip,
					TransformationMode.Horizontal);
		});

		MenuItem flipVertical = new MenuItem(popup, SWT.NONE);
		flipVertical.setText("Flip Vertical");
		flipVertical.setImage(flipVerticalId.createImage());
		flipVertical.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Flip,
					TransformationMode.Vertical);
		});
		MenuItem separator2 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem mirrorUpperHalf = new MenuItem(popup, SWT.NONE);
		mirrorUpperHalf.setText("Mirror Upper Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorUpperHalf.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Mirror,
					TransformationMode.UpperHalf);
		});

		MenuItem mirrorLowerHalf = new MenuItem(popup, SWT.NONE);
		mirrorLowerHalf.setText("Mirror Lower Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorLowerHalf.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Mirror,
					TransformationMode.LowerHalf);
		});

		MenuItem mirrorLeftHalf = new MenuItem(popup, SWT.NONE);
		mirrorLeftHalf.setText("Mirror Left Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorLeftHalf.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Mirror,
					TransformationMode.LeftHalf);
		});
		MenuItem mirrorRightHalf = new MenuItem(popup, SWT.NONE);
		mirrorRightHalf.setText("Mirror Right Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorRightHalf.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Mirror,
					TransformationMode.RightHalf);
		});
		MenuItem separator3 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem rotateCW = new MenuItem(popup, SWT.NONE);
		rotateCW.setText("Rotate CW");
		rotateCW.setImage(rotateCWId.createImage());
		rotateCW.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Rotate,
					TransformationMode.CW);
		});

		MenuItem rotateCCW = new MenuItem(popup, SWT.NONE);
		rotateCCW.setText("Rotate CCW");
		rotateCCW.setImage(rotateCCWId.createImage());
		rotateCCW.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Rotate,
					TransformationMode.CCW);
		});

		MenuItem separator4 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem shiftUp = new MenuItem(popup, SWT.NONE);
		shiftUp.setText("Shift Up");
		shiftUp.setImage(upId.createImage());
		shiftUp.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Shift,
					TransformationMode.Up);
		});

		MenuItem shiftDown = new MenuItem(popup, SWT.NONE);
		shiftDown.setText("Shift Down");
		shiftDown.setImage(downId.createImage());
		shiftDown.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Shift,
					TransformationMode.Down);
		});

		MenuItem shiftLeft = new MenuItem(popup, SWT.NONE);
		shiftLeft.setText("Shift Left");
		shiftLeft.setImage(leftId.createImage());
		shiftLeft.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Shift,
					TransformationMode.Left);
		});

		MenuItem shiftRight = new MenuItem(popup, SWT.NONE);
		shiftRight.setText("Shift Right");
		shiftRight.setImage(rightId.createImage());
		shiftRight.addListener(SWT.Selection, e -> {
			getSelector().transform((e.stateMask & SWT.SHIFT) == SWT.SHIFT, TransformationType.Shift,
					TransformationMode.Right);
		});

		MenuItem separator5 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem swapTiles = new MenuItem(popup, SWT.NONE);
		swapTiles.setText("Swap Selected Tiles");
		swapTiles.setImage(swapId.createImage());
		swapTiles.addListener(SWT.Selection, e -> {
			getSelector().swapTiles();
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

	private ImagingWidget getPreviewer() {
		if (previewer == null) {
			previewer = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			previewer.setWidgetName("Preview :");
			previewer.setWidgetMode(WidgetMode.VIEWER);
			previewer.setWidth(8);
			previewer.setHeight(8);
			previewer.setPixelSize(3);
			previewer.setPixelGridEnabled(false);
			previewer.setGridStyle(GridStyle.PIXEL);
			previewer.setTileGridEnabled(false);
			previewer.setTileCursorEnabled(false);
			previewer.setMultiColorEnabled(multiColorMode);
			previewer.setSelectedTileOffset(0);
			previewer.setBitlane(getBinaryData());
			previewer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			previewer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			previewer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			previewer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			previewer.setSelectedColor(1);
			previewer.recalc();
		}
		return previewer;
	}

	private ImagingWidget getSelector() {
		if (selector == null) {
			selector = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL) {

				@Override
				protected boolean isClearTileConfirmed(boolean allSelected) {
					return MessageDialog.openQuestion(parent.getShell(), "Question", MessageFormat.format(
							"Do you really want to clear {0} ?", allSelected ? "all selected  tiles" : "this tile"));
				}

				@Override
				protected void showMessage(String message) {
					MessageDialog.openInformation(parent.getShell(), "Information", message);
				}

				@Override
				protected void setNotification(int offset, int tileSize) {

					getNotification().setText(MessageFormat.format("Offset: ${0} tile:{1} bytes",
							String.format("%04X", offset), tileSize));
				}

				@Override
				protected boolean isRotationConfirmed() {
					return MessageDialog.openQuestion(parent.getShell(), "Question",
							"Rotating these tile(s) causes data loss, because it is/they are not squarish.\n\nDo you want to rotate anyway?");
				}

				@Override
				protected void notifyAnimationStarted(boolean state) {
					startAnimation.setText(state ? "Stop Animation" : "Start Animation");
				}
			};
			selector.setWidgetName("Selector:");
			selector.setWidgetMode(WidgetMode.SELECTOR);
			selector.setWidth(8);
			selector.setHeight(8);
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
			selector.addDrawListener(getPreviewer());
		}
		return selector;

	}

	private Text getNotification() {
		if (notification == null) {
			notification = new Text(controls, SWT.NONE);
			notification.setEnabled(false);
		}
		return notification;
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
			startAnimation.setSelection(false);
			startAnimation.setText("Start Animation");
			startAnimation.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (!getSelector().isAnimationRunning()) {
						selector.startAnimation();
					} else {
						selector.stopAnimation();
					}
				}
			});
		}
		return startAnimation;
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
			getPreviewer().setWidth(8);
			getPreviewer().setHeight(8);
			getPreviewer().setTileColumns(1);
			getPreviewer().setTileRows(1);
			getPreviewer().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(1);
			getSelector().setTileRows(1);
			getSelector().setColumns(16);
			getSelector().setRows(16);
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
			getPreviewer().setWidth(8);
			getPreviewer().setHeight(8);
			getPreviewer().setTileColumns(2);
			getPreviewer().setTileRows(1);
			getPreviewer().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(2);
			getSelector().setTileRows(1);
			getSelector().setColumns(8);
			getSelector().setRows(16);
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
			getPreviewer().setWidth(8);
			getPreviewer().setHeight(8);
			getPreviewer().setTileColumns(1);
			getPreviewer().setTileRows(2);
			getPreviewer().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(1);
			getSelector().setTileRows(2);
			getSelector().setColumns(16);
			getSelector().setRows(8);
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
			getPreviewer().setWidth(8);
			getPreviewer().setHeight(8);
			getPreviewer().setTileColumns(2);
			getPreviewer().setTileRows(2);
			getPreviewer().recalc();
			getSelector().setWidth(8);
			getSelector().setHeight(8);
			getSelector().setTileColumns(2);
			getSelector().setTileRows(2);
			getSelector().setColumns(8);
			getSelector().setRows(8);
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
			getPreviewer().setWidth(24);
			getPreviewer().setHeight(21);
			getPreviewer().setTileColumns(1);
			getPreviewer().setTileRows(1);
			getPreviewer().recalc();
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
			getPreviewer().setWidth(24);
			getPreviewer().setHeight(21);
			getPreviewer().setTileColumns(2);
			getPreviewer().setTileRows(1);
			getPreviewer().recalc();
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
			getPreviewer().setWidth(24);
			getPreviewer().setHeight(21);
			getPreviewer().setTileColumns(1);
			getPreviewer().setTileRows(2);
			getPreviewer().recalc();
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
			getPreviewer().setWidth(24);
			getPreviewer().setHeight(21);
			getPreviewer().setTileColumns(2);
			getPreviewer().setTileRows(2);
			getPreviewer().recalc();
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

			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL url = bundle.getEntry("/fonts/c64_lower.64c");
			// URL url = bundle.getEntry("/images/galencia_dump2");
			File file = null;

			try {
				file = new File(FileLocator.resolve(url).toURI());
				URL resolvedUrl = FileLocator.toFileURL(url);
				URI resolvedUri = new URI(resolvedUrl.getProtocol(), resolvedUrl.getPath(), null);
				file = new File(resolvedUri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				binaryData = BinaryFileReader.readFile(file, 2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * binaryData = new byte[0xffff]; for (int i = 0; i <
			 * binaryData.length; i++) binaryData[i] = 0;
			 */
		}
		return binaryData;
	}

}
