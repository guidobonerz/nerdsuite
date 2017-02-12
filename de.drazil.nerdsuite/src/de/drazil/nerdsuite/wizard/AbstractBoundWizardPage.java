package de.drazil.nerdsuite.wizard;

import org.eclipse.jface.wizard.WizardPage;

import de.drazil.nerdsuite.validator.IValidatable;

public abstract class AbstractBoundWizardPage<MODEL> extends WizardPage implements IValidatable
{
	private Class<? extends MODEL> modelClass;
	private MODEL model = null;

	public AbstractBoundWizardPage(String pageName, Class<? extends MODEL> modelClass)
	{
		super(pageName);
		this.modelClass = modelClass;
		setPageComplete(false);
		
	}

	public MODEL getModel()
	{
		if (model == null)
		{
			try
			{
				model = modelClass.newInstance();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return model;
	}

	@Override
	public void setValidated(boolean validated)
	{
		System.out.println(validated);
		setPageComplete(validated);
		if (validated)
		{
			canFlipToNextPage();
		}
	}
}
