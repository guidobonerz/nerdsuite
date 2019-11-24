
package de.drazil.nerdsuite.handler;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.enums.WizardType;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class ImportHandler {
	@Execute
	public void execute(MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService,
			@Named("de.drazil.nerdsuite.commandparameter.ProjectTypeId") String projectTypeId, IEventBroker broker) {
		/*
		 * broker.send("ImportFile", new BrokerObject((String)
		 * part.getTransientData().get(Constants.OWNER), target));
		 */

		Map<String, Object> userData = new HashMap<>();
		userData.put("PROJECT_TYPE_ID", projectTypeId);
		ProjectWizard projectWizard = new ProjectWizard(WizardType.ImportAsNewProject, "Import file as new Project", userData);
		WizardDialog wizardDialog = new WizardDialog(shell, projectWizard);
		if (wizardDialog.open() == WizardDialog.OK) {

		}
	}

}