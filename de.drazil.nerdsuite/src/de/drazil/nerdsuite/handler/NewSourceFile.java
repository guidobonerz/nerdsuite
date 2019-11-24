package de.drazil.nerdsuite.handler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.wizard.SourceFileWizard;

public class NewSourceFile {

	@Execute
	public void execute(IWorkbench workbench, Shell shell, EPartService partService)

	{
		Map<String, Object> userData = new HashMap<>();

		SourceFileWizard sourceFileWizard = new SourceFileWizard(userData);
		WizardDialog wizardDialog = new WizardDialog(shell, sourceFileWizard);
		if (wizardDialog.open() == WizardDialog.OK) {
			// Project project = projectWizard.getProject();
			// MPart part =
			// partService.findPart("de.drazil.nerdsuite.part.projectbrowser");
			// Explorer explorer = (Explorer) part.getObject();
			// Explorer.refreshExplorer(explorer, project);
		}
	}

}