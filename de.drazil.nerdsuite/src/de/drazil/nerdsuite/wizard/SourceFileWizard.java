package de.drazil.nerdsuite.wizard;

import javax.inject.Inject;

import org.eclipse.jface.wizard.Wizard;

public class SourceFileWizard extends Wizard
{
	private NewSourceFileWizardPage newSourceFileWizardPage = null;

	@Inject
	public SourceFileWizard()
	{
		super();
		newSourceFileWizardPage = new NewSourceFileWizardPage();
	}

	@Override
	public boolean performFinish()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addPages()
	{
		addPage(newSourceFileWizardPage);
	}

}
