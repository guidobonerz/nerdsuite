package de.drazil.nerdsuite.handler;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.Workspace;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
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
			// createProjectStructure(project);

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

				Map<String, Object> projectSetup = new HashMap<String, Object>();
				projectSetup.put("project", project);
				projectSetup.put("gfxFormat", gf);
				projectSetup.put("gfxFormatVariant", 0);
				projectSetup.put("setSelectedTile", 0);

				MPart part = MBasicFactory.INSTANCE.createPart();
				part.setLabel(project.getProjectType() + "(" + project.getName() + ")");
				part.setCloseable(true);
				part.setObject(projectSetup);

				// part.setElementId(Long.toString(System.currentTimeMillis()));

				part.setContributionURI("bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.GfxEditorView");
				List<MPartStack> stacks = modelService.findElements(app, "de.drazil.nerdsuite.partstack.editorStack",
						MPartStack.class, null);
				stacks.get(0).getChildren().add(part);
				partService.showPart(part, PartState.ACTIVATE);

				// eventBroker.post("projectSetup", projectSetup);
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