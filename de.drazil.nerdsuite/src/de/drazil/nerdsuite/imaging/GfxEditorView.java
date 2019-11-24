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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.AnimationMode;
import de.drazil.nerdsuite.enums.CursorMode;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.enums.PaintMode;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.enums.ScaleMode;
import de.drazil.nerdsuite.enums.TileSelectionModes;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.imaging.service.AnimationService;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.IConfirmable;
import de.drazil.nerdsuite.imaging.service.ITileUpdateListener;
import de.drazil.nerdsuite.imaging.service.InvertService;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.MulticolorService;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.CustomSize;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.model.GridState;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.ColorChooser;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.LayerChooser;
import de.drazil.nerdsuite.widget.PainterWidget;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.RepositoryWidget;
import de.drazil.nerdsuite.widget.Tile;

public class GfxEditorView implements IConfirmable, ITileUpdateListener, IColorPaletteProvider {
	private PainterWidget painter;
	private RepositoryWidget repository;

	private ScrolledComposite scrollablePainter;
	private ScrolledComposite scrollableLayerChooser;

	private Composite parent;

	private TileRepositoryService tileRepositoryService;

	private ColorChooser multiColorChooser;
	private LayerChooser layerChooser;

	private CustomSize customSize = null;
	private GraphicFormat graphicFormat = null;
	private GraphicFormatVariant graphicFormatVariant = null;
	private Point defaultSize;
	private Point actualSize;

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
	public void manageInvert(@UIEventTopic("Invert") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			InvertService service = ServiceFactory.getService(owner, InvertService.class);
			service.setImagingWidgetConfiguration(painter.getConf());
			service.execute(0, this);
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
			tileRepositoryService.getSelectedTile().setMulticolorEnabled(multicolor);
			multiColorChooser.setMonochrom(!multicolor);
			part.setDirty(true);
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
	public void manageSelectionMode(@UIEventTopic("CursorMode") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			CursorMode cursorMode = (CursorMode) brokerObject.getTransferObject();
			painter.setCursorMode(cursorMode);
		}
	}

	@Inject
	@Optional
	public void animate(@UIEventTopic("AnimationMode") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			AnimationService service = ServiceFactory.getService(brokerObject.getOwner(), AnimationService.class);
			service.setComposite(parent);
			service.execute(((AnimationMode) brokerObject.getTransferObject()));
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
	public void exportFile(@UIEventTopic("ExportFile") BrokerObject brokerObject) {
		if (brokerObject.getTransferObject().equals("gfxEditor")) {
			if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
				StringBuilder sb = new StringBuilder();
			}
		}
	}

	@Inject
	@Optional
	public void importFile(@UIEventTopic("ImportFile") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			// IHandlerService handlerService = (IHandlerService)
			// getSite().getService(IHandlerService.class);
			// ICommandService commandService = (ICommandService)
			// getSite().getService(ICommandService.class);
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
		graphicFormatVariant = (GraphicFormatVariant) ((Map<String, Object>) part.getObject()).get("gfxFormatVariant");
		owner = (String) ((Map<String, Object>) part.getObject()).get("owner");
		part.getTransientData().put(Constants.OWNER, owner);
		part.setTooltip(graphicFormat.getName() + " " + graphicFormatVariant.getName());
		part.setIconURI("platform:/plugin/de.drazil.nerdsuite/" + project.getIconName());
		boolean isNewProject = (Boolean) ((Map<String, Object>) part.getObject()).get("isNewProject");
		String pt = project.getProjectType();
		ProjectType projectType = ProjectType.getProjectTypeById(pt.substring(pt.indexOf('_') + 1));

		file = new File(Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase()
				+ projectType.getSuffix());

		try {
			if (isNewProject) {
				customSize = (CustomSize) ((Map<String, Object>) part.getObject()).get("gfxCustomSize");
				updateWorkspace(true);
				file.createNewFile();
			} else {
				tileRepositoryService = load(file);
				if (project.getProjectVariant().equalsIgnoreCase("CUSTOM")) {
					customSize = tileRepositoryService.getCustomSize();
				}
			}
		} catch (IOException e1) {

		}

		defaultSize = new Point(graphicFormat.getWidth() * graphicFormatVariant.getPixelSize(),
				graphicFormat.getHeight() * graphicFormatVariant.getPixelSize());
		actualSize = new Point(
				graphicFormat.getWidth() * graphicFormatVariant.getPixelSize() * graphicFormatVariant.getTileColumns(),
				graphicFormat.getHeight() * graphicFormatVariant.getPixelSize() * graphicFormatVariant.getTileRows());
		if (customSize != null) {
			actualSize = new Point(
					customSize.getWidth() * graphicFormatVariant.getPixelSize() * customSize.getTileColumns(),
					customSize.getHeight() * graphicFormatVariant.getPixelSize() * customSize.getTileRows());
		}

		GridLayout layout = new GridLayout(2, false);

		parent.setLayout(layout);

		painter = createPainterWidget();
		GridData gridData = null;

		gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);

		gridData.widthHint = actualSize.x > 700 ? 700 : actualSize.x;
		gridData.heightHint = actualSize.y > 600 ? 600 : actualSize.y;

		gridData = new GridData(GridData.CENTER);

		gridData.verticalSpan = 2;
		scrollablePainter.setLayoutData(gridData);

		multiColorChooser = new ColorChooser(parent, SWT.DOUBLE_BUFFERED, 4,
				PlatformFactory.getPlatformColors(project.getTargetPlatform()));

