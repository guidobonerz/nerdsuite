package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
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
import de.drazil.nerdsuite.imaging.service.AnimationService;
import de.drazil.nerdsuite.imaging.service.ClipboardService;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.InvertService;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.imaging.service.SwapService;
import de.drazil.nerdsuite.widget.ConfigurationDialog;
import de.drazil.nerdsuite.widget.IConfigurationListener;
import de.drazil.nerdsuite.widget.ImagePainter;
import de.drazil.nerdsuite.widget.ImagePainterFactory;
import de.drazil.nerdsuite.widget.ImageReferenceSelector;
import de.drazil.nerdsuite.widget.ImageRepository;
import de.drazil.nerdsuite.widget.ImageViewer;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.PaintMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.PixelConfig;
import net.miginfocom.swt.MigLayout;

public class GfxEditorView //implements IConfigurationListener {
{
	private ImagePainter painter;
	private ImageViewer previewer;
	private ImageRepository selector;
	private ImageReferenceSelector referenceSelector;
	private Scale animationTimerDelayScale;
	private Composite parent;
	private Button multicolor;
	private Button startAnimation;
	private Text notification;
	private Composite controls;
	private Combo pixelConfigSelector;
	private Combo formatSelector;
	private Combo paintModeSelector;
	private byte binaryData[] = null;
	private byte blankData[] = null;
	boolean multiColorMode = false;
	private ConfigurationDialog configurationDialog = null;
	private boolean isAnimationRunning = false;
	private ImagePainterFactory imagePainterFactory = null;
	
	@Inject
	public GfxEditorView() {

	}
	/*
	 * @Execute public void execute(MWindow window, EModelService modelService) {
	 * 
	 * MUIElement findElement = modelService.find("menu:com.test.filesubmenu",
	 * window.getMainMenu());
	 * 
	 * MUIElement doSomethingElement =
	 * modelService.find("com.test.handledmenuitem.dosomething",
	 * window.getMainMenu());
	 * 
	 * logger.debug("Found submenu " + findElement); logger.debug(
	 * "Found do something element " + doSomethingElement); }
	 */
	// @Inject
	// EMenuService menuService;

