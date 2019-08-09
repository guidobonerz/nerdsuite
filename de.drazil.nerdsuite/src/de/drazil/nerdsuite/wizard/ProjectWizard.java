package de.drazil.nerdsuite.wizard;

import java.io.File;
import java.util.Map.Entry;

import lombok.Getter;

import org.eclipse.jface.wizard.Wizard;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.Workspace;

public class ProjectWizard extends Wizard
{
	@Getter
	private Project project;
	private NewProjectWizardPage newProjectWizardPage;

	public ProjectWizard(String projectType)
	{
		super();
		newProjectWizardPage = new NewProjectWizardPage(projectType);
	}

	@Override
	public boolean performFinish()
	{
		/*
		project = newProjectWizardPage.getModel();
		project.setId(project.getName().toUpperCase());
		try
		{
			Workspace workspace = Initializer.getConfiguration().getWorkspace();
			workspace.add(project);
			Initializer.getConfiguration().writeWorkspace(workspace);
			createProjectStructure(project);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
*/
		return true;
	}

	@Override
	public void addPages()
	{
		addPage(newProjectWizardPage);
	}

	private void createProjectStructure(Project project)
	{
		File projectFolder = new File(Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase());
		projectFolder.mkdir();
		for (Entry<String, ProjectFolder> entry : project.getProjectFolderMap().entrySet())
		{
			File subfolder = new File(projectFolder.getAbsolutePath() + Constants.FILE_SEPARATOR + entry.getValue().getName().toLowerCase());
			subfolder.mkdir();
		}
	}
}
