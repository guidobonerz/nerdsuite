package de.drazil.nerdsuite.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.explorer.Explorer;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class NewProjectHandler {

	@Execute
	public void execute(IWorkbench workbench, Shell shell, EPartService partService) {
		ProjectWizard projectWizard = new ProjectWizard();
		WizardDialog wizardDialog = new WizardDialog(shell, projectWizard);
		if (wizardDialog.open() == WizardDialog.OK) {
			Project project = projectWizard.getProject();
			MPart part = partService.findPart("de.drazil.nerdsuite.part.projectbrowser");
			Explorer explorer = (Explorer) part.getObject();
			Explorer.refreshExplorer(explorer, project);
		}
	}
}