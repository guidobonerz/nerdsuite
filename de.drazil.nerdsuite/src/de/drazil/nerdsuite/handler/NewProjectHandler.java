package de.drazil.nerdsuite.handler;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.enums.WizardType;
import de.drazil.nerdsuite.imaging.service.ImportService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class NewProjectHandler {

	@Execute
	public void execute(MPerspective activePerspective, MApplication app, Shell shell, EPartService partService, EModelService modelService,
			@Named("de.drazil.nerdsuite.commandparameter.ProjectTypeId") String projectTypeId, @Named("de.drazil.nerdsuite.commandparameter.WizardType") String wizardType, IEventBroker broker) {

		Map<String, Object> userData = new HashMap<>();
		userData.put(ProjectWizard.PROJECT_TYPE_ID, projectTypeId);

		ProjectWizard projectWizard = new ProjectWizard(WizardType.getWizardTypeById(Integer.valueOf(wizardType)), userData);
		WizardDialog wizardDialog = new WizardDialog(shell, projectWizard);

		if (wizardDialog.open() == WizardDialog.OK) {

			String projectId = (String) userData.get(ProjectWizard.PROJECT_ID);
			String projectName = (String) userData.get(ProjectWizard.PROJECT_NAME);
			String targetPlatform = (String) userData.get(ProjectWizard.TARGET_PLATFORM);
			String type = (String) userData.get(ProjectWizard.PROJECT_TYPE);
			String subType = type.substring(type.indexOf('_') + 1);
			String variant = (String) userData.get(ProjectWizard.PROJECT_VARIANT);
			Project project = new Project();
			project.setId(projectId);
			project.setName(projectName);

			String owner = String.format("%s_%s_%s", type, variant, projectId);

			Map<String, Object> projectSetup = new HashMap<String, Object>();
			projectSetup.put("project", project);
			projectSetup.put("fileName", (String) userData.get(ProjectWizard.FILE_NAME));
			LocalDateTime ldt = LocalDateTime.now();
			Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			project.setCreatedOn(d);
			project.setChangedOn(d);
			ProjectType projectType = ProjectType.getProjectTypeById(subType);
			project.setIconName(projectType.getIconName());
			project.setSuffix(projectType.getSuffix());

			if (projectTypeId.equals("GRAPHIC_PROJECT")) {

				projectSetup.put("importFormat", (String) userData.get(ProjectWizard.IMPORT_FORMAT));
				projectSetup.put("bytesToSkip", (Integer) userData.get(ProjectWizard.BYTES_TO_SKIP));
				String projectAction = projectSetup.get("fileName") == null ? "newProjectAction" : "newImportProjectAction";
				projectSetup.put("projectAction", projectAction);

				GraphicFormat gf = GraphicFormatFactory.getFormatById(type);
				GraphicFormatVariant gfv = GraphicFormatFactory.getFormatVariantById(type, variant);
				project.setSingleFileProject(true);
				project.setOpen(true);

				ProjectMetaData metadata = new ProjectMetaData();

				metadata.setPlatform(targetPlatform);
				metadata.setType(subType);
				metadata.setVariant(variant);
				metadata.init((Integer) userData.get("width"), (Integer) userData.get("height"), (Integer) userData.get("columns"), (Integer) userData.get("rows"), gf.getStorageSize(), false);

				if (projectAction.startsWith("new")) {
					TileRepositoryService repository = ServiceFactory.getService(projectId, TileRepositoryService.class);
					repository.setMetadata(metadata);
					if (metadata.getType().equals("PETSCII") || metadata.getType().equals("SCREENSET")) {
						String id = "C64_UPPER";
						repository.getMetadata().setReferenceId(id);
						if (!ServiceFactory.checkService(id)) {
							TileRepositoryService referenceRepository = ServiceFactory.getService(id, TileRepositoryService.class);
							referenceRepository.load(id, true);
						}
						metadata.setBlankValue(gf.getBlankValue());
					}

					projectSetup.put("repositoryOwner", projectId);

					if (projectAction.startsWith("newImport")) {
						ImportService importService = ServiceFactory.getCommonService(ImportService.class);
						importService.doImportGraphic(projectSetup);
					} else {
						int maxItems = (int) userData.get(ProjectWizard.PROJECT_MAX_ITEMS);
						repository.init(maxItems);
					}
					repository.save(project);
					Initializer.getConfiguration().updateWorkspace(project, true, false);
				} else {
					throw new IllegalArgumentException("No such project action.");
				}

				// File file = createProjectStructure(project, projectType.getSuffix());

				String editorView = "bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.GfxEditorView";

				MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.GfxEditorView", editorView, owner, project.getName(), projectSetup);

				E4Utils.addPart2PartStack(app, modelService, partService, "de.drazil.nerdsuite.partstack.editorStack", part, true);
			} else if (projectTypeId.equals("CODING_PROJECT")) {

				MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.SourceEditorView",
						"bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.sourceeditor.SourceEditorView", owner, project.getName(), projectSetup);

				E4Utils.addPart2PartStack(app, modelService, partService, "de.drazil.nerdsuite.partstack.editorStack", part, true);
			}

			broker.send("explorer/refresh", null);
		}
	}

	private File createProjectStructure(Project project, String suffix) {

		File projectFolder = new File(Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + project.getId().toLowerCase());
		projectFolder.mkdir();

		for (ProjectFolder folder : project.getFolderList()) {
			File subfolder = new File(projectFolder.getAbsolutePath() + Constants.FILE_SEPARATOR + folder.getName().toLowerCase());
			subfolder.mkdir();
		}

		return null;
	}
}