
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.drazil.nerdsuite.util.E4Utils;

public class PerspectiveSwitchHandler {

	@Execute
	public void execute(IEventBroker broker, EPartService partService, MApplication app, EModelService modelService,
			@Named("de.drazil.nerdsuite.commandparameter.perspectiveId") String perspectiveId) {
		if ("u64".equals(perspectiveId)) {
			E4Utils.switchPerspective("de.drazil.nerdsuite.perspective.U64AppStreamer", app, modelService, partService);
		} else if ("gfx".equals(perspectiveId)) {
			E4Utils.switchPerspective("de.drazil.nerdsuite.perspective.GfxPerspective", app, modelService, partService);
		} else if ("code".equals(perspectiveId)) {
			E4Utils.switchPerspective("de.drazil.nerdsuite.perspective.CodingPerspective", app, modelService, partService);
		} else if ("disass".equals(perspectiveId)) {
			E4Utils.switchPerspective("de.drazil.nerdsuite.perspective.disassembler", app, modelService, partService);
		} else {

		}
	}
}