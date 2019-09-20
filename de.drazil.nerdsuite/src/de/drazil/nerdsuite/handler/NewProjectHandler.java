package de.drazil.nerdsuite.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.enums.SizeVariant;
import de.drazil.nerdsuite.explorer.Explorer;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.Workspace;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.widget.Tile;
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

				String pt = project.getProjectType();
				GraphicFormat gf = GraphicFormatFactory.getFormatByName(pt);

				project.setSingleFileProject(true);
				project.setOpen(true);

				ProjectType projectType = ProjectType.getProjectTypeById(pt.substring(pt.indexOf('_') + 1));
				project.setIconName(projectType.getIconName());

				Map<String, Object> projectSetup = new HashMap<String, Object>();
				projectSetup.put("project", project);
				projectSetup.put("gfxFormat", gf);
				int v = SizeVariant.getSizeVariantByName(project.getProjectSubType()).getId();
				projectSetup.put("gfxFormatVariant", v);
				projectSetup.put("isNewProject", true);
				projectSetup.put("owner", project.getProjectSubType() + "_" + project.getName());

				File file = createProjectStructure(project, projectType.getSuffix());
				Workspace workspace = Initializer.getConfiguration().getWorkspace();
				workspace.add(project);
				Initializer.getConfiguration().writeWorkspace(workspace);

				MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.GfxEditorView",
						"bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.GfxEditorView", project,
						projectSetup);

				E4Utils.addPart2PartStack(app, modelService, partService, "de.drazil.nerdsuite.partstack.editorStack",
						part, true);

			}

			Explorer explorer = E4Utils.findPartObject(partService, "de.drazil.nerdsuite.part.Explorer",
					Explorer.class);
			explorer.refresh();

		}
	}

	private File createProjectStructure(Project project, String suffix) {
		File file = null;
		if (project.isSingleFileProject()) {

			file = new File(
					Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase() + suffix);
			try {
				file.createNewFile();
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
		return file;
	}
}