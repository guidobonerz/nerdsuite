package de.drazil.nerdsuite.imaging;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.enums.PaintMode;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.enums.ScaleMode;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.IConfirmable;
import de.drazil.nerdsuite.imaging.service.ITileSelectionListener;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.MulticolorService;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GridState;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.ColorChooser;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.Tile;

public class GfxEditorView implements IConfirmable, ITileSelectionListener, IColorPaletteProvider {
	private ImagingWidget painter;
	private ImagingWidget previewer;
	private ImagingWidget repository;

	private Composite parent;

	private TileRepositoryService tileRepositoryService;

	private Button color1;
	private Button color2;
	private Button color3;
	private Button color4;

	private Button showOnlyActiveLayer;
	private Button showInactiveLayersTranslucent;

	private GraphicFormat graphicFormat = null;
	private int graphicFormatVariant = 0;

	private File file;
	private Project project;
	private String owner;
	@Inject
	MPart part;

	@Inject
	EModelService modelService;

	public GfxEditorView() {

	}

	@Inject
	@Optional
	public void managePencilMode(@UIEventTopic("PencilMode") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			PencilMode pencilMode = (PencilMode) brokerObject.getTransferObject();
			painter.getConf().setPencilMode(pencilMode);
		}
	}

	@Inject
	@Optional
	public void managePaintMode(@UIEventTopic("PaintMode") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			PaintMode paintMode = (PaintMode) brokerObject.getTransferObject();
			painter.getConf().setPaintMode(paintMode);
		}
	}

	@Inject
	@Optional
	public void manageShift(@UIEventTopic("Shift") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			ShiftService service = ServiceFactory.getService(owner, ShiftService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), this);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageRotake(@UIEventTopic("Rotate") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			RotationService service = ServiceFactory.getService(owner, RotationService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), this);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageFlip(@UIEventTopic("Flip") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			FlipService service = ServiceFactory.getService(owner, FlipService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), this);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageMirror(@UIEventTopic("Mirror") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			MirrorService service = ServiceFactory.getService(owner, MirrorService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), this);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void managePurge(@UIEventTopic("Purge") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			PurgeService service = ServiceFactory.getService(owner, PurgeService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(this);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageMulticolor(@UIEventTopic("Multicolor") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			boolean multicolor = (Boolean) brokerObject.getTransferObject();
			MulticolorService service = ServiceFactory.getService(owner, MulticolorService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(multicolor ? 1 : 0, this);
			tileRepositoryService.getSelectedTile().setMulticolor(multicolor);
		}
	}

	@Inject
	@Optional
	public void manageGridState(@UIEventTopic("GridType") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			GridState gridState = (GridState) brokerObject.getTransferObject();
			painter.getConf().setGridStyle(gridState.getGridStyle());
			painter.getConf().setPixelGridEnabled(gridState.isEnabled());
			painter.recalc();
		}
	}

	@Inject
	@Optional
	public void manageSave(@UIEventTopic("Save") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			save(file);
		}
	}

	@Inject
	@Optional
	public void manageTile(@UIEventTopic("Tile") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("add")) {
				tileRepositoryService.addTile(tileRepositoryService.getSelectedTile().getLayer(0).getContent().length);
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("remove")) {
				tileRepositoryService.removeSelected();
			} else {

			}
			repository.recalc();
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageLayer(@UIEventTopic("Layer") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("add")) {
				tileRepositoryService.getSelectedTile().addLayer();
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("remove")) {
				tileRepositoryService.getSelectedTile().removeActiveLayer();
			} else {

			}
			repository.recalc();
			part.setDirty(true);
		}
	}

	@PreDestroy
	public void preDestroy(MApplication app, MTrimmedWindow window, EModelService modelService, MPart part) {
		if (part.isDirty()) {

		}
	}

	@Override
	public boolean isConfirmed(String confirmationMessage) {
		return MessageDialog.openQuestion(parent.getShell(), "Image Process Confirmation", confirmationMessage);
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent, MApplication app, MTrimmedWindow window, EMenuService menuService) {
		this.parent = parent;

		project = (Project) ((Map<String, Object>) part.getObject()).get("project");
		graphicFormat = (GraphicFormat) ((Map<String, Object>) part.getObject()).get("gfxFormat");
		graphicFormatVariant = (Integer) ((Map<String, Object>) part.getObject()).get("gfxFormatVariant");
		owner = (String) ((Map<String, Object>) part.getObject()).get("owner");
		part.getTransientData().put(Constants.OWNER, owner);
		part.setTooltip(
				graphicFormat.getName() + " " + graphicFormat.getVariants().get(graphicFormatVariant).getName());
		part.setIconURI("platform:/plugin/de.drazil.nerdsuite/" + project.getIconName());
		boolean isNewProject = (Boolean) ((Map<String, Object>) part.getObject()).get("isNewProject");
		String pt = project.getProjectType();
		ProjectType projectType = ProjectType.getProjectTypeById(pt.substring(pt.indexOf('_') + 1));

		file = new File(Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase()
				+ projectType.getSuffix());

		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		parent.setLayout(layout);

		painter = createPainterWidget();
		GridData gridData = null;
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.verticalSpan = 5;
		painter.setLayoutData(gridData);

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

		showOnlyActiveLayer = new Button(parent, SWT.CHECK);
		showOnlyActiveLayer.setText("Show active layer only");
		showOnlyActiveLayer.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setShowOnlyActiveLayer(((Button) e.widget).getSelection());
		});

		showInactiveLayersTranslucent = new Button(parent, SWT.CHECK);
		showInactiveLayersTranslucent.setText("Show inactive layers translucent");
		showInactiveLayersTranslucent.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setShowInactiveLayerTranslucent(((Button) e.widget).getSelection());
		});

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		color1.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		color2.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		color3.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		color4.setLayoutData(gridData);

		PopupDialog pd = new PopupDialog(parent.getShell(), PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false,
				false, null, null) {
			@Override
			protected Control createDialogArea(Composite parent) {
				ColorChooser cc1 = new ColorChooser(parent, SWT.NONE, 0);
				cc1.setPlatformColors(PlatformFactory.getPlatformColors(project.getTargetPlatform()));
				return cc1;
			}

			protected void adjustBounds() {
				super.adjustBounds();
				Display d = Display.getCurrent();
				if (d == null) {
					d = Display.getDefault();
				}
				Point point = d.getCursorLocation();
				getShell().setLocation(point.x + 9, point.y + 14);
			}

			@Override
			protected Point getDefaultSize() {

				return new Point(100, 100);
			}
		};

		// gridData = new GridData(GridData.FILL);
		// gridData.horizontalSpan = 5;
		// cc1.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		showOnlyActiveLayer.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		showInactiveLayersTranslucent.setLayoutData(gridData);

		repository = createRepositoryWidget();
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 5;
		repository.setLayoutData(gridData);

		try {
			if (isNewProject) {
				updateWorkspace(true);
				file.createNewFile();
			} else {
				tileRepositoryService = load(file);
			}
		} catch (IOException e1) {

		}

		painter.init(owner, this);
		repository.init(owner, this);

		tileRepositoryService = ServiceFactory.getService(owner, TileRepositoryService.class);
		tileRepositoryService.addTileSelectionListener(painter, repository, this);
		tileRepositoryService.addTileManagementListener(painter, repository);

		if (graphicFormat.getId().endsWith("CHAR")) {
			repository.getConf().setScaleMode(ScaleMode.D8);
		} else if (graphicFormat.getId().endsWith("SPRITE")) {
			repository.getConf().setScaleMode(ScaleMode.D8);
		} else if (graphicFormat.getId().endsWith("SCREEN")) {
			repository.getConf().setScaleMode(ScaleMode.D4);

		} else {

		}

		painter.recalc();
		painter.addDrawListener(repository);

		menuService.registerContextMenu(painter, "de.drazil.nerdsuite.popupmenu.GfxToolbox");
		menuService.registerContextMenu(repository, "de.drazil.nerdsuite.popupmenu.GfxToolbox");

		if (isNewProject) {
			tileRepositoryService.addTile(painter.getConf().getTileSize());
		}
		//
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				tileRepositoryService.notifySelection();
				pd.open();
			}
		});

	}

	public ImagingWidget createPainterWidget() {
		painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		painter.getConf().setGraphicFormat(graphicFormat, graphicFormatVariant);
		painter.getConf().setWidgetName("Painter :");
		painter.getConf().setPixelGridEnabled(true);
		painter.getConf().setGridStyle(GridType.Dot);
		painter.getConf().setTileGridEnabled(true);
		painter.getConf().setTileCursorEnabled(false);
		painter.getConf().setSeparatorEnabled(graphicFormat.getId().endsWith("SCREEN") ? false : true);
		painter.getConf().supportsPainting = true;
		painter.getConf().supportsDrawCursor = true;
		painter.getConf().setScaleMode(ScaleMode.None);
		painter.recalc();
		// painter.addDrawListener(getPreviewerWidget());
		return painter;
	}

	public ImagingWidget createPreviewerWidget() {
		previewer = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		previewer.getConf().setWidgetName("Preview :");
		previewer.getConf().setPixelGridEnabled(false);
		previewer.getConf().setGridStyle(GridType.Dot);
		previewer.getConf().setTileGridEnabled(false);
		previewer.getConf().setTileCursorEnabled(false);
		previewer.getConf().setSeparatorEnabled(false);
		previewer.recalc();
		return previewer;
	}

	private ImagingWidget createRepositoryWidget() {
		repository = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
		repository.getConf().setGraphicFormat(graphicFormat, graphicFormatVariant);
		repository.getConf().setWidgetName("Selector:");
		repository.getConf().setPixelGridEnabled(false);
		repository.getConf().setTileGridEnabled(true);
		repository.getConf().setTileSubGridEnabled(false);
		repository.getConf().setTileCursorEnabled(true);
		repository.getConf().setSeparatorEnabled(false);
		repository.getConf().supportsMultiSelection = true;
		repository.getConf().supportsSingleSelection = true;
		repository.recalc();
		// repository.addDrawListener(getPainterWidget());
		// repository.addDrawListener(getPreviewerWidget());

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

	@Override
	public void tileSelected(Tile tile) {
		List<String> tags1 = new LinkedList<>();
		tags1.add("MultiColorButton");
		E4Utils.setToolItemEnabled(part, modelService, tags1, tile.isMulticolor());
		List<String> tags2 = new LinkedList<>();
		tags2.add("Animator");
		E4Utils.setToolItemEnabled(part, modelService, tags2, false);

	}

	@Override
	public void tilesSelected(List<TileLocation> tileLocationList) {
		System.out.println("selected tiles:" + tileLocationList.size());
		List<String> tags = new LinkedList<>();
		tags.add("Animator");
		E4Utils.setToolItemEnabled(part, modelService, tags, tileLocationList.size() > 1);

	}

	private void save(File file) {
		System.out.println("save tiles");
		updateWorkspace(false);
		LocalDateTime ldt = LocalDateTime.now();
		Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		project.setChangedOn(d);
		TileRepositoryService service = ServiceFactory.getService(owner, TileRepositoryService.class);
		TileRepositoryService.save(file, service, getHeaderText());
		part.setDirty(false);
	}

	private TileRepositoryService load(File file) {
		System.out.println("load tiles");
		part.setDirty(false);
		return TileRepositoryService.load(file, owner);
	}

	@Persist
	private void close() {
		save(file);
	}

	private void updateWorkspace(boolean addProject) {
		Initializer.getConfiguration().updateWorkspace(project, addProject);
	}

	@Override
	public Color getBackgroundColorIndex(Tile tile) {
		return getColorByIndex(tile.getBackgroundColorIndex());
	}

	@Override
	public Color getColor(Tile tile, int x, int y) {
		return null;
	}

	@Override
	public Color getColorByIndex(int index) {
		return PlatformFactory.getPlatformColors(project.getTargetPlatform()).get(index).getColor();
	}

	private String getHeaderText() {
		String s = "// Nerdsuite Project by drazil 2017-2019\n" + "// Projectname_____: " + project.getName() + "\n"
				+ "// Created on______: " + DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn())
				+ "\n" + "// Changed on______: "
				+ DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()) + "\n"
				+ "// Targetplatform__: " + project.getTargetPlatform() + "\n" + "// Type___________ : "
				+ graphicFormat.getName() + "\n" + "// Variant_________: "
				+ graphicFormat.getVariants().get(graphicFormatVariant).getName() + "\n";
		return s;
	}

}
