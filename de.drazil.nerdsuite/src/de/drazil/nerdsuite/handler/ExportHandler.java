
package de.drazil.nerdsuite.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.service.ExportService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.wizard.ExportWizard;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class ExportHandler {

	@Execute
	public void execute(MPart part, MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService, IEventBroker broker) {

		String owner = (String) part.getTransientData().get(Constants.OWNER);

		Map<String, Object> userData = new HashMap<>();
		userData.put(ProjectWizard.PROJECT_TYPE_ID, "?");

		ExportWizard wizard = new ExportWizard(userData);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);

		if (wizardDialog.open() == WizardDialog.OK) {
			Map<String, Object> projectSetup = new HashMap<String, Object>();

			projectSetup.put("fileName", (String) userData.get(ProjectWizard.FILE_NAME));
			projectSetup.put("repository", ServiceFactory.getService(owner, TileRepositoryService.class));
			ExportService service = ServiceFactory.getCommonService(ExportService.class);
			service.doExportGraphic(projectSetup);
		}
	}
}