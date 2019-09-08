package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.constants.GridStyle;
import de.drazil.nerdsuite.constants.PaintMode;
import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GridState;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.widget.ConfigurationDialog;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;
import net.miginfocom.swt.MigLayout;

public class GfxEditorView // implements IConfigurationListener {
{
	private ImagingWidget painter;
	private ImagingWidget previewer;
	private ImagingWidget repository;
	private Project project;

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
	private TileRepositoryService tileRepositoryService;

	Button tile1;
	Button tile2;

	private Button layer1;
	private Button layer2;
	private Button layer3;
	private Button layer4;

	private Button color1;
	private Button color2;
	private Button color3;
	private Button color4;

	private Button showOnlyActiveLayer;
	private Button showInactiveLayersTranslucent;

	@Inject
	EMenuService menuService;

	public GfxEditorView() {

	}

	@Inject
	@Optional
	void controlPaintMode(@UIEventTopic("PaintMode") PaintMode paintMode, EModelService service, MPart part) {
		MToolItem single = (MToolItem) service.find("de.drazil.nerdsuite.handledtoolitem.singlePaintMode",
				part.getToolbar());
		MToolItem vertical = (MToolItem) service.find("de.drazil.nerdsuite.handledtoolitem.verticalMirrorPaintMode",
				part.getToolbar());
		MToolItem horizontal = (MToolItem) service.find("de.drazil.nerdsuite.handledtoolitem.horizontalMirrorPaintMode",
				part.getToolbar());
		MToolItem kaleidoscope = (MToolItem) service.find("de.drazil.nerdsuite.handledtoolitem.kaleidoscopePaintMode",
				part.getToolbar());
		single.setSelected(false);
		vertical.setSelected(false);
		horizontal.setSelected(false);
		kaleidoscope.setSelected(false);

		if (paintMode == PaintMode.Single) {
			single.setSelected(true);
		} else if (paintMode == PaintMode.VerticalMirror) {
			vertical.setSelected(true);
		} else if (paintMode == PaintMode.HorizontalMirror) {
			horizontal.setSelected(true);
		} else if (paintMode == PaintMode.Kaleidoscope) {
			kaleidoscope.setSelected(true);
		} else {
		}

		getPainterWidget().getConf().setPaintMode(paintMode);
	}

	@Inject
	@Optional
	void controlLayer(@UIEventTopic("addOrRemoveLayer") boolean addOrRemove) {
		Tile tile = tileRepositoryService.getSelectedTile();
		if (addOrRemove) {
			tile.addLayer();
		} else {
			tile.removeLastLayer();
		}
	}

	@Inject
	@Optional
	void controlTile(@UIEventTopic("addOrRemoveTile") int index) {
		System.out.println("addOrRemoveTile:" + index);
		// ServiceFactory.getService(serviceOwnerId,
		// TileRepositoryService.class).addTile("", size);;

	}

	@Inject
	@Optional
	void controlGridState(@UIEventTopic("GridState") GridState state, EModelService service, MPart part) {
		MToolItem itemGrid = (MToolItem) service.find("de.drazil.nerdsuite.handledtoolitem.showLineGrid",
				part.getToolbar());
		MToolItem itemDotGrid = (MToolItem) service.find("de.drazil.nerdsuite.handledtoolitem.showDotGrid",
				part.getToolbar());
		if (state.gridStyle == GridStyle.Dot && state.isEnabled()) {
			itemGrid.setSelected(!state.isEnabled());
		}
		if (state.gridStyle == GridStyle.Line && state.isEnabled()) {
			itemDotGrid.setSelected(!state.isEnabled());
		}
		getPainterWidget().getConf().setGridStyle(state.gridStyle);
		getPainterWidget().getConf().setPixelGridEnabled(state.isEnabled());
		getPainterWidget().recalc();

	}

