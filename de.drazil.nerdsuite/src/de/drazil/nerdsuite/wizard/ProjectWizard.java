package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import de.drazil.nerdsuite.enums.WizardType;

public class ProjectWizard extends Wizard {
	public static final String PROJECT_TYPE_ID = "PROJECT_TYPE_ID";
	public static final String V_CODING_PROJECT = "CODING_PROJECT";
	public static final String V_GRAPHIC_PROJECT = "GRAPHIC_PROJECT";
	public static final String TARGET_PLATFORM = "TARGET_PLATFORM";
	public static final String PROJECT_ID = "PROJECT_ID";
	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String PROJECT_TYPE = "PROJECT_TYPE";
	public static final String PROJECT_VARIANT = "PROJECT_VARIANT";
	public static final String FILE_NAME = "FILE_NAME";
	

	private WizardType wizardType;
	private Map<String, Object> userData;

	public ProjectWizard(WizardType wizardType, String title, Map<String, Object> userData) {
		super();
		setWindowTitle(title);
		this.wizardType = wizardType;
		this.userData = userData;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void addPages() {
		if (wizardType == WizardType.ImportAsNewProject) {
			addPage(new FileSelectionWizardPage(userData));
		}
		if (userData.get(PROJECT_TYPE_ID).equals(V_CODING_PROJECT)) {
			addPage(new CodingProjectWizardPage(userData));
		} else if (userData.get(PROJECT_TYPE_ID).equals(V_GRAPHIC_PROJECT)) {
			addPage(new GraphicsProjectWizardPage(userData));
		}
	}
}
