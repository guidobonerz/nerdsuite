package de.drazil.nerdsuite.wizard;

import lombok.Getter;
import lombok.Setter;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.ProjectFile;
import de.drazil.nerdsuite.util.WidgetDataBinder;
import de.drazil.nerdsuite.validator.DuplicateNameValidator;
import de.drazil.nerdsuite.validator.IValidatable;
import de.drazil.nerdsuite.validator.LengthValidator;
import de.drazil.nerdsuite.validator.LengthValidator.CheckType;

public class NewSourceFileWizardPage extends AbstractBoundWizardPage<ProjectFile>
{

	private Label sourceFileNameLabel;
	private Text sourceFileNameText;

	public NewSourceFileWizardPage()
	{
		super("wizardPage", ProjectFile.class);
		setTitle("Create new Source File");
		setDescription("Please enter a filename");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new FormLayout());

		FormData formData;

		sourceFileNameLabel = new Label(container, SWT.NONE);
		sourceFileNameLabel.setText("Filename");
		sourceFileNameText = new Text(container, SWT.BORDER);

		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(container, 0);
		sourceFileNameLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(sourceFileNameLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(sourceFileNameLabel, 30);
		formData.right = new FormAttachment(sourceFileNameLabel, 300);
		sourceFileNameText.setLayoutData(formData);

		initDataBindings();
	}

	protected void initDataBindings()
	{
		// getModel().add(new ProjectFolder(Constants.SOURCE_FOLDER,
		// Constants.DEFAULT_SOURCE_PATH));
		// getModel().add(new ProjectFolder(Constants.BINARY_FOLDER,
		// Constants.DEFAULT_BINARY_PATH));
		// getModel().add(new ProjectFolder(Constants.INCLUDE_FOLDER,
		// Constants.DEFAULT_INCLUDE_PATH));
		// getModel().add(new ProjectFolder(Constants.SYMBOL_FOLDER,
		// Constants.DEFAULT_SYMBOL_PATH));

		// Map<String, ProjectFolder> folderMap = getModel().getProjectFolderMap();

		final WidgetDataBinder widgetDataBinder = new WidgetDataBinder(this);
		widgetDataBinder.bind(sourceFileNameText, getModel(), Constants.NAME, new LengthValidator("Filename", 1, CheckType.Min));
		widgetDataBinder.bind(sourceFileNameText, getModel(), Constants.NAME, new DuplicateNameValidator("Filename", null));
		IObservableCollection oc = widgetDataBinder.getDataBindingContext().getBindings();
		AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(oc, AggregateValidationStatus.MAX_SEVERITY);
		aggregateValidationStatus.addChangeListener(new AggregatedValidationChangeListener(oc, this));
	}

	private static class AggregatedValidationChangeListener implements IChangeListener
	{
		@Setter
		@Getter
		private IObservableCollection oc;

		@Setter
		@Getter
		IValidatable validatable;

		public AggregatedValidationChangeListener(IObservableCollection oc, IValidatable validatable)
		{
			setOc(oc);
			setValidatable(validatable);
		}

		@Override
		public void handleChange(ChangeEvent event)
		{
			AggregateValidationStatus as = (AggregateValidationStatus) event.getSource();
			// to assure that the changeevent is thrown every time
			as.getValue();
			validatable.setValidated(AggregateValidationStatus.getStatusMerged(getOc()).isOK());
		}
	}
}