	@Optional
	@Inject
	void startNewProject(@UIEventTopic("projectSetup") Map<String, Object> projectSetup) {
		getPainterWidget().getConf().setGraphicFormat((GraphicFormat) projectSetup.get("gfxFormat"), 0);
		// (int) projectSetup.get("gfxFormatVariant")
		int startIndex = (int) projectSetup.get("setSelectedTile");

		int contentSize = getPainterWidget().getConf().getWidth() * getPainterWidget().getConf().getHeight();

		tileRepositoryService.addTile("tile1", contentSize);
		Layer layer1 = null;

		layer1 = tileRepositoryService.getTile(0).getActiveLayer();
		layer1.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer1.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		layer1.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		layer1.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		layer1.setSelectedColorIndex(0);
		Layer layer2 = tileRepositoryService.getTile(0).addLayer("layer2");
		layer2.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer2.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(4).getColor());
		layer2.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(5).getColor());
		layer2.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(6).getColor());
		layer2.setSelectedColorIndex(0);
		Layer layer3 = tileRepositoryService.getTile(0).addLayer("layer3");
		layer3.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer3.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(7).getColor());
		layer3.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(8).getColor());
		layer3.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(9).getColor());
		layer3.setSelectedColorIndex(0);
		Layer layer4 = tileRepositoryService.getTile(0).addLayer("layer4");
		layer4.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer4.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(10).getColor());
		layer4.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(11).getColor());
		layer4.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(12).getColor());
		layer4.setSelectedColorIndex(0);

		tileRepositoryService.addTile("tile2", contentSize);
		Layer layer1b = null;

		layer1b = tileRepositoryService.getTile(1).getActiveLayer();
		layer1b.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer1b.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		layer1b.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		layer1b.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		layer1b.setSelectedColorIndex(0);
		Layer layer2b = tileRepositoryService.getTile(1).addLayer("layer2");
		layer2b.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer2b.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		layer2b.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(4).getColor());
		layer2b.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(5).getColor());
		layer2b.setSelectedColorIndex(0);
		Layer layer3b = tileRepositoryService.getTile(1).addLayer("layer3");
		layer3b.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer3b.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(6).getColor());
		layer3b.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(7).getColor());
		layer3b.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(8).getColor());
		layer3b.setSelectedColorIndex(0);
		Layer layer4b = tileRepositoryService.getTile(1).addLayer("layer4");
		layer4b.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer4b.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(9).getColor());
		layer4b.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(10).getColor());
		layer4b.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(11).getColor());
		layer4b.setSelectedColorIndex(0);

		// getPreviewerWidget().getConf().setGraphicFormat(gf);
		// getPreviewerWidget().recalc();
		// getRepositoryWidget().getConf().setGraphicFormat(gf);
		// getRepositoryWidget().recalc();
		tileRepositoryService.addTileSelectionListener(getPainterWidget());
		tileRepositoryService.setSelectedTile(startIndex);

		getPainterWidget().recalc();
	}

	@PostConstruct
	public void postConstruct(Composite parent, MPart part, EMenuService menuService) {
		this.parent = parent;
		tileRepositoryService = ServiceFactory.getService(getOwner(), TileRepositoryService.class);
		parent.setLayout(new MigLayout());
		getPainterWidget().setLayoutData("span 6 6");

		tile1 = new Button(parent, SWT.NONE);
		tile1.setText("tile1");
		tile1.addListener(SWT.Selection, e -> {
			tileRepositoryService.setSelectedTile(0);
		});

		tile2 = new Button(parent, SWT.NONE);
		tile2.setText("tile2");
		tile2.addListener(SWT.Selection, e -> {
			tileRepositoryService.setSelectedTile(1);
		});
		tile2.setLayoutData("wrap");

		layer1 = new Button(parent, SWT.NONE);
		layer1.setText("layer1");
		layer1.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setLayerActive(0, true);
			Layer l = tileRepositoryService.getSelectedTile().getActiveLayer();
			color1.setBackground(l.getColor(0));
			color2.setBackground(l.getColor(1));
			color3.setBackground(l.getColor(2));
			color4.setBackground(l.getColor(3));
		});
		layer2 = new Button(parent, SWT.NONE);
		layer2.setText("layer2");
		layer2.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setLayerActive(1, true);
			Layer l = tileRepositoryService.getSelectedTile().getActiveLayer();
			color1.setBackground(l.getColor(0));
			color2.setBackground(l.getColor(1));
			color3.setBackground(l.getColor(2));
			color4.setBackground(l.getColor(3));
		});
		layer3 = new Button(parent, SWT.NONE);
		layer3.setText("layer3");
		layer3.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setLayerActive(2, true);
			Layer l = tileRepositoryService.getSelectedTile().getActiveLayer();
			color1.setBackground(l.getColor(0));
			color2.setBackground(l.getColor(1));
			color3.setBackground(l.getColor(2));
			color4.setBackground(l.getColor(3));
		});
		layer4 = new Button(parent, SWT.NONE);
		layer4.setText("layer4");
		layer4.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setLayerActive(3, true);
			Layer l = tileRepositoryService.getSelectedTile().getActiveLayer();
			color1.setBackground(l.getColor(0));
			color2.setBackground(l.getColor(1));
			color3.setBackground(l.getColor(2));
			color4.setBackground(l.getColor(3));
		});
		layer4.setLayoutData("wrap");

		color1 = new Button(parent, SWT.NONE);
		color1.setText("color1");
		color1.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().getActiveLayer().setSelectedColorIndex(0);
		});
		color2 = new Button(parent, SWT.NONE);
		color2.setText("color2");
		color2.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().getActiveLayer().setSelectedColorIndex(1);
		});
		color3 = new Button(parent, SWT.NONE);
		color3.setText("color3");
		color3.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().getActiveLayer().setSelectedColorIndex(2);
		});
		color4 = new Button(parent, SWT.NONE);
		color4.setText("color4");
		color4.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().getActiveLayer().setSelectedColorIndex(3);
		});
		color4.setLayoutData("wrap");

		showOnlyActiveLayer = new Button(parent, SWT.CHECK);
		showOnlyActiveLayer.setText("Show active layer only");
		showOnlyActiveLayer.addListener(SWT.Selection, e -> {
			tileRepositoryService.getTile(0).setShowOnlyActiveLayer(((Button) e.widget).getSelection());
		});

		showOnlyActiveLayer.setLayoutData("span 4, wrap");

		showInactiveLayersTranslucent = new Button(parent, SWT.CHECK);
		showInactiveLayersTranslucent.setText("Show inactive layers translucent");
		showInactiveLayersTranslucent.addListener(SWT.Selection, e -> {
			tileRepositoryService.getTile(0).setShowInactiveLayerTranslucent(((Button) e.widget).getSelection());
		});
		showInactiveLayersTranslucent.setLayoutData("span 4, wrap");

		/*
		 * showOnlyActiveLayer.setLayoutData("cell 1 2");
		 * showInactiveLayersTranslucent.setLayoutData("cell 1 3");
		 * 
		 * layer1.setLayoutData("cell 1 1"); layer2.setLayoutData("cell 2 1");
		 * layer3.setLayoutData("cell 3 1"); layer4.setLayoutData("cell 4 1");
		 * 
		 * color1.setLayoutData("cell 1 2"); color2.setLayoutData("cell 2 2");
		 * color3.setLayoutData("cell 3 2"); color4.setLayoutData("cell 4 2");
		 * 
		 */

		// getPreviewerWidget().setLayoutData("cell 1 0");
		// getRepositoryWidget().setLayoutData("cell 0 1 2 1");

		/*
		 * parent.setLayout(new MigLayout());
		 * 
		 * controls = new Composite(parent, SWT.BORDER); controls.setLayoutData(new
		 * GridData(SWT.FILL, SWT.FILL, true, true)); GridLayout layout = new
		 * GridLayout(2, false); controls.setLayout(layout);
		 * controls.setLayoutData("cell 1 0"); imagePainterFactory = new
		 * ImagePainterFactory(); getPixelConfigSelector(); getFormatSelector();
		 * getPaintModeSelector(); getStartAnimation(); getAnimationTimerDelayScale();
		 * getNotification(); getReferenceSelector();
		 * 
		 * getPainter().setLayoutData("cell 0 0");
		 * getPreviewer().setLayoutData("cell 1 0");
		 * getSelector().setLayoutData("cell 0 1 2 1");
		 * 
		 * menuService.registerContextMenu(parent,
		 * "de.drazil.nerdsuite.popupmenu.iconeditor");
		 * 
		 * ImageDescriptor undoId = null; ImageDescriptor redoId = null; ImageDescriptor
		 * upId = null; ImageDescriptor downId = null; ImageDescriptor leftId = null;
		 * ImageDescriptor rightId = null; ImageDescriptor cutId = null; ImageDescriptor
		 * copyId = null; ImageDescriptor pasteId = null; ImageDescriptor rotateCWId =
		 * null; ImageDescriptor rotateCCWId = null; ImageDescriptor flipHorizontalId =
		 * null; ImageDescriptor flipVerticalId = null; ImageDescriptor swapId = null;
		 * ImageDescriptor invertId = null;
		 * 
		 * try { cutId = ImageDescriptor.createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/cut.png")); copyId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/page_white_copy.png"));
		 * pasteId = ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/paste_plain.png"));
		 * rotateCWId = ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_rotate_clockwise.png"))
		 * ; rotateCCWId = ImageDescriptor.createFromURL( new URL(
		 * "platform:/plugin/de.drazil.nerdsuite/icons/shape_rotate_anticlockwise.png"))
		 * ; flipHorizontalId = ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_flip_horizontal.png"));
		 * flipVerticalId = ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/shape_flip_vertical.png"));
		 * upId = ImageDescriptor.createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_up.png")); downId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_down.png")); leftId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_left.png")); rightId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_right.png")); swapId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_switch.png")); invertId
		 * = ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/contrast.png")); undoId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_undo.png")); redoId =
		 * ImageDescriptor .createFromURL(new
		 * URL("platform:/plugin/de.drazil.nerdsuite/icons/arrow_redo.png"));
		 * 
		 * } catch (MalformedURLException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 * 
		 * Menu popup = new Menu(getSelector()); MenuItem lastAction = new
		 * MenuItem(popup, SWT.NONE); lastAction.setText("Repeat Last Action\tCtrl+L");
		 * lastAction.setEnabled(false); lastAction.addListener(SWT.Selection, e -> {
		 * }); MenuItem separator10 = new MenuItem(popup, SWT.SEPARATOR); MenuItem cut =
		 * new MenuItem(popup, SWT.NONE); cut.setText("Cut\tCtrl+X");
		 * cut.setImage(cutId.createImage()); cut.addListener(SWT.Selection, e -> {
		 * ClipboardService service = getSelector().getService(ClipboardService.class);
		 * service.execute(ClipboardService.CUT); }); MenuItem copy = new
		 * MenuItem(popup, SWT.NONE); copy.setText("Copy\tCtrl+C");
		 * copy.setImage(copyId.createImage()); copy.addListener(SWT.Selection, e -> {
		 * ClipboardService service = getSelector().getService(ClipboardService.class);
		 * service.execute(ClipboardService.COPY); }); MenuItem paste = new
		 * MenuItem(popup, SWT.NONE); paste.setText("Paste\tCtrl+V");
		 * paste.setImage(pasteId.createImage()); paste.addListener(SWT.Selection, e ->
		 * { ClipboardService service =
		 * getSelector().getService(ClipboardService.class);
		 * service.execute(ClipboardService.PASTE);
		 * 
		 * }); MenuItem selectAll = new MenuItem(popup, SWT.NONE);
		 * selectAll.setText("Select All"); selectAll.addListener(SWT.Selection, e -> {
		 * getSelector().selectAll(); });
		 * 
		 * MenuItem clear = new MenuItem(popup, SWT.NONE); clear.setText("Purge");
		 * clear.addListener(SWT.Selection, e -> { PurgeService service =
		 * getSelector().getService(PurgeService.class); service.execute(); }); MenuItem
		 * separator1 = new MenuItem(popup, SWT.SEPARATOR); MenuItem undo = new
		 * MenuItem(popup, SWT.NONE); undo.setImage(undoId.createImage());
		 * undo.setText("Undo"); undo.setEnabled(false);
		 * 
		 * MenuItem redo = new MenuItem(popup, SWT.NONE);
		 * redo.setImage(redoId.createImage()); redo.setText("Redo");
		 * redo.setEnabled(false);
		 * 
		 * MenuItem separator1b = new MenuItem(popup, SWT.SEPARATOR); MenuItem
		 * flipHorizontal = new MenuItem(popup, SWT.NONE);
		 * flipHorizontal.setText("Flip Horizontal\tShift+Up");
		 * flipHorizontal.setImage(flipHorizontalId.createImage());
		 * flipHorizontal.addListener(SWT.Selection, e -> { FlipService service =
		 * getSelector().getService(FlipService.class);
		 * service.execute(FlipService.HORIZONTAL); });
		 * 
		 * MenuItem flipVertical = new MenuItem(popup, SWT.NONE);
		 * flipVertical.setText("Flip Vertical\tShift+Right");
		 * flipVertical.setImage(flipVerticalId.createImage());
		 * flipVertical.addListener(SWT.Selection, e -> { FlipService service =
		 * getSelector().getService(FlipService.class);
		 * service.execute(FlipService.VERTICAL); }); MenuItem separator2 = new
		 * MenuItem(popup, SWT.SEPARATOR);
		 * 
		 * MenuItem mirrorUpperHalf = new MenuItem(popup, SWT.NONE);
		 * mirrorUpperHalf.setText("Mirror Upper Half");
		 * mirrorUpperHalf.addListener(SWT.Selection, e -> { MirrorService service =
		 * getSelector().getService(MirrorService.class);
		 * service.execute(MirrorService.UPPER_HALF); });
		 * 
		 * MenuItem mirrorLowerHalf = new MenuItem(popup, SWT.NONE);
		 * mirrorLowerHalf.setText("Mirror Lower Half");
		 * mirrorLowerHalf.addListener(SWT.Selection, e -> { MirrorService service =
		 * getSelector().getService(MirrorService.class);
		 * service.execute(MirrorService.LOWER_HALF); });
		 * 
		 * MenuItem mirrorLeftHalf = new MenuItem(popup, SWT.NONE);
		 * mirrorLeftHalf.setText("Mirror Left Half");
		 * mirrorLeftHalf.addListener(SWT.Selection, e -> { MirrorService service =
		 * getSelector().getService(MirrorService.class);
		 * service.execute(MirrorService.LEFT_HALF); }); MenuItem mirrorRightHalf = new
		 * MenuItem(popup, SWT.NONE); mirrorRightHalf.setText("Mirror Right Half");
		 * mirrorRightHalf.addListener(SWT.Selection, e -> { MirrorService service =
		 * getSelector().getService(MirrorService.class);
		 * service.execute(MirrorService.RIGHT_HALF); }); MenuItem separator3 = new
		 * MenuItem(popup, SWT.SEPARATOR);
		 * 
		 * MenuItem rotateCW = new MenuItem(popup, SWT.NONE);
		 * rotateCW.setText("Rotate CW\tShift+Right");
		 * rotateCW.setImage(rotateCWId.createImage());
		 * rotateCW.addListener(SWT.Selection, e -> { RotationService service =
		 * getSelector().getService(RotationService.class);
		 * service.execute(RotationService.CW); });
		 * 
		 * MenuItem rotateCCW = new MenuItem(popup, SWT.NONE);
		 * rotateCCW.setText("Rotate CCW\tShift+Left");
		 * rotateCCW.setImage(rotateCCWId.createImage());
		 * rotateCCW.addListener(SWT.Selection, e -> { RotationService service =
		 * getSelector().getService(RotationService.class);
		 * service.execute(RotationService.CCW); });
		 * 
		 * MenuItem separator4 = new MenuItem(popup, SWT.SEPARATOR);
		 * 
		 * MenuItem shiftUp = new MenuItem(popup, SWT.NONE);
		 * shiftUp.setAccelerator(SWT.MOD1 + 'U'); shiftUp.setText("Shift Up\tCtrl+Up");
		 * shiftUp.setImage(upId.createImage()); shiftUp.addListener(SWT.Selection, e ->
		 * { ShiftService service = getSelector().getService(ShiftService.class);
		 * service.execute(ShiftService.UP); });
		 * 
		 * MenuItem shiftDown = new MenuItem(popup, SWT.NONE);
		 * shiftDown.setText("Shift Down\tCtrl+Down");
		 * shiftDown.setImage(downId.createImage());
		 * shiftDown.addListener(SWT.Selection, e -> { ShiftService service =
		 * getSelector().getService(ShiftService.class);
		 * service.execute(ShiftService.DOWN); });
		 * 
		 * MenuItem shiftLeft = new MenuItem(popup, SWT.NONE);
		 * shiftLeft.setText("Shift Left\tCtrl+Left");
		 * shiftLeft.setImage(leftId.createImage());
		 * shiftLeft.addListener(SWT.Selection, e -> { ShiftService service =
		 * getSelector().getService(ShiftService.class);
		 * service.execute(ShiftService.LEFT); });
		 * 
		 * MenuItem shiftRight = new MenuItem(popup, SWT.NONE);
		 * shiftRight.setText("Shift Right\tCtrl+Right");
		 * shiftRight.setImage(rightId.createImage());
		 * shiftRight.addListener(SWT.Selection, e -> { ShiftService service =
		 * getSelector().getService(ShiftService.class);
		 * service.execute(ShiftService.RIGHT); });
		 * 
		 * MenuItem separator5 = new MenuItem(popup, SWT.SEPARATOR);
		 * 
		 * MenuItem swapTiles = new MenuItem(popup, SWT.NONE);
		 * swapTiles.setText("Swap\tShift+Tab");
		 * swapTiles.setImage(swapId.createImage());
		 * swapTiles.addListener(SWT.Selection, e -> { SwapService service =
		 * getSelector().getService(SwapService.class); service.execute(); });
		 * 
		 * MenuItem invertTiles = new MenuItem(popup, SWT.NONE);
		 * invertTiles.setText("&Invert\tShift+I"); invertTiles.setAccelerator(SWT.SHIFT
		 * + 'I'); invertTiles.setImage(invertId.createImage());
		 * invertTiles.addListener(SWT.Selection, e -> { InvertService service =
		 * getSelector().getService(InvertService.class); service.execute(); });
		 * 
		 * getSelector().setMenu(popup); setPixelConfig("BC8");
		 * setPaintFormat("Screen"); setPaintMode("Pixel");
		 * getPixelConfigSelector().select(2); getFormatSelector().select(0);
		 * getPaintModeSelector().select(0); configurationDialog = new
		 * ConfigurationDialog(parent.getShell());
		 * configurationDialog.addConfigurationListener(this);
		 */
	}

	/*
	 * private Scale getAnimationTimerDelayScale() { if (animationTimerDelayScale ==
	 * null) { animationTimerDelayScale = new Scale(controls, SWT.HORIZONTAL);
	 * animationTimerDelayScale.setEnabled(true);
	 * animationTimerDelayScale.setMinimum(50);
	 * animationTimerDelayScale.setMaximum(500);
	 * animationTimerDelayScale.setSelection(200);
	 * getSelector().getService(AnimationService.class).setDelay(200);
	 * animationTimerDelayScale.setIncrement(50);
	 * animationTimerDelayScale.setPageIncrement(50); GridData gridData = new
	 * GridData(); gridData.grabExcessHorizontalSpace = true;
	 * gridData.horizontalAlignment = GridData.FILL;
	 * animationTimerDelayScale.setLayoutData(gridData);
	 * animationTimerDelayScale.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override public void widgetSelected(SelectionEvent e) { int step =
	 * (getAnimationTimerDelayScale().getSelection() /
	 * getAnimationTimerDelayScale().getIncrement())
	 * getAnimationTimerDelayScale().getIncrement();
	 * getAnimationTimerDelayScale().setSelection(step);
	 * getSelector().getService(AnimationService.class).setDelay(step); } }); }
	 * return animationTimerDelayScale; }
	 */
	public ImagingWidget getPainterWidget() {
		if (painter == null) {

			painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, getOwner());
			painter.getConf().setWidgetName("Painter :");
			painter.getConf().setPixelSize(15);
			painter.getConf().setPixelGridEnabled(true);
			painter.getConf().setGridStyle(GridStyle.Dot);
			painter.getConf().setTileGridEnabled(true);
			painter.getConf().setTileCursorEnabled(false);
			painter.getConf().supportsPainting = true;
			painter.getConf().supportsDrawCursor = true;
			painter.recalc();
			// painter.addDrawListener(getRepositoryWidget());
			// painter.addDrawListener(getPreviewerWidget());

			// menuService.registerContextMenu(painter,
			// "de.drazil.nerdsuite.popupmenu.popupmenu");

		}
		return painter;
	}

	public ImagingWidget getPreviewerWidget() {
		if (previewer == null) {

			previewer = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, getOwner());
			previewer.getConf().setWidgetName("Preview :");
			previewer.getConf().setPixelSize(1);
			previewer.getConf().setRows(1);
			previewer.getConf().setColumns(1);
			previewer.getConf().setPixelGridEnabled(false);
			previewer.getConf().setGridStyle(GridStyle.Dot);
			previewer.getConf().setTileGridEnabled(false);
			previewer.getConf().setTileCursorEnabled(false);
			previewer.getConf().setSeparatorEnabled(false);
			previewer.recalc();
		}
		return previewer;
	}

	private ImagingWidget getRepositoryWidget() {
		if (repository == null) {

			repository = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL,
					getOwner());
			repository.getConf().setWidgetName("Selector:");
			repository.getConf().setColumns(4);
			repository.getConf().setRows(4);
			repository.getConf().setPixelSize(3);
			repository.getConf().setPixelGridEnabled(false);
			repository.getConf().setTileGridEnabled(true);
			repository.getConf().setTileSubGridEnabled(false);
			repository.getConf().setTileCursorEnabled(true);
			repository.getConf().setSeparatorEnabled(false);
			repository.recalc();
			// repository.addDrawListener(getPainterWidget());
			// repository.addDrawListener(getPreviewerWidget());

		}
		// menuService.registerContextMenu(repository,
		// "de.drazil.nerdsuite.popupmenu.popupmenu");
		return repository;

	}

	/*
	 * private ImagingWidget getReferenceSelector() { if (referenceSelector == null)
	 * { referenceSelector = new ImageReferenceSelector(controls,
	 * SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
	 * referenceSelector.getConf().setWidgetName("ReferenceSelector:");
	 * referenceSelector.getConf().setWidth(8);
	 * referenceSelector.getConf().setHeight(8);
	 * referenceSelector.getConf().setTileColumns(1);
	 * referenceSelector.getConf().setTileRows(1);
	 * referenceSelector.getConf().setColumns(16);
	 * referenceSelector.getConf().setRows(16);
	 * referenceSelector.getConf().setPixelSize(2);
	 * referenceSelector.getConf().setPixelGridEnabled(false);
	 * referenceSelector.getConf().setTileGridEnabled(true);
	 * referenceSelector.getConf().setTileSubGridEnabled(false);
	 * referenceSelector.getConf().setTileCursorEnabled(true);
	 * referenceSelector.getConf().setSeparatorEnabled(false);
	 * referenceSelector.setSelectedTileOffset(0, 0, true);
	 * referenceSelector.setBitplane(getBinaryData()); referenceSelector.setColor(0,
	 * InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
	 * referenceSelector.setColor(1,
	 * InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
	 * referenceSelector.setColor(2,
	 * InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
	 * referenceSelector.setColor(3,
	 * InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
	 * referenceSelector.setSelectedColor(1); GridData gridData = new GridData();
	 * gridData.grabExcessHorizontalSpace = true; gridData.horizontalAlignment =
	 * GridData.FILL; gridData.horizontalSpan = 2;
	 * referenceSelector.setLayoutData(gridData); referenceSelector.recalc();
	 * referenceSelector.addDrawListener(getPainter());
	 * 
	 * } return referenceSelector;
	 * 
	 * }
	 */
	private Text getNotification() {
		if (notification == null) {
			notification = new Text(controls, SWT.NONE);
			notification.setEnabled(false);
		}
		return notification;
	}

	/*
	 * private Button getStartAnimation() { if (startAnimation == null) {
	 * startAnimation = new Button(controls, SWT.PUSH);
	 * startAnimation.setEnabled(true); startAnimation.setSelection(false);
	 * startAnimation.setText("Start Animation"); GridData gridData = new
	 * GridData(); gridData.grabExcessHorizontalSpace = true;
	 * gridData.horizontalAlignment = GridData.FILL;
	 * startAnimation.setLayoutData(gridData);
	 * startAnimation.addListener(SWT.Selection, new Listener() {
	 * 
	 * @Override public void handleEvent(Event event) {
	 * 
	 * if (!isAnimationRunning) { isAnimationRunning = true;
	 * getSelector().setMouseActionEnabled(false);
	 * getPainter().setMouseActionEnabled(false);
	 * getPreviewer().setMouseActionEnabled(false);
	 * getSelector().getService(AnimationService.class).execute(AnimationService.
	 * START); } else {
	 * getSelector().getService(AnimationService.class).execute(AnimationService.
	 * STOP); getSelector().setMouseActionEnabled(true);
	 * getPainter().setMouseActionEnabled(true);
	 * getPreviewer().setMouseActionEnabled(true); isAnimationRunning = false; } }
	 * }); } return startAnimation; }
	 */
	/*
	 * private Combo getPixelConfigSelector() { if (pixelConfigSelector == null) {
	 * pixelConfigSelector = new Combo(controls, SWT.DROP_DOWN);
	 * pixelConfigSelector.setItems(new String[] { "BC1", "BC2", "BC8" }); GridData
	 * gridData = new GridData(); gridData.grabExcessHorizontalSpace = true;
	 * gridData.horizontalAlignment = GridData.FILL; gridData.horizontalSpan = 2;
	 * pixelConfigSelector.setLayoutData(gridData);
	 * pixelConfigSelector.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override public void widgetSelected(SelectionEvent e) { Combo c = ((Combo)
	 * e.getSource()); int index = c.getSelectionIndex();
	 * setPixelConfig(c.getItem(index)); } }); } return pixelConfigSelector; }
	 */
	/*
	 * private Combo getFormatSelector() { if (formatSelector == null) {
	 * formatSelector = new Combo(controls, SWT.DROP_DOWN);
	 * formatSelector.setItems(new String[] { "Screen", "Char", "Char 2X",
	 * "Char 2Y", "Char 2XY", "Sprite", "Sprite 2X", "Sprite 2Y", "Sprite 2XY",
	 * "Screen", "Custom ..." }); GridData gridData = new GridData();
	 * gridData.grabExcessHorizontalSpace = true; gridData.horizontalAlignment =
	 * GridData.FILL; gridData.horizontalSpan = 2;
	 * formatSelector.setLayoutData(gridData);
	 * formatSelector.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override public void widgetSelected(SelectionEvent e) { Combo c = ((Combo)
	 * e.getSource()); int index = c.getSelectionIndex();
	 * setPaintFormat(c.getItem(index)); } }); } return formatSelector; }
	 * 
	 * private Combo getPaintModeSelector() { if (paintModeSelector == null) {
	 * paintModeSelector = new Combo(controls, SWT.DROP_DOWN);
	 * paintModeSelector.setItems(new String[] { "Pixel", "VerticalMirror",
	 * "HorizontalMirror", "Kaleidoscope" }); GridData gridData = new GridData();
	 * gridData.grabExcessHorizontalSpace = true; gridData.horizontalAlignment =
	 * GridData.FILL; gridData.horizontalSpan = 2;
	 * paintModeSelector.setLayoutData(gridData);
	 * paintModeSelector.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override public void widgetSelected(SelectionEvent e) { Combo c = ((Combo)
	 * e.getSource()); int index = c.getSelectionIndex();
	 * setPaintMode(c.getItem(index)); } }); } return paintModeSelector; }
	 */
	/*
	 * private void setPaintMode(String paintMode) { switch (paintMode) { case
	 * "Pixel": { getPainter().getConf().setPaintMode(PaintMode.Simple); break; }
	 * case "VerticalMirror": {
	 * getPainter().getConf().setPaintMode(PaintMode.VerticalMirror); break; } case
	 * "HorizontalMirror": {
	 * getPainter().getConf().setPaintMode(PaintMode.HorizontalMirror); break; }
	 * case "Kaleidoscope": {
	 * getPainter().getConf().setPaintMode(PaintMode.Kaleidoscope); break; } } }
	 */
	/*
	 * private void setPixelConfig(String pixelConfig) { switch (pixelConfig) { case
	 * "BC1": { getPainter().getConf().setPixelConfig(PixelConfig.BC1);
	 * getSelector().getConf().setPixelConfig(PixelConfig.BC1);
	 * getPreviewer().getConf().setPixelConfig(PixelConfig.BC1); break; } case
	 * "BC2": { getPainter().getConf().setPixelConfig(PixelConfig.BC2);
	 * getSelector().getConf().setPixelConfig(PixelConfig.BC2);
	 * getPreviewer().getConf().setPixelConfig(PixelConfig.BC2); break; } case
	 * "BC8": { getPainter().getConf().setPixelConfig(PixelConfig.BC8);
	 * getSelector().getConf().setPixelConfig(PixelConfig.BC8);
	 * getPreviewer().getConf().setPixelConfig(PixelConfig.BC8); break; }
	 * 
	 * } getPreviewer().recalc(); getSelector().recalc(); getPainter().recalc();
	 * parent.layout(); }
	 */

	/*
	 * @Override public void configurationChanged(int width, int height, int
	 * tileColumns, int tileRows, int painterPixelSize, int selectorPixelSize, int
	 * columns, int rows, int currentWidth) {
	 * getPainter().getConf().setWidth(width);
	 * getPainter().getConf().setHeight(height);
	 * getPainter().getConf().setTileColumns(tileColumns);
	 * getPainter().getConf().setTileRows(tileRows);
	 * getPainter().getConf().setPixelSize(painterPixelSize); getPainter().recalc();
	 * getPreviewer().getConf().setWidth(width);
	 * getPreviewer().getConf().setHeight(height);
	 * getPreviewer().getConf().setTileColumns(tileColumns);
	 * getPreviewer().getConf().setTileRows(tileRows); getPreviewer().recalc();
	 * getSelector().getConf().setWidth(width);
	 * getSelector().getConf().setHeight(height);
	 * getSelector().getConf().setTileColumns(tileColumns);
	 * getSelector().getConf().setTileRows(tileRows);
	 * getSelector().getConf().setPixelSize(selectorPixelSize);
	 * getSelector().recalc(); parent.layout(); }
	 */
	private byte[] getBinaryData() {
		if (binaryData == null) {

			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL url = bundle.getEntry("fonts/c64_lower.64c");

			try {
				binaryData = BinaryFileHandler.readFile(url.openConnection().getInputStream(), 2);
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

	private String getOwner() {
		return this.getClass().getClass() + ":" + this.hashCode();
	}

}
