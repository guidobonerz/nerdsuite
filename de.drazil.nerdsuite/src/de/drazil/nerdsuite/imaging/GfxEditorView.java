package de.drazil.nerdsuite.imaging;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.AnimationMode;
import de.drazil.nerdsuite.enums.CursorMode;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.enums.PaintMode;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.enums.TileSelectionModes;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.imaging.service.AnimationService;
import de.drazil.nerdsuite.imaging.service.ClipboardService;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.IConfirmable;
import de.drazil.nerdsuite.imaging.service.ITileUpdateListener;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.imaging.service.InvertService;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.MulticolorToggleService;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.model.GridState;
import de.drazil.nerdsuite.model.ViewSetup;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.GraphicMetaData;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.ColorChooser;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.LayerChooser;
import de.drazil.nerdsuite.widget.PainterWidget;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.ReferenceWidget;
import de.drazil.nerdsuite.widget.RepositoryWidget;
import de.drazil.nerdsuite.widget.Tile;

public class GfxEditorView implements ITileUpdateListener {

	private Composite parent;
	private PainterWidget painter;
	private RepositoryWidget repository;
	private ReferenceWidget referenceRepository;

	private ScrolledComposite scrollablePainter;
	private ScrolledComposite scrollableRepository;
	private ScrolledComposite scrollableLayerChooser;

	private TileRepositoryService tileRepositoryService;
	private TileRepositoryService tileRepositoryReferenceService;

	private IColorPaletteProvider colorPaletteProvider;
	private IConfirmable modificationConfirmation;

	private ColorChooser multiColorChooser;

	private LayerChooser layerChooser;

	private GraphicMetaData metadata = null;
	private GraphicFormat graphicFormat = null;
	private GraphicFormatVariant graphicFormatVariant = null;
	private Point actualSize;

	private Project project;
	private String owner;

	@Inject
	private MPart part;

	@Inject
	private EModelService modelService;

