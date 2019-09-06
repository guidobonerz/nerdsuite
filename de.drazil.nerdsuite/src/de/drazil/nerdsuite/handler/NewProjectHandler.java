package de.drazil.nerdsuite.handler;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
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

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.Workspace;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class NewProjectHandler {

	@Inject
	private IEventBroker eventBroker;

	@Execute
	public void execute(MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService,
			@Named("de.drazil.nerdsuite.commandparameter.projectType") String projectTypeId) {

		ProjectWizard projectWizard = new ProjectWizard(projectTypeId);
		WizardDialog wizardDialog = new WizardDialog(shell, projectWizard);
		if (wizardDialog.open() == WizardDialog.OK) {

			Project project = projectWizard.getProject();
			Workspace workspace = Initializer.getConfiguration().getWorkspace();
			workspace.add(project);
			Initializer.getConfiguration().writeWorkspace(workspace);
			createProjectStructure(project);

			String perspectiveId = projectTypeId.equals("CODING_PROJECT") ? "de.drazil.nerdsuite.perspective.coding"
					: "de.drazil.nerdsuite.perspective.gfx";

			List<MPerspective> perspectives = modelService.findElements(app, perspectiveId, MPerspective.class, null);
			for (MPerspective perspective : perspectives) {
				if (!perspective.equals(activePerspective)) {
					partService.switchPerspective(perspective);
				}
			}

			if (projectTypeId.equals("GRAPHIC_PROJECT")) {
				GraphicFormat gf = GraphicFormatFactory.getFormatByName(project.getProjectType());

				TileRepositoryService tileService = ServiceFactory.getService(project.getId() + "_REPOSITORY",
						TileRepositoryService.class);
				int contentSize = gf.getWidth() / gf.getStorageEntity() * gf.getHeight();

				tileService.addTile("first+tile", contentSize);
				Layer layer = null;

				layer = tileService.getTile(0).getActiveLayer();
				layer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
				layer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
				layer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
				layer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
				layer.setSelectedColorIndex(1);

				eventBroker.post("project", project);
				eventBroker.post("gfxFormat", gf);
				eventBroker.post("setSelectedTile", 0);
			}

			/*
			 * Project project = projectWizard.getProject(); MPart part =
			 * partService.findPart("de.drazil.nerdsuite.part.projectbrowser"); Explorer
			 * explorer = (Explorer) part.getObject(); Explorer.refreshExplorer(explorer,
			 * project);
			 */
		}
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