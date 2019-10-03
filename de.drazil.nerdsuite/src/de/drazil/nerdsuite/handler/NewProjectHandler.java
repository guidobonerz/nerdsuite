package de.drazil.nerdsuite.handler;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
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

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.enums.SizeVariant;
import de.drazil.nerdsuite.explorer.Explorer;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.CustomFormatDialog;
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

				String pt = project.getProjectType();
				GraphicFormat gf = GraphicFormatFactory.getFormatByName(pt);

				project.setSingleFileProject(true);
				project.setOpen(true);
				LocalDateTime ldt = LocalDateTime.now();
				Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
				project.setCreatedOn(d);
				project.setChangedOn(d);

				ProjectType projectType = ProjectType.getProjectTypeById(pt.substring(pt.indexOf('_') + 1));
				project.setIconName(projectType.getIconName());

				int formatVariant = SizeVariant.getSizeVariantByName(project.getProjectSubType()).getId();

				if (project.getProjectSubType().equalsIgnoreCase("CUSTOM")) {
					CustomFormatDialog cfd = new CustomFormatDialog(shell);
					cfd.open(gf.getVariants().get(formatVariant));
				}

				Map<String, Object> projectSetup = new HashMap<String, Object>();
				projectSetup.put("project", project);
				projectSetup.put("gfxFormat", gf);
				projectSetup.put("gfxFormatVariant", formatVariant);
				projectSetup.put("isNewProject", true);
				projectSetup.put("owner",
						project.getProjectType() + "_" + project.getProjectSubType() + "_" + project.getName());

				// File file = createProjectStructure(project, projectType.getSuffix());

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

		File projectFolder = new File(
				Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase());
		projectFolder.mkdir();

		for (ProjectFolder folder : project.getFolderList()) {
			File subfolder = new File(
					projectFolder.getAbsolutePath() + Constants.FILE_SEPARATOR + folder.getName().toLowerCase());
			subfolder.mkdir();
		}

		return null;
	}
}