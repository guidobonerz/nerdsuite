package de.drazil.nerdsuite.imaging;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.constants.GridType;
import de.drazil.nerdsuite.constants.PaintMode;
import de.drazil.nerdsuite.constants.PencilMode;
import de.drazil.nerdsuite.constants.ScaleMode;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GridState;
import de.drazil.nerdsuite.widget.ImagingWidget;

public class GfxEditorView {
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

	public GfxEditorView() {

	}

	@Inject
	@Optional
	public void managePencilMode(@UIEventTopic("PencilMode") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			PencilMode pencilMode = (PencilMode) brokerObject.getTransferObject();
			getPainterWidget().getConf().setPencilMode(pencilMode);
		}
	}

	@Inject
	@Optional
	public void managePaintMode(@UIEventTopic("PaintMode") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			PaintMode paintMode = (PaintMode) brokerObject.getTransferObject();
			getPainterWidget().getConf().setPaintMode(paintMode);
		}
	}

	@Inject
	@Optional
	public void manageShift(@UIEventTopic("Shift") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			ShiftService service = ServiceFactory.getService(getOwner(), ShiftService.class);
			service.setImagingWidgetConfiguration(getPainterWidget().getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()));
		}
	}

	@Inject
	@Optional
	public void manageRotake(@UIEventTopic("Rotate") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			RotationService service = ServiceFactory.getService(getOwner(), RotationService.class);
			service.setImagingWidgetConfiguration(getPainterWidget().getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()));
		}
	}

	@Inject
	@Optional
	public void manageFlip(@UIEventTopic("Flip") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			FlipService service = ServiceFactory.getService(getOwner(), FlipService.class);
			service.setImagingWidgetConfiguration(getPainterWidget().getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()));
		}
	}

	@Inject
	@Optional
	public void manageMirror(@UIEventTopic("Mirror") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			MirrorService service = ServiceFactory.getService(getOwner(), MirrorService.class);
			service.setImagingWidgetConfiguration(getPainterWidget().getConf());
			service.execute(Integer.valueOf((int) brokerObject.getTransferObject()));
		}
	}

	@Inject
	@Optional
	public void managePurge(@UIEventTopic("Purge") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			PurgeService service = ServiceFactory.getService(getOwner(), PurgeService.class);
			service.setImagingWidgetConfiguration(getPainterWidget().getConf());
			service.execute();
		}
	}

	@Inject
	@Optional
	public void manageGridState(@UIEventTopic("GridType") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			GridState gridState = (GridState) brokerObject.getTransferObject();
			getPainterWidget().getConf().setGridStyle(gridState.getGridStyle());
			getPainterWidget().getConf().setPixelGridEnabled(gridState.isEnabled());
			getPainterWidget().recalc();
		}
	}

	@Inject
	@Optional
	public void manageTile(@UIEventTopic("Tile") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(getOwner())) {
			if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("add")) {
				tileRepositoryService.addTile("rename_me",
						tileRepositoryService.getSelectedTile().getLayer(0).getContent().length);
			} else if (((String) brokerObject.getTransferObject()).equalsIgnoreCase("remove")) {
				tileRepositoryService.removeSelected();
			} else {

			}
			getRepositoryWidget().recalc();
		}
	}

	@PreDestroy
	public void preDestroy(MApplication app, MTrimmedWindow window, EModelService modelService) {

	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent, MApplication app, MTrimmedWindow window, MPart part,
			EMenuService menuService, EModelService modelService) {
		this.parent = parent;
		tileRepositoryService = ServiceFactory.getService(getOwner(), TileRepositoryService.class);
		part.getTransientData().put(Constants.OWNER, getOwner());
		menuService.registerContextMenu(getPainterWidget(), "de.drazil.nerdsuite.popupmenu.GfxToolbox");

		GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		parent.setLayout(layout);

		GridData gridData = null;
		GraphicFormat graphicFormat = (GraphicFormat) ((Map<String, Object>) part.getObject()).get("gfxFormat");
		int graphicFormatVariant = (Integer) ((Map<String, Object>) part.getObject()).get("gfxFormatVariant");
		getPainterWidget().getConf().setGraphicFormat(graphicFormat, graphicFormatVariant);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.verticalSpan = 5;
		getPainterWidget().setLayoutData(gridData);

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

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		showOnlyActiveLayer.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 4;
		showInactiveLayersTranslucent.setLayoutData(gridData);

		getRepositoryWidget().getConf()
				.setGraphicFormat((GraphicFormat) ((Map<String, Object>) part.getObject()).get("gfxFormat"), 0);
		menuService.registerContextMenu(getRepositoryWidget(), "de.drazil.nerdsuite.popupmenu.GfxToolbox");

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 5;

		getRepositoryWidget().setLayoutData(gridData);

		int contentSize = getPainterWidget().getConf().getWidth() * getPainterWidget().getConf().getHeight();
		tileRepositoryService.addTile("rename_me", contentSize);
		tileRepositoryService.addTileSelectionListener(getPainterWidget());
		tileRepositoryService.setSelectedTile(0);

		if (graphicFormat.getId().endsWith("CHAR")) {
			getRepositoryWidget().getConf().setScaleMode(ScaleMode.D8);
		} else if (graphicFormat.getId().endsWith("SPRITE")) {
			getRepositoryWidget().getConf().setScaleMode(ScaleMode.D8);
		} else if (graphicFormat.getId().endsWith("SCREEN")) {
			getRepositoryWidget().getConf().setScaleMode(ScaleMode.D4);

		} else {

		}

		getPainterWidget().recalc();
		getPainterWidget().addDrawListener(getRepositoryWidget());

	}

	public ImagingWidget getPainterWidget() {
		if (painter == null) {
			painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, getOwner());
			painter.getConf().setWidgetName("Painter :");
			painter.getConf().setPixelGridEnabled(true);
			painter.getConf().setGridStyle(GridType.Dot);
			painter.getConf().setTileGridEnabled(true);
			painter.getConf().setTileCursorEnabled(false);
			painter.getConf().supportsPainting = true;
			painter.getConf().supportsDrawCursor = true;
			painter.getConf().setScaleMode(ScaleMode.None);
			painter.recalc();

			// painter.addDrawListener(getPreviewerWidget());
		}
		return painter;
	}

	public ImagingWidget getPreviewerWidget() {
		if (previewer == null) {
			previewer = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, getOwner());
			previewer.getConf().setWidgetName("Preview :");
			previewer.getConf().setPixelGridEnabled(false);
			previewer.getConf().setGridStyle(GridType.Dot);
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
		}
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

	private String getOwner() {
		return this.getClass().getSimpleName() + ":" + this.hashCode();
	}

}
