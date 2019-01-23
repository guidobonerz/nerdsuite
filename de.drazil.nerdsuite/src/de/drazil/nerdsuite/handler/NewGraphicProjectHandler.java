
package de.drazil.nerdsuite.handler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.widget.Layer;

public class NewGraphicProjectHandler {
	@Inject
	private IEventBroker eventBroker;

	@Execute
	public void execute(MPerspective activePerspective, MApplication app, EPartService partService,
			EModelService modelService, @Named("de.drazil.nerdsuite.commandparameter.gfxFormat") String format) {

		List<MPerspective> perspectives = modelService.findElements(app, "de.drazil.nerdsuite.perspective.Gfx",
				MPerspective.class, null);
		for (MPerspective perspective : perspectives) {
			if (!perspective.equals(activePerspective)) {
				partService.switchPerspective(perspective);
			}
		}

		GraphicFormat gf = GraphicFormatFactory.getFormatByName(format);

		TileRepositoryService tileService = ServiceFactory.getService("REPOSITORY", TileRepositoryService.class);
		tileService.addTile("test1", gf.getMetadata().getContentSize());
		tileService.addTile("test2", gf.getMetadata().getContentSize());
		Layer layer = null;

		layer = tileService.getTile(0).getActiveLayer();
		layer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		layer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		layer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		layer.setSelectedColorIndex(1);

		layer = tileService.getTile(1).getActiveLayer();
		layer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		layer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		layer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		layer.setSelectedColorIndex(1);

		eventBroker.post("gfxFormat", gf);
		eventBroker.post("setSelectedTile", 0);

	}
}