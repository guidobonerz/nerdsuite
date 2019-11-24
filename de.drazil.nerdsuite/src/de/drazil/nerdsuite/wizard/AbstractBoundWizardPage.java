package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;

import de.drazil.nerdsuite.validator.IValidatable;

public abstract class AbstractBoundWizardPage extends WizardPage implements IValidatable {
	protected Map<String, Object> userData;

	public AbstractBoundWizardPage(String pageName, Map<String, Object> userData) {
		super(pageName);
		this.userData = userData;
		setPageComplete(false);
	}

	@Override
	public void setValidated(boolean validated) {

		setPageComplete(validated);
		if (validated) {
			canFlipToNextPage();
		}
	}
}
