
package de.drazil.nerdsuite.handler;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.enums.ProjectType;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.wizard.MountFileWizard;
import de.drazil.nerdsuite.wizard.ProjectWizard;

public class MountFileHandler {
	@Execute
	public void execute(Shell shell, IEventBroker broker) {
		Map<String, Object> userData = new HashMap<>();
		MountFileWizard mountFileWizard = new MountFileWizard(userData);
		WizardDialog wizardDialog = new WizardDialog(shell, mountFileWizard);

		if (wizardDialog.open() == WizardDialog.OK) {
			File mountFile = new File((String) userData.get(ProjectWizard.FILE_NAME));
			System.out.println(mountFile);
			Project project = new Project();
			project.setId(mountFile.getName().toUpperCase());
			project.setName(mountFile.getName().toUpperCase());
			project.setSingleFileProject(true);
			project.setOpen(true);
			project.setMountpoint(true);
			project.setMountLocation(String.format("file@%s", mountFile.getAbsolutePath()));
			project.setIconName(ProjectType.getProjectTypeById("MOUNT_POINT").getIconName());
			LocalDateTime ldt = LocalDateTime.now();
			Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			project.setCreatedOn(d);
			project.setChangedOn(d);
			Initializer.getConfiguration().updateWorkspace(project, true, true);
			broker.send("explorer/refresh", null);
		}
	}

}