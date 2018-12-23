
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

import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;

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
		eventBroker.post("gfxFormat", gf);

		// MPart editorPart =
		// partService.findPart("de.drazil.nerdsuite.part.GfxEditor");
		
		// GfxEditorView editor = (GfxEditorView) editorPart.getObject();

		int a = 0;
		// MPart repository =
		// partService.findPart("de.drazil.nerdsuite.part.repository");
		// MPart preview = partService.findPart("de.drazil.nerdsuite.part.preview");
	}
}