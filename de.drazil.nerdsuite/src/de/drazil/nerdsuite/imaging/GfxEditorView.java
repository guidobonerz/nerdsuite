package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.constants.GridType;
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

	public GfxEditorView() {

	}

	public void controlPaintMode(@UIEventTopic("PaintMode") PaintMode paintMode) {
		getPainterWidget().getConf().setPaintMode(paintMode);
	}

	public void controlGridState(@UIEventTopic("GridState") GridState state) {
		getPainterWidget().getConf().setGridStyle(state.getGridStyle());
		getPainterWidget().getConf().setPixelGridEnabled(state.isEnabled());
		getPainterWidget().recalc();
	}

	@PreDestroy
	public void preDestroy(MApplication app, MTrimmedWindow window, EModelService modelService) {

	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent, MApplication app, MTrimmedWindow window, MPart part,
			EMenuService menuService, EModelService modelService) {
		this.parent = parent;

		getPainterWidget().getConf()
				.setGraphicFormat((GraphicFormat) ((Map<String, Object>) part.getObject()).get("gfxFormat"), 0);
		part.getTransientData().put(Constants.OWNER, getOwner());
		part.getTransientData().put("CONFIG", getPainterWidget().getConf());
		tileRepositoryService = ServiceFactory.getService(getOwner(), TileRepositoryService.class);

		boolean result = menuService.registerContextMenu(getPainterWidget(),
				"de.drazil.nerdsuite.popupmenu.GfxToolbox");
		result = menuService.registerContextMenu(getRepositoryWidget(), "de.drazil.nerdsuite.popupmenu.GfxToolbox");
		parent.setLayout(new MigLayout());
		getPainterWidget().setLayoutData("span 6 6");

		tile1 = new Button(parent, SWT.NONE);
		tile1.setText("tile1");
		tile1.addListener(SWT.Selection, e -> {
			tileRepositoryService.setSelectedTile(0);
			tileRepositoryService.getSelectedTile()
					.setShowInactiveLayerTranslucent(showInactiveLayersTranslucent.getSelection());
			tileRepositoryService.getSelectedTile().setShowOnlyActiveLayer(showOnlyActiveLayer.getSelection());
		});

		tile2 = new Button(parent, SWT.NONE);
		tile2.setText("tile2");
		tile2.addListener(SWT.Selection, e -> {
			tileRepositoryService.setSelectedTile(1);
			tileRepositoryService.getSelectedTile()
					.setShowInactiveLayerTranslucent(showInactiveLayersTranslucent.getSelection());
			tileRepositoryService.getSelectedTile().setShowOnlyActiveLayer(showOnlyActiveLayer.getSelection());
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
			tileRepositoryService.getSelectedTile().setShowOnlyActiveLayer(((Button) e.widget).getSelection());
		});

		showOnlyActiveLayer.setLayoutData("span 4, wrap");

		showInactiveLayersTranslucent = new Button(parent, SWT.CHECK);
		showInactiveLayersTranslucent.setText("Show inactive layers translucent");
		showInactiveLayersTranslucent.addListener(SWT.Selection, e -> {
			tileRepositoryService.getSelectedTile().setShowInactiveLayerTranslucent(((Button) e.widget).getSelection());
		});
		showInactiveLayersTranslucent.setLayoutData("span 4, wrap");

		getRepositoryWidget().setLayoutData("wrap");

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

		tileRepositoryService.addTileSelectionListener(getPainterWidget());
		tileRepositoryService.setSelectedTile(0);

		getPainterWidget().recalc();

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

	private String getOwner() {
		return this.getClass().getSimpleName() + ":" + this.hashCode();
	}

}
