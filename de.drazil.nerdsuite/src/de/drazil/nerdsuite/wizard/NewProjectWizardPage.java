package de.drazil.nerdsuite.wizard;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.ProjectType;
import de.drazil.nerdsuite.model.TargetPlatform;
import de.drazil.nerdsuite.util.WidgetDataBinder;
import de.drazil.nerdsuite.validator.DuplicateNameValidator;
import de.drazil.nerdsuite.validator.IValidatable;
import de.drazil.nerdsuite.validator.LengthValidator;
import de.drazil.nerdsuite.validator.LengthValidator.CheckType;
import de.drazil.nerdsuite.widget.ProjectTypeFactory;
import lombok.Getter;
import lombok.Setter;

public class NewProjectWizardPage extends AbstractBoundWizardPage<Project> {

	private Label projectNameLabel;
	private Label targetPlatformLabel;
	private Label separatorLabel;
	private Label basePathLabel;
	private Label sourcePathLabel;
	private Label binaryPathLabel;
	private Label includePathLabel;
	private Label symbolPathLabel;
	private Text projectNameText;
	private Text basePathText;
	private Text sourcePathText;
	private Text binaryPathText;
	private Text includePathText;
	private Text symbolPathText;
	private Button createExampleButton;
	private ComboViewer targetPlatformCombo;
	private ProjectType projectType;
	private List<TargetPlatform> targetPlatformnList;

	/**
	 * Create the wizard.
	 */
	public NewProjectWizardPage(String projectTypeId) {
		super("wizardPage", Project.class);
		projectType = ProjectTypeFactory.getProjectTypeByName(projectTypeId);
		setTitle("Create new " + projectType.getName());
		setDescription("Please enter a Project Name");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new FormLayout());

		FormData formData;

		projectNameLabel = new Label(container, SWT.NONE);
		projectNameLabel.setText("Project Name");
		targetPlatformLabel = new Label(container, SWT.NONE);
		targetPlatformLabel.setText("Target Platform");
		separatorLabel = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		basePathLabel = new Label(container, SWT.NONE);
		basePathLabel.setText("Base Path");
		sourcePathLabel = new Label(container, SWT.NONE);
		sourcePathLabel.setText("Source Path");
		binaryPathLabel = new Label(container, SWT.NONE);
		binaryPathLabel.setText("Binary Path");
		includePathLabel = new Label(container, SWT.NONE);
		includePathLabel.setText("Include Path");
		symbolPathLabel = new Label(container, SWT.NONE);
		symbolPathLabel.setText("Symbol Path");

