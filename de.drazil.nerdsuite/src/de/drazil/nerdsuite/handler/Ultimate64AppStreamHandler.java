package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.drazil.nerdsuite.util.E4Utils;

public class Ultimate64AppStreamHandler {

	public Ultimate64AppStreamHandler() {

	}

	@Execute
	public void execute(IEventBroker broker, EPartService partService, MApplication app, EModelService modelService) {

		MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.Ultimate64AppStreamView",
				"bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.Ultimate64AppStreamView", "Ultimate64",
				"Ultimate64 Application Streaming Client", null);
		E4Utils.addPart2PartStack(app, modelService, partService, "de.drazil.nerdsuite.partstack.editorStack", part,
				true);

	}
}
