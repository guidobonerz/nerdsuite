
package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.util.E4Utils;

public class RunOnU64Handler {
	@Execute
	public void execute(MPart part, MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService, IEventBroker broker) {

		E4Utils.switchPerspective("de.drazil.nerdsuite.perspective.U64AppStreamer", app, modelService, partService);

		broker.send("U64_LoadAndRun", new BrokerObject("", "RunOnU64"));
	}
}