		layerChooser = createLayerChooser();

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		multiColorChooser.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_BOTH);
		scrollableLayerChooser.setLayoutData(gridData);

		repository = createRepositoryWidget();
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		repository.setLayoutData(gridData);

		painter.init(owner, this);
		repository.init(owner, this);

		tileRepositoryService = ServiceFactory.getService(owner, TileRepositoryService.class);
		tileRepositoryService.addTileSelectionListener(painter, repository, layerChooser, this);
		tileRepositoryService.addTileManagementListener(painter, repository);
		tileRepositoryService.setCustomSize(customSize);

		if (graphicFormat.getId().endsWith("CHAR")) {
			repository.getConf().setScaleMode(ScaleMode.D8);
		} else if (graphicFormat.getId().endsWith("SPRITE")) {
			repository.getConf().setScaleMode(ScaleMode.D8);
		} else if (graphicFormat.getId().endsWith("SCREEN")) {
			repository.getConf().setScaleMode(ScaleMode.D8);

		} else {

		}

		painter.recalc();
		painter.addDrawListener(repository);

		menuService.registerContextMenu(painter, "de.drazil.nerdsuite.popupmenu.GfxToolbox");
		menuService.registerContextMenu(repository, "de.drazil.nerdsuite.popupmenu.GfxToolbox");

		multiColorChooser.addColorSelectionListener(painter);
		multiColorChooser.addColorSelectionListener(repository);

		if (isNewProject) {
			tileRepositoryService.addTile(painter.getConf().getTileSize());
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				tileRepositoryService.updateTileViewer(UpdateMode.All);
				painter.setCursorMode(CursorMode.Point);
			}
		});
	}

	public LayerChooser createLayerChooser() {
		scrollableLayerChooser = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.DOUBLE_BUFFERED) {
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return new Point(180, 500);
			}
		};

		layerChooser = new LayerChooser(scrollableLayerChooser, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, owner);
		return layerChooser;
	}

	public PainterWidget createPainterWidget() {
		scrollablePainter = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.DOUBLE_BUFFERED);
		painter = new PainterWidget(scrollablePainter, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		painter.getConf().setGraphicFormat(graphicFormat, graphicFormatVariant, customSize);
		painter.getConf().setWidgetName("Painter :");
		painter.getConf().setPixelGridEnabled(true);
		painter.getConf().setGridStyle(GridType.Dot);
		painter.getConf().setTileGridEnabled(true);
		painter.getConf().setTileCursorEnabled(false);
		painter.getConf().setSeparatorEnabled(graphicFormat.getId().endsWith("SCREEN") ? false : true);
		painter.getConf().supportsPainting = true;
		painter.getConf().supportsDrawCursor = true;
		painter.getConf().setTileSelectionModes(TileSelectionModes.RANGE);
		painter.getConf().setScaleMode(ScaleMode.None);
		painter.recalc();

		ScrollBar vb = scrollablePainter.getVerticalBar();
		vb.setThumb(10);

		vb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				System.out
						.println(graphicFormat.getHeight() * graphicFormat.getPixelSize() + "    " + vb.getSelection());
			}
		});

		scrollablePainter.setContent(painter);
		scrollablePainter.setExpandVertical(true);
		scrollablePainter.setExpandHorizontal(true);
		scrollablePainter.setMinSize(actualSize);

		scrollablePainter.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				// scrollablePainter.setMinSize(actualSize);
			}
		});
		return painter;

	}

	private RepositoryWidget createRepositoryWidget() {
		repository = new RepositoryWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
		repository.getConf().setGraphicFormat(graphicFormat, graphicFormatVariant, customSize);
		repository.getConf().setWidgetName("Selector:");
		repository.getConf().setPixelGridEnabled(false);
		repository.getConf().setTileGridEnabled(true);
		repository.getConf().setTileSubGridEnabled(false);
		repository.getConf().setTileCursorEnabled(true);
		repository.getConf().setSeparatorEnabled(false);
		repository.getConf().setTileSelectionModes(TileSelectionModes.SINGLE | TileSelectionModes.MULTI);
		repository.recalc();
		return repository;
	}

	@Override
	public void updateTiles(List<Integer> selectedTileIndexList, UpdateMode updateMode) {
		boolean enableAnimationControls = (updateMode == UpdateMode.Selection || updateMode == UpdateMode.Animation);
		List<String> tags = new LinkedList<>();
		tags.add("Animator");
		E4Utils.setToolItemEnabled(part, modelService, tags, enableAnimationControls);
	}

	@Override
	public void updateTile(int selectedTileIndex, UpdateMode updateMode) {
		List<String> tags1 = new LinkedList<>();
		tags1.add("MultiColorButton");
		Tile tile = tileRepositoryService.getTile(selectedTileIndex);
		E4Utils.setToolItemSelected(part, modelService, tags1, tile.isMulticolor());
		multiColorChooser.setMonochrom(!tile.isMulticolor());
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
	public Color getColor(Tile tile, int x, int y) {
		return null;
	}

	@Override
	public Color getColorByIndex(int index) {
		return PlatformFactory.getPlatformColors(project.getTargetPlatform())
				.get(tileRepositoryService.getSelectedTile().getActiveLayer().getColorIndex(index)).getColor();
	}

	private String getHeaderText() {
		String s = String.format(Constants.PROJECT_FILE_INFO_HEADER, project.getName(),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn()),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()),
				project.getTargetPlatform(), graphicFormat.getName(), graphicFormatVariant.getName());
		return s;
	}

}
