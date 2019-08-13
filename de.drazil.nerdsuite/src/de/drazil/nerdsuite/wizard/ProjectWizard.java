package de.drazil.nerdsuite.wizard;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.Workspace;
import lombok.Getter;

public class ProjectWizard extends Wizard {
	@Getter
	private Project project;
	private IProjectWizardPage<Project> projectWizardPage;

	public ProjectWizard(String projectType) {
		super();

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

		Workspace workspace = Initializer.getConfiguration().getWorkspace();
		workspace.add(project);
		Initializer.getConfiguration().writeWorkspace(workspace);
		createProjectStructure(project);

		return true;
	}

	@Override
	public void addPages() {
		addPage((IWizardPage) projectWizardPage);
	}

	private void createProjectStructure(Project project) {
		File projectFolder = new File(
				Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase());
		projectFolder.mkdir();
		for (ProjectFolder folder : project.getFolderList()) {
			File subfolder = new File(
					projectFolder.getAbsolutePath() + Constants.FILE_SEPARATOR + folder.getName().toLowerCase());
			subfolder.mkdir();
		}
	}
}