	@PostConstruct
	public void postConstruct(Composite parent, MPart part, EMenuService menuService) {
		this.parent = parent;
		getPainter();
/*
		parent.setLayout(new MigLayout());

		controls = new Composite(parent, SWT.BORDER);
		controls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		controls.setLayout(layout);
		controls.setLayoutData("cell 1 0");
		imagePainterFactory = new ImagePainterFactory();
		getPixelConfigSelector();
		getFormatSelector();
		getPaintModeSelector();
		getStartAnimation();
		getAnimationTimerDelayScale();
		getNotification();
		getReferenceSelector();

		getPainter().setLayoutData("cell 0 0");
		getPreviewer().setLayoutData("cell 1 0");
		getSelector().setLayoutData("cell 0 1 2 1");

		menuService.registerContextMenu(parent, "de.drazil.nerdsuite.popupmenu.iconeditor");

		ImageDescriptor undoId = null;
		ImageDescriptor redoId = null;
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
		ImageDescriptor invertId = null;

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
			invertId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/contrast.png"));
			undoId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_undo.png"));
			redoId = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_redo.png"));

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Menu popup = new Menu(getSelector());
		MenuItem lastAction = new MenuItem(popup, SWT.NONE);
		lastAction.setText("Repeat Last Action\tCtrl+L");
		lastAction.setEnabled(false);
		lastAction.addListener(SWT.Selection, e -> {
		});
		MenuItem separator10 = new MenuItem(popup, SWT.SEPARATOR);
		MenuItem cut = new MenuItem(popup, SWT.NONE);
		cut.setText("Cut\tCtrl+X");
		cut.setImage(cutId.createImage());
		cut.addListener(SWT.Selection, e -> {
			ClipboardService service = getSelector().getService(ClipboardService.class);
			service.execute(ClipboardService.CUT);
		});
		MenuItem copy = new MenuItem(popup, SWT.NONE);
		copy.setText("Copy\tCtrl+C");
		copy.setImage(copyId.createImage());
		copy.addListener(SWT.Selection, e -> {
			ClipboardService service = getSelector().getService(ClipboardService.class);
			service.execute(ClipboardService.COPY);
		});
		MenuItem paste = new MenuItem(popup, SWT.NONE);
		paste.setText("Paste\tCtrl+V");
		paste.setImage(pasteId.createImage());
		paste.addListener(SWT.Selection, e -> {
			ClipboardService service = getSelector().getService(ClipboardService.class);
			service.execute(ClipboardService.PASTE);

		});
		MenuItem selectAll = new MenuItem(popup, SWT.NONE);
		selectAll.setText("Select All");
		selectAll.addListener(SWT.Selection, e -> {
			getSelector().selectAll();
		});

		MenuItem clear = new MenuItem(popup, SWT.NONE);
		clear.setText("Purge");
		clear.addListener(SWT.Selection, e -> {
			PurgeService service = getSelector().getService(PurgeService.class);
			service.execute();
		});
		MenuItem separator1 = new MenuItem(popup, SWT.SEPARATOR);
		MenuItem undo = new MenuItem(popup, SWT.NONE);
		undo.setImage(undoId.createImage());
		undo.setText("Undo");
		undo.setEnabled(false);

		MenuItem redo = new MenuItem(popup, SWT.NONE);
		redo.setImage(redoId.createImage());
		redo.setText("Redo");
		redo.setEnabled(false);

		MenuItem separator1b = new MenuItem(popup, SWT.SEPARATOR);
		MenuItem flipHorizontal = new MenuItem(popup, SWT.NONE);
		flipHorizontal.setText("Flip Horizontal\tShift+Up");
		flipHorizontal.setImage(flipHorizontalId.createImage());
		flipHorizontal.addListener(SWT.Selection, e -> {
			FlipService service = getSelector().getService(FlipService.class);
			service.execute(FlipService.HORIZONTAL);
		});

		MenuItem flipVertical = new MenuItem(popup, SWT.NONE);
		flipVertical.setText("Flip Vertical\tShift+Right");
		flipVertical.setImage(flipVerticalId.createImage());
		flipVertical.addListener(SWT.Selection, e -> {
			FlipService service = getSelector().getService(FlipService.class);
			service.execute(FlipService.VERTICAL);
		});
		MenuItem separator2 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem mirrorUpperHalf = new MenuItem(popup, SWT.NONE);
		mirrorUpperHalf.setText("Mirror Upper Half");
		mirrorUpperHalf.addListener(SWT.Selection, e -> {
			MirrorService service = getSelector().getService(MirrorService.class);
			service.execute(MirrorService.UPPER_HALF);
		});

		MenuItem mirrorLowerHalf = new MenuItem(popup, SWT.NONE);
		mirrorLowerHalf.setText("Mirror Lower Half");
		mirrorLowerHalf.addListener(SWT.Selection, e -> {
			MirrorService service = getSelector().getService(MirrorService.class);
			service.execute(MirrorService.LOWER_HALF);
		});

		MenuItem mirrorLeftHalf = new MenuItem(popup, SWT.NONE);
		mirrorLeftHalf.setText("Mirror Left Half");
		mirrorLeftHalf.addListener(SWT.Selection, e -> {
			MirrorService service = getSelector().getService(MirrorService.class);
			service.execute(MirrorService.LEFT_HALF);
		});
		MenuItem mirrorRightHalf = new MenuItem(popup, SWT.NONE);
		mirrorRightHalf.setText("Mirror Right Half");
		mirrorRightHalf.addListener(SWT.Selection, e -> {
			MirrorService service = getSelector().getService(MirrorService.class);
			service.execute(MirrorService.RIGHT_HALF);
		});
		MenuItem separator3 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem rotateCW = new MenuItem(popup, SWT.NONE);
		rotateCW.setText("Rotate CW\tShift+Right");
		rotateCW.setImage(rotateCWId.createImage());
		rotateCW.addListener(SWT.Selection, e -> {
			RotationService service = getSelector().getService(RotationService.class);
			service.execute(RotationService.CW);
		});

		MenuItem rotateCCW = new MenuItem(popup, SWT.NONE);
		rotateCCW.setText("Rotate CCW\tShift+Left");
		rotateCCW.setImage(rotateCCWId.createImage());
		rotateCCW.addListener(SWT.Selection, e -> {
			RotationService service = getSelector().getService(RotationService.class);
			service.execute(RotationService.CCW);
		});

		MenuItem separator4 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem shiftUp = new MenuItem(popup, SWT.NONE);
		shiftUp.setAccelerator(SWT.MOD1 + 'U');
		shiftUp.setText("Shift Up\tCtrl+Up");
		shiftUp.setImage(upId.createImage());
		shiftUp.addListener(SWT.Selection, e -> {
			ShiftService service = getSelector().getService(ShiftService.class);
			service.execute(ShiftService.UP);
		});

		MenuItem shiftDown = new MenuItem(popup, SWT.NONE);
		shiftDown.setText("Shift Down\tCtrl+Down");
		shiftDown.setImage(downId.createImage());
		shiftDown.addListener(SWT.Selection, e -> {
			ShiftService service = getSelector().getService(ShiftService.class);
			service.execute(ShiftService.DOWN);
		});

		MenuItem shiftLeft = new MenuItem(popup, SWT.NONE);
		shiftLeft.setText("Shift Left\tCtrl+Left");
		shiftLeft.setImage(leftId.createImage());
		shiftLeft.addListener(SWT.Selection, e -> {
			ShiftService service = getSelector().getService(ShiftService.class);
			service.execute(ShiftService.LEFT);
		});

		MenuItem shiftRight = new MenuItem(popup, SWT.NONE);
		shiftRight.setText("Shift Right\tCtrl+Right");
		shiftRight.setImage(rightId.createImage());
		shiftRight.addListener(SWT.Selection, e -> {
			ShiftService service = getSelector().getService(ShiftService.class);
			service.execute(ShiftService.RIGHT);
		});

		MenuItem separator5 = new MenuItem(popup, SWT.SEPARATOR);

		MenuItem swapTiles = new MenuItem(popup, SWT.NONE);
		swapTiles.setText("Swap\tShift+Tab");
		swapTiles.setImage(swapId.createImage());
		swapTiles.addListener(SWT.Selection, e -> {
			SwapService service = getSelector().getService(SwapService.class);
			service.execute();
		});

		MenuItem invertTiles = new MenuItem(popup, SWT.NONE);
		invertTiles.setText("&Invert\tShift+I");
		invertTiles.setAccelerator(SWT.SHIFT + 'I');
		invertTiles.setImage(invertId.createImage());
		invertTiles.addListener(SWT.Selection, e -> {
			InvertService service = getSelector().getService(InvertService.class);
			service.execute();
		});

		getSelector().setMenu(popup);
		setPixelConfig("BC8");
		setPaintFormat("Screen");
		setPaintMode("Pixel");
		getPixelConfigSelector().select(2);
		getFormatSelector().select(0);
		getPaintModeSelector().select(0);
		configurationDialog = new ConfigurationDialog(parent.getShell());
		configurationDialog.addConfigurationListener(this);
*/
	}
/*
	private Scale getAnimationTimerDelayScale() {
		if (animationTimerDelayScale == null) {
			animationTimerDelayScale = new Scale(controls, SWT.HORIZONTAL);
			animationTimerDelayScale.setEnabled(true);
			animationTimerDelayScale.setMinimum(50);
			animationTimerDelayScale.setMaximum(500);
			animationTimerDelayScale.setSelection(200);
			getSelector().getService(AnimationService.class).setDelay(200);
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
					getSelector().getService(AnimationService.class).setDelay(step);
				}
			});
		}
		return animationTimerDelayScale;
	}
*/
	public ImagePainter getPainter() {
		if (painter == null) {
			painter = new ImagePainter(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
			painter.getConf().setWidgetName("Painter :");
			painter.getConf().setWidth(8);
			painter.getConf().setHeight(8);
			painter.getConf().setPixelGridEnabled(true);
			painter.getConf().setGridStyle(GridStyle.Dot);
			painter.getConf().setTileGridEnabled(true);
			painter.getConf().setTileCursorEnabled(false);
			painter.setSelectedTileOffset(0, 0, false);
			painter.setBitplane(getBlankData());
			painter.setImagePainterFactory(imagePainterFactory);
			painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			painter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			painter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			painter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			painter.setSelectedColor(1);
			painter.recalc();
			// painter.addDrawListener(getSelector());
			// painter.addDrawListener(getPreviewer());
		}
		return painter;
	}

	/*
	 * private ImageViewer getPreviewer() { if (previewer == null) { previewer = new
	 * ImageViewer(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
	 * previewer.getConf().setWidgetName("Preview :");
	 * previewer.getConf().setWidth(8); previewer.getConf().setHeight(8);
	 * previewer.getConf().setPixelSize(3);
	 * previewer.getConf().setPixelGridEnabled(false);
	 * previewer.getConf().setGridStyle(GridStyle.Dot);
	 * previewer.getConf().setTileGridEnabled(false);
	 * previewer.getConf().setTileCursorEnabled(false);
	 * previewer.getConf().setSeparatorEnabled(false);
	 * previewer.setSelectedTileOffset(0, 0, false);
	 * previewer.setBitplane(getBlankData());
	 * previewer.setImagePainterFactory(imagePainterFactory); previewer.setColor(0,
	 * InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
	 * previewer.setColor(1,
	 * InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
	 * previewer.setColor(2,
	 * InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
	 * previewer.setColor(3,
	 * InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
	 * previewer.setSelectedColor(1);
	 * 
	 * } return previewer; }
	 */
/*
	private ImagingWidget getReferenceSelector() {
		if (referenceSelector == null) {
			referenceSelector = new ImageReferenceSelector(controls,
					SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
			referenceSelector.getConf().setWidgetName("ReferenceSelector:");
			referenceSelector.getConf().setWidth(8);
			referenceSelector.getConf().setHeight(8);
			referenceSelector.getConf().setTileColumns(1);
			referenceSelector.getConf().setTileRows(1);
			referenceSelector.getConf().setColumns(16);
			referenceSelector.getConf().setRows(16);
			referenceSelector.getConf().setPixelSize(2);
			referenceSelector.getConf().setPixelGridEnabled(false);
			referenceSelector.getConf().setTileGridEnabled(true);
			referenceSelector.getConf().setTileSubGridEnabled(false);
			referenceSelector.getConf().setTileCursorEnabled(true);
			referenceSelector.getConf().setSeparatorEnabled(false);
			referenceSelector.setSelectedTileOffset(0, 0, true);
			referenceSelector.setBitplane(getBinaryData());
			referenceSelector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			referenceSelector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			referenceSelector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			referenceSelector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			referenceSelector.setSelectedColor(1);
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			referenceSelector.setLayoutData(gridData);
			referenceSelector.recalc();
			referenceSelector.addDrawListener(getPainter());

		}
		return referenceSelector;

	}
*/
	private Text getNotification() {
		if (notification == null) {
			notification = new Text(controls, SWT.NONE);
			notification.setEnabled(false);
		}
		return notification;
	}
/*
	private Button getStartAnimation() {
		if (startAnimation == null) {
			startAnimation = new Button(controls, SWT.PUSH);
			startAnimation.setEnabled(true);
			startAnimation.setSelection(false);
			startAnimation.setText("Start Animation");
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			startAnimation.setLayoutData(gridData);
			startAnimation.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {

					if (!isAnimationRunning) {
						isAnimationRunning = true;
						getSelector().setMouseActionEnabled(false);
						getPainter().setMouseActionEnabled(false);
						getPreviewer().setMouseActionEnabled(false);
						getSelector().getService(AnimationService.class).execute(AnimationService.START);
					} else {
						getSelector().getService(AnimationService.class).execute(AnimationService.STOP);
						getSelector().setMouseActionEnabled(true);
						getPainter().setMouseActionEnabled(true);
						getPreviewer().setMouseActionEnabled(true);
						isAnimationRunning = false;
					}
				}
			});
		}
		return startAnimation;
	}
*/
	/*
	private Combo getPixelConfigSelector() {
		if (pixelConfigSelector == null) {
			pixelConfigSelector = new Combo(controls, SWT.DROP_DOWN);
			pixelConfigSelector.setItems(new String[] { "BC1", "BC2", "BC8" });
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			pixelConfigSelector.setLayoutData(gridData);
			pixelConfigSelector.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Combo c = ((Combo) e.getSource());
					int index = c.getSelectionIndex();
					setPixelConfig(c.getItem(index));
				}
			});
		}
		return pixelConfigSelector;
	}
	*/
/*
	private Combo getFormatSelector() {
		if (formatSelector == null) {
			formatSelector = new Combo(controls, SWT.DROP_DOWN);
			formatSelector.setItems(new String[] { "Screen", "Char", "Char 2X", "Char 2Y", "Char 2XY", "Sprite",
					"Sprite 2X", "Sprite 2Y", "Sprite 2XY", "Screen", "Custom ..." });
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
*/
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
/*
	private void setPixelConfig(String pixelConfig) {
		switch (pixelConfig) {
		case "BC1": {
			getPainter().getConf().setPixelConfig(PixelConfig.BC1);
			getSelector().getConf().setPixelConfig(PixelConfig.BC1);
			getPreviewer().getConf().setPixelConfig(PixelConfig.BC1);
			break;
		}
		case "BC2": {
			getPainter().getConf().setPixelConfig(PixelConfig.BC2);
			getSelector().getConf().setPixelConfig(PixelConfig.BC2);
			getPreviewer().getConf().setPixelConfig(PixelConfig.BC2);
			break;
		}
		case "BC8": {
			getPainter().getConf().setPixelConfig(PixelConfig.BC8);
			getSelector().getConf().setPixelConfig(PixelConfig.BC8);
			getPreviewer().getConf().setPixelConfig(PixelConfig.BC8);
			break;
		}

		}
		getPreviewer().recalc();
		getSelector().recalc();
		getPainter().recalc();
		parent.layout();
	}
*/
	/*
	private void setPaintFormat(String format) {
		switch (format) {
		case "Screen": {
			getPainter().getConf().setWidth(40);
			getPainter().getConf().setHeight(25);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(16);
			getPreviewer().getConf().setWidth(40);
			getPreviewer().getConf().setHeight(25);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(1);
			getPreviewer().getConf().setPixelSize(4);
			getSelector().getConf().setWidth(40);
			getSelector().getConf().setHeight(25);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(1);
			getSelector().getConf().setPixelSize(8);
			break;
		}
		case "Char": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(40);
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(1);
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(16);
			getSelector().getConf().setPixelSize(3);
			break;
		}
		case "Char 2X": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(20);
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(1);
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(16);
			getSelector().getConf().setPixelSize(3);
			break;
		}

		case "Char 2Y": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(20);
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(2);
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(8);
			getSelector().getConf().setPixelSize(3);
			break;
		}

		case "Char 2XY": {
			getPainter().getConf().setWidth(8);
			getPainter().getConf().setHeight(8);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(20);
			getPreviewer().getConf().setWidth(8);
			getPreviewer().getConf().setHeight(8);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(2);
			getSelector().getConf().setWidth(8);
			getSelector().getConf().setHeight(8);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(8);
			getSelector().getConf().setPixelSize(3);
			break;
		}

		case "Sprite": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(10);
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(1);
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(6);
			break;
		}

		case "Sprite 2X": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(1);
			getPainter().getConf().setPixelSize(10);
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(1);
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(1);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(6);
			break;
		}

		case "Sprite 2Y": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(1);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(10);
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(1);
			getPreviewer().getConf().setTileRows(2);
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(1);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(16);
			getSelector().getConf().setRows(3);
			break;
		}

		case "Sprite 2XY": {
			getPainter().getConf().setWidth(24);
			getPainter().getConf().setHeight(21);
			getPainter().getConf().setTileColumns(2);
			getPainter().getConf().setTileRows(2);
			getPainter().getConf().setPixelSize(10);
			getPreviewer().getConf().setWidth(24);
			getPreviewer().getConf().setHeight(21);
			getPreviewer().getConf().setTileColumns(2);
			getPreviewer().getConf().setTileRows(2);
			getSelector().getConf().setWidth(24);
			getSelector().getConf().setHeight(21);
			getSelector().getConf().setTileColumns(2);
			getSelector().getConf().setTileRows(2);
			getSelector().getConf().setPixelSize(2);
			getSelector().getConf().setColumns(8);
			getSelector().getConf().setRows(3);
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
		getPainter().recalc();
		getPreviewer().recalc();
		getSelector().recalc();
		parent.layout();

	}
*/
	/*
	@Override
	public void configurationChanged(int width, int height, int tileColumns, int tileRows, int painterPixelSize,
			int selectorPixelSize, int columns, int rows, int currentWidth) {
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
		getSelector().recalc();
		parent.layout();
	}
*/
	private byte[] getBinaryData() {
		if (binaryData == null) {

			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL url = bundle.getEntry("fonts/c64_lower.64c");

			try {
				binaryData = BinaryFileReader.readFile(url.openConnection().getInputStream(), 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return binaryData;
	}

	private byte[] getBlankData() {
		if (blankData == null) {
			blankData = new byte[0x1f40];
			for (int i = 0; i < blankData.length; i++)
				blankData[i] = 32;// (byte) (Math.random() * 80);
		}
		return blankData;
	}

}
