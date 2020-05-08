
package de.drazil.nerdsuite.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;

public class ExportHandler {

	@Execute
	public void execute(MPart part, MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService, IEventBroker broker,
			@Named("de.drazil.nerdsuite.commandparameter.Export") String command) {

		String owner = (String) part.getTransientData().get(Constants.OWNER);

		broker.send(command, new BrokerObject("", command));

		/*
		 * Map<String, Object> userData = new HashMap<>();
		 * userData.put(ProjectWizard.PROJECT_TYPE_ID, "?");
		 * 
		 * ExportWizard wizard = new ExportWizard(userData); WizardDialog wizardDialog =
		 * new WizardDialog(shell, wizard);
		 * 
		 * if (wizardDialog.open() == WizardDialog.OK) { Map<String, Object>
		 * projectSetup = new HashMap<String, Object>();
		 * 
		 * projectSetup.put("fileName", (String) userData.get(ProjectWizard.FILE_NAME));
		 * projectSetup.put("repository", ServiceFactory.getService(owner,
		 * TileRepositoryService.class)); ExportService service =
		 * ServiceFactory.getCommonService(ExportService.class);
		 * service.doExportGraphic(projectSetup); }
		 */
	}
}