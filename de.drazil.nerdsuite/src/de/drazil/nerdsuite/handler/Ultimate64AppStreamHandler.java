package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.drazil.nerdsuite.util.E4Utils;

public class Ultimate64AppStreamHandler {

	@Execute
	public void execute(IEventBroker broker, EPartService partService, MApplication app, EModelService modelService) {
		E4Utils.switchPerspective("de.drazil.nerdsuite.perspective.U64AppStreamer", app, modelService, partService);
	}
}