		projectNameText = new Text(container, SWT.BORDER);
		basePathText = new Text(container, SWT.BORDER);
		basePathText.setEditable(false);
		sourcePathText = new Text(container, SWT.BORDER);
		binaryPathText = new Text(container, SWT.BORDER);
		includePathText = new Text(container, SWT.BORDER);
		symbolPathText = new Text(container, SWT.BORDER);
		targetPlatformCombo = new ComboViewer(container, SWT.NONE);
		targetPlatformCombo.setContentProvider(ArrayContentProvider.getInstance());
		targetPlatformCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof TargetPlatform) {
					TargetPlatform current = (TargetPlatform) element;

					return current.getName();
				}
				return super.getText(element);
			}
		});

		targetPlatformnList = getTargetPlatFormList();
		TargetPlatform tp[] = new TargetPlatform[targetPlatformnList.size()];
		tp = targetPlatformnList.toArray(tp);
		targetPlatformCombo.setInput(tp);
		targetPlatformCombo.getCombo().select(0);
		createExampleButton = new Button(container, SWT.CHECK);
		createExampleButton.setText("Create Example SourceFile");

		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(container, 0);
		projectNameLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(projectNameLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameLabel, 30);
		formData.right = new FormAttachment(projectNameLabel, 300);
		projectNameText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(projectNameLabel, 10);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		targetPlatformLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(targetPlatformLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameText, 0, SWT.LEFT);
		formData.right = new FormAttachment(projectNameText, 0, SWT.RIGHT);
		targetPlatformCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(targetPlatformCombo.getControl(), 5);
		formData.left = new FormAttachment(targetPlatformCombo.getControl(), 0, SWT.LEFT);
		createExampleButton.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(createExampleButton, 5);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		separatorLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(separatorLabel, 10);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		basePathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(basePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameText, 0, SWT.LEFT);
		formData.right = new FormAttachment(projectNameText, 0, SWT.RIGHT);
		basePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(basePathLabel, 10);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		sourcePathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(sourcePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameText, 0, SWT.LEFT);
		formData.right = new FormAttachment(projectNameText, 0, SWT.RIGHT);
		sourcePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(sourcePathLabel, 10);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		binaryPathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(binaryPathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameText, 0, SWT.LEFT);
		formData.right = new FormAttachment(projectNameText, 0, SWT.RIGHT);
		binaryPathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(binaryPathLabel, 10);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		includePathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(includePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameText, 0, SWT.LEFT);
		formData.right = new FormAttachment(projectNameText, 0, SWT.RIGHT);
		includePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(includePathLabel, 10);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		symbolPathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(symbolPathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(projectNameText, 0, SWT.LEFT);
		formData.right = new FormAttachment(projectNameText, 0, SWT.RIGHT);
		symbolPathText.setLayoutData(formData);

		initDataBindings();
	}

	protected void initDataBindings() {
		getModel().add(new ProjectFolder(Constants.SOURCE_FOLDER, Constants.DEFAULT_SOURCE_PATH));
		getModel().add(new ProjectFolder(Constants.BINARY_FOLDER, Constants.DEFAULT_BINARY_PATH));
		getModel().add(new ProjectFolder(Constants.INCLUDE_FOLDER, Constants.DEFAULT_INCLUDE_PATH));
		getModel().add(new ProjectFolder(Constants.SYMBOL_FOLDER, Constants.DEFAULT_SYMBOL_PATH));

		Map<String, ProjectFolder> folderMap = getModel().getProjectFolderMap();

		final WidgetDataBinder widgetDataBinder = new WidgetDataBinder(this);
		widgetDataBinder.bind(projectNameText, getModel(), Constants.NAME,
				new LengthValidator("Projectname", 1, CheckType.Min));
		widgetDataBinder.bind(projectNameText, getModel(), Constants.NAME, new DuplicateNameValidator<Project>(
				"Projectname", Initializer.getConfiguration().getWorkspace().getProjectMap()));
		widgetDataBinder.bind(sourcePathText, folderMap.get("src"), Constants.NAME,
				new LengthValidator("Sourcefolder Name", 1, CheckType.Min));
		widgetDataBinder.bind(binaryPathText, folderMap.get("bin"), Constants.NAME,
				new LengthValidator("Binaryfolder Name", 1, CheckType.Min));
		widgetDataBinder.bind(includePathText, folderMap.get("include"), Constants.NAME,
				new LengthValidator("Includefolder Name", 1, CheckType.Min));
		widgetDataBinder.bind(symbolPathText, folderMap.get("symbol"), Constants.NAME,
				new LengthValidator("Symbolfolder Name", 1, CheckType.Min));
		IObservableCollection oc = widgetDataBinder.getDataBindingContext().getBindings();
		AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(oc,
				AggregateValidationStatus.MAX_SEVERITY);
		aggregateValidationStatus.addChangeListener(new AggregatedValidationChangeListener(oc, this));
	}

	private static class AggregatedValidationChangeListener implements IChangeListener {
		@Setter
		@Getter
		private IObservableCollection oc;

		@Setter
		@Getter
		IValidatable validatable;

		public AggregatedValidationChangeListener(IObservableCollection oc, IValidatable validatable) {
			setOc(oc);
			setValidatable(validatable);
		}

		@Override
		public void handleChange(ChangeEvent event) {
			AggregateValidationStatus as = (AggregateValidationStatus) event.getSource();
			// to assure that the changeevent is thrown every time
			as.getValue();
			validatable.setValidated(AggregateValidationStatus.getStatusMerged(getOc()).isOK());
		}
	}

	
	
	private List<TargetPlatform> getTargetPlatFormList() {
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		ObjectMapper mapper = new ObjectMapper();
		try {
			targetPlatformnList = Arrays
					.asList(mapper.readValue(bundle.getEntry("configuration/platform.json"), TargetPlatform[].class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetPlatformnList;
	}
}
