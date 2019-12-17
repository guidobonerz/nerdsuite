package de.drazil.nerdsuite.handler;

import java.io.File;
import java.text.DateFormat;
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
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.enums.WizardType;
import de.drazil.nerdsuite.explorer.Explorer;
import de.drazil.nerdsuite.imaging.service.ImportService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.CustomFormatDialog;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class NewProjectHandler {

	@Execute
	public void execute(MPerspective activePerspective, MApplication app, IWorkbench workbench, Shell shell,
			EPartService partService, EModelService modelService,
			@Named("de.drazil.nerdsuite.commandparameter.ProjectTypeId") String projectTypeId,
			@Named("de.drazil.nerdsuite.commandparameter.WizardType") String wizardType) {

		Map<String, Object> userData = new HashMap<>();
		userData.put(ProjectWizard.PROJECT_TYPE_ID, projectTypeId);

		ProjectWizard projectWizard = new ProjectWizard(WizardType.getWizardTypeById(Integer.valueOf(wizardType)),
				userData);
		WizardDialog wizardDialog = new WizardDialog(shell, projectWizard);

		if (wizardDialog.open() == WizardDialog.OK) {

			String projectId = (String) userData.get(ProjectWizard.PROJECT_ID);
			String projectName = (String) userData.get(ProjectWizard.PROJECT_NAME);

			Project project = new Project();
			project.setId(projectId);
			project.setName(projectName);

			if (projectTypeId.equals("GRAPHIC_PROJECT")) {

				String type = (String) userData.get(ProjectWizard.PROJECT_TYPE);
				String type2 = type.substring(type.indexOf('_') + 1);

				ProjectMetaData metadata = new ProjectMetaData();
				metadata.setPlatform((String) userData.get(ProjectWizard.TARGET_PLATFORM));
				metadata.setType(type2);
				metadata.setVariant((String) userData.get(ProjectWizard.PROJECT_VARIANT));

				GraphicFormat gf = GraphicFormatFactory.getFormatById(type);
				GraphicFormatVariant gfv = GraphicFormatFactory.getFormatVariantById(type, metadata.getVariant());
				project.setSingleFileProject(true);
				project.setOpen(true);
				LocalDateTime ldt = LocalDateTime.now();
				Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
				project.setCreatedOn(d);
				project.setChangedOn(d);

				ProjectType projectType = ProjectType.getProjectTypeById(type2);
				project.setIconName(projectType.getIconName());
				project.setSuffix(projectType.getSuffix());

				metadata.setWidth(gf.getWidth());
				metadata.setHeight(gf.getHeight());
				metadata.setRows(gfv.getTileRows());
				metadata.setColumns(gfv.getTileColumns());
				metadata.setStorageEntity(gf.getStorageEntity());

				String owner = (String) userData.get(ProjectWizard.PROJECT_TYPE) + "_"
						+ (String) userData.get(ProjectWizard.PROJECT_VARIANT) + "_"
						+ (String) userData.get(ProjectWizard.PROJECT_ID);

				Map<String, Object> projectSetup = new HashMap<String, Object>();
				projectSetup.put("project", project);
				projectSetup.put("importFileName", (String) userData.get(ProjectWizard.FILE_NAME));
				projectSetup.put("importFormat", (String) userData.get(ProjectWizard.IMPORT_FORMAT));
				projectSetup.put("bytesToSkip", (Integer) userData.get(ProjectWizard.BYTES_TO_SKIP));
				String projectAction = projectSetup.get("importFileName") == null ? "newProjectAction"
						: "newImportProjectAction";
				projectSetup.put("projectAction", projectAction);

				if (metadata.getVariant().equalsIgnoreCase("CUSTOM")) {
					CustomFormatDialog cfd = new CustomFormatDialog(shell);
					cfd.open(metadata, gfv.isSupportCustomBaseSize());
				}

				if (projectAction.startsWith("new")) {
					File file = new File(Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR
							+ project.getId().toLowerCase() + "." + projectType.getSuffix());
					projectSetup.put("file", file);
					TileRepositoryService repository = ServiceFactory.getService(owner, TileRepositoryService.class);
					repository.setMetadata(metadata);

					projectSetup.put("repository", repository);
					if (projectAction.startsWith("newImport")) {
						ImportService importService = ServiceFactory.getCommonService(ImportService.class);
						importService.doImportGraphic(projectSetup);
						TileRepositoryService.save(file, repository, project);
					} else {
						int maxItems = (int) userData.get(ProjectWizard.PROJECT_MAX_ITEMS);
						repository.setInitialSize(maxItems);
					}
					Initializer.getConfiguration().updateWorkspace(project, file, true);
				} else {
					throw new IllegalArgumentException("No such project action.");
				}

				// File file = createProjectStructure(project, projectType.getSuffix());

				MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.GfxEditorView",
						"bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.GfxEditorView", owner,
						project.getName(), projectSetup);

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