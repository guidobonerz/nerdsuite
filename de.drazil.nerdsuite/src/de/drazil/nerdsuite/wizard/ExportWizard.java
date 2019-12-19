package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

public class ExportWizard extends Wizard {
	public static final String PROJECT_TYPE_ID = "PROJECT_TYPE_ID";
	public static final String V_CODING_PROJECT = "CODING_PROJECT";
	public static final String V_GRAPHIC_PROJECT = "GRAPHIC_PROJECT";
	public static final String TARGET_PLATFORM = "TARGET_PLATFORM";
	public static final String PROJECT_ID = "PROJECT_ID";
	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String PROJECT_TYPE = "PROJECT_TYPE";
	public static final String PROJECT_VARIANT = "PROJECT_VARIANT";
	public static final String PROJECT_MAX_ITEMS = "PROJECT_MAX_ITEMS";
	public static final String FILE_NAME = "FILE_NAME";
	public static final String IMPORT_FORMAT = "IMPORT_FORMAT";
	public static final String BYTES_TO_SKIP = "BYTES_TO_SKIP";

	private Map<String, Object> userData;

	public ExportWizard(Map<String, Object> userData) {
		super();
		setWindowTitle("Export to bitmap");
		this.userData = userData;
		this.userData.put("FILE_SELECTION_TITLE", "Select export location");
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void addPages() {
		addPage(new FolderSelectionWizardPage(userData));

	}
}
