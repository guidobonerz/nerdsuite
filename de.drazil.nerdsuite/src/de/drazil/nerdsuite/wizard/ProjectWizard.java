package de.drazil.nerdsuite.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.drazil.nerdsuite.model.Project;
import lombok.Getter;

public class ProjectWizard extends Wizard {
	@Getter
	private Project project;
	private IProjectWizardPage<Project> projectWizardPage;

	public ProjectWizard(String projectType) {
		super();
		setWindowTitle("New Project");
		if (projectType.equals("CODING_PROJECT")) {
			projectWizardPage = new CodingProjectWizardPage(projectType);
		} else if (projectType.equals("GRAPHIC_PROJECT")) {
			projectWizardPage = new GraphicsProjectWizardPage(projectType);
		}
	}

	@Override
	public boolean performFinish() {

		project = projectWizardPage.getModel();
		project.setId(project.getName().toUpperCase());
		return true;
	}

	@Override
	public void addPages() {
		addPage((IWizardPage) projectWizardPage);
	}
}
