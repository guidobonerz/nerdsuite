package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

public class MountFileWizard extends Wizard {

	private Map<String, Object> userData;

	public MountFileWizard(Map<String, Object> userData) {
		super();
		setWindowTitle("Mount file");
		this.userData = userData;
		this.userData.put("FILE_SELECTION_TITLE", "Select a file you wish to mount");
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void addPages() {
		addPage(new FileSelectionWizardPage(userData));
	}
}