	private static List<ViewSetup> pixelMap = new ArrayList<ViewSetup>();
	static {
		pixelMap.add(new ViewSetup("CHARSET", "STANDARD", "PAINTER", 1, 32));
		pixelMap.add(new ViewSetup("CHARSET", "DX", "PAINTER", 1, 32));
		pixelMap.add(new ViewSetup("CHARSET", "DY", "PAINTER", 1, 32));
		pixelMap.add(new ViewSetup("CHARSET", "DXY", "PAINTER", 1, 32));
		pixelMap.add(new ViewSetup("CHARSET", "CUSTOM", "PAINTER", 1, 32));

		pixelMap.add(new ViewSetup("SPRITESET", "STANDARD", "PAINTER", 1, 16));
		pixelMap.add(new ViewSetup("SPRITESET", "DX", "PAINTER", 1, 16));
		pixelMap.add(new ViewSetup("SPRITESET", "DY", "PAINTER", 1, 16));
		pixelMap.add(new ViewSetup("SPRITESET", "DXY", "PAINTER", 1, 16));
		pixelMap.add(new ViewSetup("SPRITESET", "CUSTOM", "PAINTER", 1, 16));

		pixelMap.add(new ViewSetup("SCREENSET", "STANDARD", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "DX", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "DY", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "DXY", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "CUSTOM", "PAINTER", 8, 2));

		pixelMap.add(new ViewSetup("SCREENSET", "STANDARD", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "DX", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "DY", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "DXY", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("SCREENSET", "CUSTOM", "REFERENCE", 1, 2));

		pixelMap.add(new ViewSetup("PETSCII", "STANDARD", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("PETSCII", "DX", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("PETSCII", "DY", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("PETSCII", "DXY", "PAINTER", 8, 2));
		pixelMap.add(new ViewSetup("PETSCII", "CUSTOM", "PAINTER", 8, 2));

		pixelMap.add(new ViewSetup("PETSCII", "STANDARD", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("PETSCII", "DX", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("PETSCII", "DY", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("PETSCII", "DXY", "REFERENCE", 1, 2));
		pixelMap.add(new ViewSetup("PETSCII", "CUSTOM", "REFERENCE", 1, 2));

	}

	public GfxEditorView() {

		colorPaletteProvider = new IColorPaletteProvider() {

			@Override
			public Color getColor(Tile tile, int x, int y) {
				return null;
			}

			@Override
			public Color getColorByIndex(int index) {
				return PlatformFactory.getPlatformColors(tileRepositoryService.getMetadata().getPlatform()).get(index)
						.getColor();
			}
		};

		modificationConfirmation = new IConfirmable() {
			@Override
			public boolean isConfirmed(String confirmationMessage) {
				return MessageDialog.openQuestion(parent.getShell(), "Image Process Confirmation", confirmationMessage);
			}
		};

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
			service.setConf(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), modificationConfirmation);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageRotate(@UIEventTopic("Rotate") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			RotationService service = ServiceFactory.getService(owner, RotationService.class);
			service.setConf(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), modificationConfirmation);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageFlip(@UIEventTopic("Flip") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			FlipService service = ServiceFactory.getService(owner, FlipService.class);
			service.setConf(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), modificationConfirmation);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageInvert(@UIEventTopic("Invert") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			InvertService service = ServiceFactory.getService(owner, InvertService.class);
			service.setConf(painter.getConf());
			service.execute(0, modificationConfirmation);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageMirror(@UIEventTopic("Mirror") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			MirrorService service = ServiceFactory.getService(owner, MirrorService.class);
			service.setConf(painter.getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()), modificationConfirmation);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void managePurge(@UIEventTopic("Purge") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			PurgeService service = ServiceFactory.getService(owner, PurgeService.class);
			service.setConf(painter.getConf());
			service.execute(modificationConfirmation);
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void manageMulticolor(@UIEventTopic("Multicolor") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			boolean multicolor = (Boolean) brokerObject.getTransferObject();
			multiColorChooser.setMulticolorEnabled(multicolor);
			MulticolorToggleService service = ServiceFactory.getService(owner, MulticolorToggleService.class);
			service.execute(multicolor ? 1 : 0);
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
			// painter.recalc();
			painter.doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.READ);
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
			save();
		}
	}

	@Inject
	@Optional
	public void manageTile(@UIEventTopic("Tile") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("add")) {
				tileRepositoryService.addTile();
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("remove")) {
				// tileRepositoryService.removeSelected();
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
				// tileRepositoryService.addLayer();
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("remove")) {
				// tileRepositoryService.removeActiveLayer();
			} else {

			}
			repository.recalc();
			part.setDirty(true);
		}
	}

	@Inject
	@Optional
	public void clipboard(@UIEventTopic("Clipboard") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("cut")) {
				ServiceFactory.getService(owner, ClipboardService.class).clipboardAction(ClipboardService.CUT);
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("copy")) {
				ServiceFactory.getService(owner, ClipboardService.class).clipboardAction(ClipboardService.COPY);
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("paste")) {
				ServiceFactory.getService(owner, ClipboardService.class).clipboardAction(ClipboardService.PASTE);
				part.setDirty(true);
			} else {

			}
		}
	}

	@PreDestroy
	public void preDestroy(MApplication app, MTrimmedWindow window, EModelService modelService, MPart part) {
		if (part.isDirty()) {

		}
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent, MApplication app, MTrimmedWindow window, EMenuService menuService) {
		this.parent = parent;

		Map<String, Object> pm = (Map<String, Object>) part.getObject();
		project = (Project) pm.get("project");
		owner = (String) pm.get("repositoryOwner");

		tileRepositoryService = ServiceFactory.getService(project.getId(), TileRepositoryService.class);
		tileRepositoryReferenceService = tileRepositoryService.getReferenceRepository();

		metadata = tileRepositoryService.getMetadata();
		String graphicFormatId = metadata.getPlatform() + "_" + metadata.getType();
		graphicFormat = GraphicFormatFactory.getFormatById(graphicFormatId);
		graphicFormatVariant = GraphicFormatFactory.getFormatVariantById(graphicFormatId, metadata.getVariant());

		part.setDirty(false);
		part.getTransientData().put(Constants.OWNER, owner);
		part.setTooltip(graphicFormat.getName() + " " + graphicFormatVariant.getName());
		part.setIconURI("platform:/plugin/de.drazil.nerdsuite/" + project.getIconName());

		GridLayout layout = new GridLayout(tileRepositoryService.hasReference() ? 3 : 2, false);
		parent.setLayout(layout);

		painter = getPainterWidget();
		// painter.addDrawListener(repository);

		multiColorChooser = new ColorChooser(parent, SWT.DOUBLE_BUFFERED,
				graphicFormat.getId().endsWith("PETSCII") ? 2 : 4,
				PlatformFactory.getPlatformColors(tileRepositoryService.getMetadata().getPlatform()));

		if (tileRepositoryService.hasReference()) {
			referenceRepository = getReferenceRepositoryWidget();
		}

		layerChooser = createLayerChooser();

		repository = getRepositoryWidget();

		tileRepositoryService.addTileUpdateListener(painter, repository, layerChooser, this);
		tileRepositoryService.addTileManagementListener(painter, repository);

		if (tileRepositoryService.hasReference()) {
			// referenceRepository.init();
		}

		// painter.init();
		// repository.init();

		multiColorChooser.addColorSelectionListener(painter);
		multiColorChooser.addColorSelectionListener(repository);

		menuService.registerContextMenu(painter, "de.drazil.nerdsuite.popupmenu.GfxToolbox");
		menuService.registerContextMenu(repository, "de.drazil.nerdsuite.popupmenu.GfxToolbox");
		actualSize = new Point(painter.getConf().getTileWidthPixel() * painter.getConf().getZoomFactor(),
				painter.getConf().getTileHeightPixel() * painter.getConf().getZoomFactor());
		scrollablePainter.setMinSize(actualSize);
		int worksheetWidth = 640;
		int worksheetHeight = 400;
		/*
		 * if (actualSize.x >= worksheetWidth) { worksheetWidth += 25; } if
		 * (actualSize.y >= worksheetHeight) { worksheetHeight += 25; }
		 */
		GridData gridData = null;

		if (metadata.getType().equals("PETSCII") || metadata.getType().equals("SCREENSET")) {
			gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2);
			gridData.widthHint = actualSize.x >= worksheetWidth ? worksheetWidth : actualSize.x;
			gridData.heightHint = actualSize.y >= worksheetHeight ? worksheetHeight : actualSize.y;
			scrollablePainter.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			multiColorChooser.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.BEGINNING, false, true, 1, 3);
			referenceRepository.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
			scrollableLayerChooser.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			scrollableRepository.setLayoutData(gridData);
		} else {
			gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2);
			gridData.widthHint = actualSize.x > worksheetWidth ? worksheetWidth : actualSize.x;
			gridData.heightHint = actualSize.y > worksheetHeight ? worksheetHeight : actualSize.y;
			scrollablePainter.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			multiColorChooser.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
			scrollableLayerChooser.setLayoutData(gridData);

			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
			scrollableRepository.setLayoutData(gridData);
		}

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				List<String> tags = new LinkedList<>();
				tags.add("MultiColorButton");
				boolean multicolor = tileRepositoryService.getSelectedTile().isMulticolorEnabled();
				MHandledItem item = E4Utils.getMenuITemByTag(part, modelService, tags);
				item.setSelected(multicolor);
				multiColorChooser.setMulticolorEnabled(multicolor);
				parent.getDisplay().getActiveShell().notifyListeners(SWT.Resize, new Event());
				painter.setCursorMode(CursorMode.Point);
				// painter.doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.UPDATE);
				repository.doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.UPDATE);
				if (tileRepositoryService.hasReference()) {
					// referenceRepository.doRedraw(RedrawMode.DrawAllTiles,
					// ImagePainterFactory.UPDATE);
				}
				parent.layout();
			}
		});
	}

	public LayerChooser createLayerChooser() {
		scrollableLayerChooser = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.DOUBLE_BUFFERED) {
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return new Point(180, 300);
			}
		};

		layerChooser = new LayerChooser(scrollableLayerChooser, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, owner);
		return layerChooser;
	}

	public PainterWidget getPainterWidget() {

		if (scrollablePainter == null) {
			scrollablePainter = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.DOUBLE_BUFFERED);
			scrollablePainter.addListener(SWT.Resize, event -> {

				// scrollablePainter.setMinSize(painter.computeSize(pain, height));
			});
			painter = new PainterWidget(scrollablePainter, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, owner,
					colorPaletteProvider, false);
			painter.getConf().setPixelGridEnabled(true);
			painter.getConf().setGridStyle(GridType.Dot);
			painter.getConf().setTileGridEnabled(false);
			painter.getConf().setTileCursorEnabled(false);
			painter.getConf().setSeparatorEnabled(graphicFormat.getId().endsWith("SCREENSET") ? false : true);
			painter.getConf().setTileSelectionModes(TileSelectionModes.RANGE);
			painter.getConf().setViewSetup(getViewSetup(metadata.getType(), metadata.getVariant(), "PAINTER"));
			painter.getConf().computeDimensions();
			scrollablePainter.setContent(painter);
			scrollablePainter.setExpandVertical(true);
			scrollablePainter.setExpandHorizontal(true);
		}
		return painter;
	}

	private RepositoryWidget getRepositoryWidget() {
		if (scrollableRepository == null) {
			scrollableRepository = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
			repository = new RepositoryWidget(scrollableRepository, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, owner,
					colorPaletteProvider, true);
			repository.getConf().setPixelGridEnabled(false);
			repository.getConf().setTileGridEnabled(true);
			repository.getConf().setTileSubGridEnabled(false);
			repository.getConf().setTileCursorEnabled(true);
			repository.getConf().setSeparatorEnabled(false);
			repository.getConf().setTileSelectionModes(TileSelectionModes.SINGLE | TileSelectionModes.MULTI);
			repository.getConf().setViewSetup(getViewSetup(metadata.getType(), metadata.getVariant(), "REPOSITORY"));
			repository.getConf().computeDimensions();
			scrollableRepository.setContent(repository);
			scrollableRepository.setExpandVertical(true);
			scrollableRepository.setExpandHorizontal(true);
			scrollableRepository.setMinSize(actualSize);
		}
		return repository;
	}

	private ReferenceWidget getReferenceRepositoryWidget() {
		if (referenceRepository == null) {
			referenceRepository = new ReferenceWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED,
					tileRepositoryReferenceService.getOwner(), colorPaletteProvider, false);
			referenceRepository.getConf().setRows(16);
			referenceRepository.getConf().setColumns(16);
			referenceRepository.getConf().setTileGap(3);
			referenceRepository.getConf().setPixelGridEnabled(false);
			referenceRepository.getConf().setTileGridEnabled(true);
			referenceRepository.getConf().setTileSubGridEnabled(false);
			referenceRepository.getConf().setTileCursorEnabled(true);
			referenceRepository.getConf().setSeparatorEnabled(false);
			referenceRepository.getConf().setTileSelectionModes(TileSelectionModes.SINGLE);
			referenceRepository.getConf()
					.setViewSetup(getViewSetup(metadata.getType(), metadata.getVariant(), "REFERENCE"));
			referenceRepository.getConf().computeDimensions();
		}
		return referenceRepository;
	}

	private ViewSetup getViewSetup(String type, String variant, String widget) {

		ViewSetup vs = pixelMap.stream()
				.filter(v -> v.getType().equals(type) && v.getVariant().equals(variant) && v.getWidget().equals(widget))
				.findFirst().orElse(new ViewSetup(null, null, null, 1, 1));
		return vs;
	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int action) {
		boolean enableAnimationControls = (redrawMode == RedrawMode.DrawSelectedTiles
				|| redrawMode == RedrawMode.DrawTemporarySelectedTile);
		List<String> tags = new LinkedList<>();
		tags.add("Animator");
		E4Utils.setToolItemEnabled(part, modelService, tags, enableAnimationControls);
		if (redrawMode == RedrawMode.DrawSelectedTile) {
			List<String> tags1 = new LinkedList<>();
			tags1.add("MultiColorButton");
			Tile tile = tileRepositoryService.getTile(selectedTileIndexList.get(0));
			boolean multicolor = tile.isMulticolorEnabled();
			E4Utils.setToolItemSelected(part, modelService, tags1, multicolor);
			if (multiColorChooser != null) {
				multiColorChooser.setMulticolorEnabled(multicolor);
			}
		}
	}

	private void save() {
		System.out.println("save tiles");
		updateWorkspace(false);
		LocalDateTime ldt = LocalDateTime.now();
		Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		project.setChangedOn(d);
		tileRepositoryService.save(project);
		part.setDirty(false);
	}

	@Persist
	private void close() {
		save();
	}

	private void updateWorkspace(boolean addProject) {
		Initializer.getConfiguration().updateWorkspace(project, addProject, false);
	}

}
