package de.drazil.nerdsuite.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
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
import de.drazil.nerdsuite.enums.SizeVariant;
import de.drazil.nerdsuite.explorer.Explorer;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.Workspace;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class NewProjectHandler {

	@Execute
	public void execute(MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService,
			@Named("de.drazil.nerdsuite.commandparameter.projectType") String projectTypeId) {

		ProjectWizard projectWizard = new ProjectWizard(projectTypeId);
		WizardDialog wizardDialog = new WizardDialog(shell, projectWizard);
		if (wizardDialog.open() == WizardDialog.OK) {

			Project project = projectWizard.getProject();

			String perspectiveId = projectTypeId.equals("CODING_PROJECT") ? "de.drazil.nerdsuite.perspective.coding"
					: "de.drazil.nerdsuite.perspective.gfx";
			/*
			 * List<MPerspective> perspectives = modelService.findElements(app,
			 * perspectiveId, MPerspective.class, null); for (MPerspective perspective :
			 * perspectives) { if (!perspective.equals(activePerspective)) {
			 * partService.switchPerspective(perspective); } }
			 */
			if (projectTypeId.equals("GRAPHIC_PROJECT")) {

				GraphicFormat gf = GraphicFormatFactory.getFormatByName(project.getProjectType());

				project.setSingleFileProject(true);
				project.setOpen(true);

				if (project.getProjectType().endsWith("CHAR")) {
					project.setIconName("icons/chr.png");
				} else if (project.getProjectType().endsWith("SPRITE")) {
					project.setIconName("icons/spr.png");
				} else if (project.getProjectType().endsWith("SCREEN")) {
					project.setIconName("icons/scr.png");
				} else {

				}

				Map<String, Object> projectSetup = new HashMap<String, Object>();
				projectSetup.put("project", project);
				projectSetup.put("gfxFormat", gf);
				int v = SizeVariant.getSizeVariantByName(project.getProjectSubType()).getId();
				projectSetup.put("gfxFormatVariant", v);
				projectSetup.put("isNewProject", true);

				createProjectStructure(project);
				Workspace workspace = Initializer.getConfiguration().getWorkspace();
				workspace.add(project);
				Initializer.getConfiguration().writeWorkspace(workspace);

				MPart part = partService.createPart("de.drazil.nerdsuite.partdescriptor.GfxEditorView");
				part.setLabel(project.getProjectType() + "(" + project.getName() + ")");
				part.setObject(projectSetup);
				part.setElementId(project.getProjectType() + project.getName());
				part.setContributionURI("bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.GfxEditorView");

				List<MPartStack> stacks = modelService.findElements(app, "de.drazil.nerdsuite.partstack.editorStack",
						MPartStack.class, null);
				stacks.get(0).getChildren().add(part);
				partService.showPart(part, PartState.ACTIVATE);

			}

			MPart part = partService.findPart("de.drazil.nerdsuite.part.Explorer");
			Explorer explorer = (Explorer) part.getObject();
			explorer.refresh();

		}
	}

	private void createProjectStructure(Project project) {
		if (project.isSingleFileProject()) {
			String suffix = "";
			if (project.getProjectType().endsWith("CHAR")) {
				suffix = ".ns_chrset";
			} else if (project.getProjectType().endsWith("SPRITE")) {
				suffix = ".ns_sprset";
			} else if (project.getProjectType().endsWith("SCREEN")) {
				suffix = ".ns_scrset";
			}

			File projectFileName = new File(
					Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase() + suffix);
			try {
				projectFileName.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
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
}