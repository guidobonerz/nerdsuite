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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.widget.ConfigurationDialog;
import de.drazil.nerdsuite.widget.IConfigurationListener;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidget.ImagingServiceDescription;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.PaintMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.WidgetMode;
import net.miginfocom.swt.MigLayout;

public class IconEditor implements IConfigurationListener {

	private ImagingWidget painter;
	private ImagingWidget previewer;
	private ImagingWidget selector;
	private Scale animationTimerDelayScale;
	private Composite parent;
	private Button multicolor;
	private Button startAnimation;
	private Text notification;
	private Composite controls;
	private Combo formatSelector;
	private Combo paintModeSelector;
	private byte binaryData[] = null;
	boolean multiColorMode = false;
	private ConfigurationDialog configurationDialog = null;

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		parent.setLayout(new MigLayout());
		// parent.setBackground(Constants.BLACK);

		controls = new Composite(parent, SWT.BORDER);
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		controls.setLayout(layout);
		controls.setLayoutData("cell 1 0");

		getMultiColor();
		getFormatSelector();
		getPaintModeSelector();
		getStartAnimation();
		getAnimationTimerDelayScale();
		getNotification();

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
		MenuItem selectAll = new MenuItem(popup, SWT.NONE);
		selectAll.setText("Select All");
		// paste.setImage(pasteId.createImage());
		selectAll.addListener(SWT.Selection, e -> {
			getSelector().selectAll();
		});
		MenuItem clear = new MenuItem(popup, SWT.NONE);
		clear.setText("Purge");
		clear.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Purge);
			getSelector().executeService(ImagingServiceDescription.Purge);
		});
		MenuItem separator1 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem flipHorizontal = new MenuItem(popup, SWT.NONE);
		flipHorizontal.setText("Flip Horizontal");
		flipHorizontal.setImage(flipHorizontalId.createImage());
		flipHorizontal.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Flip,ImagingServiceAction.Horizontal);
			getSelector().executeService(ImagingServiceDescription.Flip, FlipService.HORIZONTAL);
		});

		MenuItem flipVertical = new MenuItem(popup, SWT.NONE);
		flipVertical.setText("Flip Vertical");
		flipVertical.setImage(flipVerticalId.createImage());
		flipVertical.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Flip,ImagingServiceAction.Vertical);
			getSelector().executeService(ImagingServiceDescription.Flip, FlipService.VERTICAL);
		});
		MenuItem separator2 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem mirrorUpperHalf = new MenuItem(popup, SWT.NONE);
		mirrorUpperHalf.setText("Mirror Upper Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorUpperHalf.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Mirror,
			// ImagingServiceAction.UpperHalf);
			getSelector().executeService(ImagingServiceDescription.Mirror, MirrorService.UPPER_HALF);
		});

		MenuItem mirrorLowerHalf = new MenuItem(popup, SWT.NONE);
		mirrorLowerHalf.setText("Mirror Lower Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorLowerHalf.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Mirror,
			// ImagingServiceAction.LowerHalf);
			getSelector().executeService(ImagingServiceDescription.Mirror, MirrorService.LOWER_HALF);
		});

		MenuItem mirrorLeftHalf = new MenuItem(popup, SWT.NONE);
		mirrorLeftHalf.setText("Mirror Left Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorLeftHalf.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Mirror,
			// ImagingServiceAction.LeftHalf);
			getSelector().executeService(ImagingServiceDescription.Mirror, MirrorService.LEFT_HALF);
		});
		MenuItem mirrorRightHalf = new MenuItem(popup, SWT.NONE);
		mirrorRightHalf.setText("Mirror Right Half");
		// mirrorUpperHalf.setImage(flipHorizontalId.createImage());
		mirrorRightHalf.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Mirror,
			// ImagingServiceAction.RightHalf);
			getSelector().executeService(ImagingServiceDescription.Mirror, MirrorService.RIGHT_HALF);
		});
		MenuItem separator3 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem rotateCW = new MenuItem(popup, SWT.NONE);
		rotateCW.setText("Rotate CW");
		rotateCW.setImage(rotateCWId.createImage());
		rotateCW.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Rotate,
			// ImagingServiceAction.CW);
			getSelector().executeService(ImagingServiceDescription.Rotate, RotationService.CW);
		});

		MenuItem rotateCCW = new MenuItem(popup, SWT.NONE);
		rotateCCW.setText("Rotate CCW");
		rotateCCW.setImage(rotateCCWId.createImage());
		rotateCCW.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Rotate,
			// ImagingServiceAction.CCW);
			getSelector().executeService(ImagingServiceDescription.Rotate, RotationService.CCW);
		});

		MenuItem separator4 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem shiftUp = new MenuItem(popup, SWT.NONE);
		shiftUp.setText("Shift Up");
		shiftUp.setImage(upId.createImage());
		shiftUp.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Shift,
			// ImagingServiceAction.Up);
			getSelector().executeService(ImagingServiceDescription.Shift, ShiftService.UP);
		});

		MenuItem shiftDown = new MenuItem(popup, SWT.NONE);
		shiftDown.setText("Shift Down");
		shiftDown.setImage(downId.createImage());
		shiftDown.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Shift,
			// ImagingServiceAction.Down);
			getSelector().executeService(ImagingServiceDescription.Shift, ShiftService.DOWN);
		});

		MenuItem shiftLeft = new MenuItem(popup, SWT.NONE);
		shiftLeft.setText("Shift Left");
		shiftLeft.setImage(leftId.createImage());
		shiftLeft.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Shift,
			// ImagingServiceAction.Left);
			getSelector().executeService(ImagingServiceDescription.Shift, ShiftService.LEFT);
		});

		MenuItem shiftRight = new MenuItem(popup, SWT.NONE);
		shiftRight.setText("Shift Right");
		shiftRight.setImage(rightId.createImage());
		shiftRight.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Shift,
			// ImagingServiceAction.Right);
			getSelector().executeService(ImagingServiceDescription.Shift, ShiftService.RIGHT);
		});

		MenuItem separator5 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem swapTiles = new MenuItem(popup, SWT.NONE);
		swapTiles.setText("Swap Selected Tiles");
		swapTiles.setImage(swapId.createImage());
		swapTiles.addListener(SWT.Selection, e -> {
			// getSelector().action((e.stateMask & SWT.SHIFT) == SWT.SHIFT,
			// ImagingServiceDescription.Swap);
			getSelector().executeService(ImagingServiceDescription.Swap);
		});

		setPaintFormat("Char");
		setPaintMode("Pixel");
		getFormatSelector().select(0);
		getPaintModeSelector().select(0);
		getSelector().setMenu(popup);

		configurationDialog = new ConfigurationDialog(parent.getShell());
		configurationDialog.addConfigurationListener(this);

	}

	private Scale getAnimationTimerDelayScale() {
		if (animationTimerDelayScale == null) {
			animationTimerDelayScale = new Scale(controls, SWT.HORIZONTAL);
			animationTimerDelayScale.setEnabled(false);
			animationTimerDelayScale.setMinimum(50);
			animationTimerDelayScale.setMaximum(1000);
			animationTimerDelayScale.setSelection(200);
			animationTimerDelayScale.setIncrement(50);
			animationTimerDelayScale.setIncrement(50);

			animationTimerDelayScale.setPageIncrement(50);
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			animationTimerDelayScale.setLayoutData(gridData);
			animationTimerDelayScale.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int step = (getAnimationTimerDelayScale().getSelection()
							/ getAnimationTimerDelayScale().getIncrement())
							* getAnimationTimerDelayScale().getIncrement();
					getAnimationTimerDelayScale().setSelection(step);
					getSelector().changeAnimationTimerDelay(step);
				}
			});
		}
		return animationTimerDelayScale;
	}

	private ImagingWidget getPainter() {
		if (painter == null) {
			painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			painter.getConf().setWidgetName("Painter :");
			painter.getConf().setWidgetMode(WidgetMode.Painter);
			painter.getConf().setWidth(8);
			painter.getConf().setHeight(8);
			painter.getConf().setPixelGridEnabled(true);
			painter.getConf().setGridStyle(GridStyle.Dot);
			painter.getConf().setTileGridEnabled(true);
			painter.getConf().setTileCursorEnabled(false);
			painter.getConf().setMultiColorEnabled(multiColorMode);
			painter.setSelectedTileOffset(0);
			painter.setBitlane(getBinaryData());
			painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			painter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			painter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			painter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			painter.setSelectedColor(1);
			painter.addDrawListener(getSelector());
			painter.addDrawListener(getPreviewer());
		}
		return painter;
	}

	private ImagingWidget getPreviewer() {
		if (previewer == null) {
			previewer = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			previewer.getConf().setWidgetName("Preview :");
			previewer.getConf().setWidgetMode(WidgetMode.Viewer);
			previewer.getConf().setWidth(8);
			previewer.getConf().setHeight(8);
			previewer.getConf().setPixelSize(3);
			previewer.getConf().setPixelGridEnabled(false);
			previewer.getConf().setGridStyle(GridStyle.Dot);
			previewer.getConf().setTileGridEnabled(false);
			previewer.getConf().setTileCursorEnabled(false);
			previewer.getConf().setMultiColorEnabled(multiColorMode);
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
				protected void setHasTileSelection(int count) {
					getStartAnimation().setEnabled(count > 1);
					getAnimationTimerDelayScale().setEnabled(count > 1);
				}

				@Override
				protected void showNotification(ImagingServiceDescription type, ImagingServiceAction mode,
						String notification, Object data) {
					if (type == ImagingServiceDescription.Animation) {
						getStartAnimation().setText(notification);
					} else {
						MessageDialog.openInformation(parent.getShell(), "Information", notification);
					}
				}

				@Override
				protected boolean isConfirmed(ImagingServiceDescription type, ImagingServiceAction mode,
						int tileCount) {
					boolean confirmation = false;
					if (type == ImagingServiceDescription.Rotate) {
						confirmation = MessageDialog.openQuestion(parent.getShell(), "Question",
								"Rotating these tile(s) causes data loss, because it is/they are not squarish.\n\nDo you want to rotate anyway?");
					}
					if (type == ImagingServiceDescription.All) {
						confirmation = MessageDialog.openQuestion(parent.getShell(), "Question",
								MessageFormat.format("Do you really want to process {0} ?",
										(tileCount > 1) ? "all selected tiles" : "this tile"));
					}
					return confirmation;
				}

				@Override
				protected void setNotification(int offset, int tileSize) {

					getNotification().setText(MessageFormat.format("Offset: ${0} tile:{1} bytes",
							String.format("%04X", offset), tileSize));
				}
			};
			selector.getConf().setWidgetName("Selector:");
			selector.getConf().setWidgetMode(WidgetMode.Selector);
			selector.getConf().setWidth(8);
			selector.getConf().setHeight(8);
			selector.getConf().setPixelSize(3);
			selector.getConf().setPixelGridEnabled(false);
			selector.getConf().setTileGridEnabled(true);
			selector.getConf().setTileSubGridEnabled(false);
			selector.getConf().setTileCursorEnabled(true);
			selector.getConf().setSeparatorEnabled(false);
			selector.getConf().setMultiColorEnabled(multiColorMode);
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
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			multicolor.setLayoutData(gridData);
			multicolor.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					painter.getConf().setMultiColorEnabled(multicolor.getSelection());
					painter.recalc();
					selector.getConf().setMultiColorEnabled(multicolor.getSelection());
					selector.recalc();
					previewer.getConf().setMultiColorEnabled(multicolor.getSelection());
					previewer.recalc();
				}
			});
		}
		return multicolor;
	}

	private Button getStartAnimation() {
		if (startAnimation == null) {
			startAnimation = new Button(controls, SWT.PUSH);
			startAnimation.setEnabled(false);
			startAnimation.setSelection(false);
			startAnimation.setText("Start Animation");
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			startAnimation.setLayoutData(gridData);
			startAnimation.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {

					if (!getSelector().isAnimationRunning() && getSelector().isAnimatable()) {
						getSelector().setMouseActionEnabled(false);
						getPainter().setMouseActionEnabled(false);
						getPreviewer().setMouseActionEnabled(false);
						getSelector().startAnimation();
					} else {
						getSelector().stopAnimation();
						getSelector().setMouseActionEnabled(true);
						getPainter().setMouseActionEnabled(true);
						getPreviewer().setMouseActionEnabled(true);
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
					"Sprite 2Y", "Sprite 2XY", "Custom ..." });
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			formatSelector.setLayoutData(gridData);
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
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			paintModeSelector.setLayoutData(gridData);
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
			getPainter().getConf().setPaintMode(PaintMode.Simple);
			break;
		}
		case "VerticalMirror": {
			getPainter().getConf().setPaintMode(PaintMode.VerticalMirror);
			break;
		}
		case "HorizontalMirror": {
			getPainter().getConf().setPaintMode(PaintMode.HorizontalMirror);
			break;
		}
		case "Kaleidoscope": {
			getPainter().getConf().setPaintMode(PaintMode.Kaleidoscope);
			break;
		}
		}
	}

	private void setPaintFormat(String format) {
		switch (format) {
		case "Char": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(40);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(1);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(16);
			getSelector().getConf().setPixelSize(3);
			getSelector().recalc();
			parent.layout();
			break;
		}
		case "Char 2X": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(20);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(1);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(16);
			getSelector().getConf().setPixelSize(3);
			getSelector().recalc();

			parent.layout();
			break;
		}

		case "Char 2Y": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(20);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(2);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(8);
			getSelector().getConf().setPixelSize(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Char 2XY": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(20);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(2);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(8);
			getSelector().getConf().setPixelSize(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(10);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(1);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(6);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite 2X": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(10);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(1);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(6);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite 2Y": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(10);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(2);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(3);
			getSelector().recalc();
			parent.layout();
			break;
		}

		case "Sprite 2XY": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(10);
			getPainter().recalc();
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(2);
			getPreviewer().recalc();
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(3);
			getSelector().recalc();
			parent.layout();
			break;
		}
		case "Custom ...": {

			configurationDialog.setConfiguration(getPainter().getConf().getWidth(), getPainter().getConf().getHeight(),
					getPainter().getConf().getTileColumns(), getPainter().getConf().getTileRows(),
					getPainter().getConf().getPixelSize(), getSelector().getConf().getPixelSize());
			configurationDialog.open();

			break;
		}
		}

	}

	@Override
	public void configurationChanged(int width, int height, int tileColumns, int tileRows, int painterPixelSize,
			int selectorPixelSize) {
		getPainter().getConf().setWidth(width);
		getPainter().getConf().setHeight(height);
		getPainter().getConf().setTileColumns(tileColumns);
		getPainter().getConf().setTileRows(tileRows);
		getPainter().getConf().setPixelSize(painterPixelSize);
		getPainter().recalc();
		getPreviewer().getConf().setWidth(width);
		getPreviewer().getConf().setHeight(height);
		getPreviewer().getConf().setTileColumns(tileColumns);
		getPreviewer().getConf().setTileRows(tileRows);
		getPreviewer().recalc();
		getSelector().getConf().setWidth(width);
		getSelector().getConf().setHeight(height);
		getSelector().getConf().setTileColumns(tileColumns);
		getSelector().getConf().setTileRows(tileRows);
		getSelector().getConf().setPixelSize(selectorPixelSize);
		getSelector().getConf().setColumns(8);
		getSelector().getConf().setRows(3);
		getSelector().recalc();
		parent.layout();
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
