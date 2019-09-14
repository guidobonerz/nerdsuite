package de.drazil.nerdsuite.wizard;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import de.drazil.nerdsuite.model.ProgrammingLanguage;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.model.SimpleEntity;
import de.drazil.nerdsuite.model.TargetPlatform;
import de.drazil.nerdsuite.validator.IValidatable;
import de.drazil.nerdsuite.widget.ProjectTypeFactory;
import lombok.Getter;
import lombok.Setter;

public class CodingProjectWizardPage extends AbstractBoundWizardPage<Project> {

	private boolean enableAssembler = false;
	private Label projectNameLabel;
	private Label targetPlatformLabel;
	private Label languageTypeLabel;
	private Label assemblerLabel;
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
	private ComboViewer targetPlatformCombo;
	private ComboViewer languageTypeCombo;
	private ComboViewer assemblerCombo;
	private Button createExampleButton;
	private SimpleEntity projectType;
	private List<TargetPlatform> targetPlatformList;
	private List<ProgrammingLanguage> programmingLanguageList;

	/**
	 * Create the wizard.
	 */
	public CodingProjectWizardPage(String projectTypeId) {
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

		List<TargetPlatform> targetPlatformList = getTargetPlatFormList();
		getModel().setTargetPlatform(targetPlatformList.get(0).getId());
		List<ProgrammingLanguage> programmingLanguageList = getProgrammingLanguageList(targetPlatformList.get(0));
		getModel().setProjectType(programmingLanguageList.get(0).getId());

		projectNameLabel = new Label(container, SWT.NONE);
		projectNameLabel.setText("Project Name");
		targetPlatformLabel = new Label(container, SWT.NONE);
		targetPlatformLabel.setText("Target Platform");
		languageTypeLabel = new Label(container, SWT.NONE);
		languageTypeLabel.setText("Coding Language");
		assemblerLabel = new Label(container, SWT.NONE);
		assemblerLabel.setText("Assembler/Compiler");
		assemblerLabel.setEnabled(enableAssembler);

		createExampleButton = new Button(container, SWT.CHECK);
		createExampleButton.setText("Create example file");
		createExampleButton.setEnabled(programmingLanguageList.get(0).isSupportsExampleFile());

		projectNameText = new Text(container, SWT.BORDER);
		targetPlatformCombo = new ComboViewer(container, SWT.NONE);
		targetPlatformCombo.setContentProvider(ArrayContentProvider.getInstance());
		targetPlatformCombo.setInput(targetPlatformList);
		targetPlatformCombo.setSelection(new StructuredSelection(targetPlatformList.get(0)));
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
		targetPlatformCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				TargetPlatform targetPlatform = (TargetPlatform) selection.getFirstElement();

				List<ProgrammingLanguage> l = getProgrammingLanguageList(targetPlatform);
				languageTypeCombo.setInput(l);
				if (l.size() > 0) {
					languageTypeCombo.getCombo().setEnabled(true);
					languageTypeCombo.setSelection(new StructuredSelection(l.get(0)));
				} else {
					languageTypeCombo.getCombo().setEnabled(false);
				}
			}
		});

		languageTypeCombo = new ComboViewer(container, SWT.NONE);
		languageTypeCombo.setContentProvider(ArrayContentProvider.getInstance());
		languageTypeCombo.setInput(programmingLanguageList);
		languageTypeCombo.setSelection(new StructuredSelection(programmingLanguageList.get(0)));
		languageTypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ProgrammingLanguage) {
					ProgrammingLanguage current = (ProgrammingLanguage) element;
					return current.getName();
				}
				return super.getText(element);
			}
		});
		languageTypeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				ProgrammingLanguage subType = (ProgrammingLanguage) selection.getFirstElement();
				createExampleButton.setEnabled(subType.isSupportsExampleFile());
				getModel().setProjectType(subType.getId());
			}
		});

		assemblerCombo = new ComboViewer(container, SWT.NONE);
		assemblerCombo.getCombo().setEnabled(enableAssembler);
		assemblerCombo.setContentProvider(ArrayContentProvider.getInstance());
		// assemblerCombo.setInput(codingLanguage);
		// assemblerCombo.setSelection(new StructuredSelection(codingLanguage.get(0)));
		assemblerCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SimpleEntity) {
					SimpleEntity current = (SimpleEntity) element;

					return current.getName();
				}
				return super.getText(element);
			}
		});
		/*
		 * assemblerCombo.addSelectionChangedListener(new ISelectionChangedListener() {
		 * 
		 * @Override public void selectionChanged(SelectionChangedEvent event) {
		 * StructuredSelection selection = (StructuredSelection) event.getSelection();
		 * SimpleEntity subType = (SimpleEntity) selection.getFirstElement();
		 * getModel().setProjectType(subType.getId()); } });
		 */

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

		basePathText = new Text(container, SWT.BORDER);
		basePathText.setEditable(false);
		sourcePathText = new Text(container, SWT.BORDER);
		binaryPathText = new Text(container, SWT.BORDER);
		includePathText = new Text(container, SWT.BORDER);
		symbolPathText = new Text(container, SWT.BORDER);

		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(container, 0);
		projectNameLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(projectNameLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(projectNameLabel, 0, SWT.LEFT);
		targetPlatformLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(targetPlatformLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(targetPlatformLabel, 0, SWT.LEFT);
		languageTypeLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(languageTypeLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(languageTypeLabel, 0, SWT.LEFT);
		assemblerLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(projectNameLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		projectNameText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(targetPlatformLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		targetPlatformCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(languageTypeLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		languageTypeCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(assemblerLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		assemblerCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(assemblerCombo.getControl(), 10, SWT.BOTTOM);
		formData.left = new FormAttachment(assemblerCombo.getControl(), 0, SWT.LEFT);
		createExampleButton.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(createExampleButton, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(140, 0);
		separatorLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(separatorLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(separatorLabel, 0, SWT.LEFT);
		basePathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(basePathLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(basePathLabel, 0, SWT.LEFT);
		sourcePathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(sourcePathLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(sourcePathLabel, 0, SWT.LEFT);
		binaryPathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(binaryPathLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(binaryPathLabel, 0, SWT.LEFT);
		includePathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(includePathLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(includePathLabel, 0, SWT.LEFT);
		symbolPathLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(basePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		basePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(sourcePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		sourcePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(binaryPathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		binaryPathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(includePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		includePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(symbolPathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		symbolPathText.setLayoutData(formData);

		initDataBindings();
	}

	protected void initDataBindings() {
		getModel().getFolderList().add(new ProjectFolder(Constants.SOURCE_FOLDER, Constants.DEFAULT_SOURCE_PATH));
		getModel().getFolderList().add(new ProjectFolder(Constants.BINARY_FOLDER, Constants.DEFAULT_BINARY_PATH));
		getModel().getFolderList().add(new ProjectFolder(Constants.INCLUDE_FOLDER, Constants.DEFAULT_INCLUDE_PATH));
		getModel().getFolderList().add(new ProjectFolder(Constants.SYMBOL_FOLDER, Constants.DEFAULT_SYMBOL_PATH));

		List<ProjectFolder> folderList = getModel().getFolderList();

		ISWTObservableValue projectNameObservable = WidgetProperties.text(SWT.Modify).observe(projectNameText);

		ISideEffect.create(() -> {
			Object value = projectNameObservable.getValue();
			getModel().setName((String) value);
		});

		/*
		 * final WidgetDataBinder widgetDataBinder = new WidgetDataBinder(this);
		 * widgetDataBinder.bind(projectNameText, getModel(), Constants.NAME, new
		 * LengthValidator("Projectname", 1, CheckType.Min));
		 * widgetDataBinder.bind(projectNameText, getModel(), Constants.NAME, new
		 * DuplicateNameValidator<Project>( "Projectname",
		 * Initializer.getConfiguration().getWorkspace().getProjects()) {
		 * 
		 * @Override protected boolean exists(List<Project> list, Object value) {
		 * boolean exists = false; if (value instanceof String && value != null) {
		 * exists = list.stream().filter(c ->
		 * c.getName().equals(Constants.NAME)).findFirst().isPresent(); } return exists;
		 * } }); widgetDataBinder.bind(sourcePathText, getFolder(folderList,
		 * Constants.SOURCE_FOLDER).get().getName(), Constants.NAME, new
		 * LengthValidator("Sourcefolder Name", 1, CheckType.Min));
		 * widgetDataBinder.bind(binaryPathText, getFolder(folderList,
		 * Constants.BINARY_FOLDER).get().getName(), Constants.NAME, new
		 * LengthValidator("Binaryfolder Name", 1, CheckType.Min));
		 * widgetDataBinder.bind(includePathText, getFolder(folderList,
		 * Constants.INCLUDE_FOLDER).get().getName(), Constants.NAME, new
		 * LengthValidator("Includefolder Name", 1, CheckType.Min));
		 * widgetDataBinder.bind(symbolPathText, getFolder(folderList,
		 * Constants.SYMBOL_FOLDER).get().getName(), Constants.NAME, new
		 * LengthValidator("Symbolfolder Name", 1, CheckType.Min));
		 * IObservableCollection oc =
		 * widgetDataBinder.getDataBindingContext().getBindings();
		 * AggregateValidationStatus aggregateValidationStatus = new
		 * AggregateValidationStatus(oc, AggregateValidationStatus.MAX_SEVERITY);
		 * aggregateValidationStatus.addChangeListener(new
		 * AggregatedValidationChangeListener(oc, this));
		 */
	}

	private Optional<ProjectFolder> getFolder(List<ProjectFolder> folderList, String folderName) {
		return folderList.stream().filter(c -> c.getId().equals(folderName)).findFirst();
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
			targetPlatformList = Arrays
					.asList(mapper.readValue(bundle.getEntry("configuration/platform.json"), TargetPlatform[].class))
					.stream().filter(c -> c.isEnabled()).collect(Collectors.toList());
		} catch (Exception e) {
			targetPlatformList = null;
		}
		return targetPlatformList;
	}

	private List<ProgrammingLanguage> getProgrammingLanguageList(TargetPlatform targetPlatform) {
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		ObjectMapper mapper = new ObjectMapper();
		try {
			programmingLanguageList = Arrays
					.asList(mapper.readValue(bundle.getEntry("configuration/programming_languages.json"),
							ProgrammingLanguage[].class))
					.stream().filter(c -> c.getId().startsWith(targetPlatform.getId())).collect(Collectors.toList());
		} catch (Exception e) {
			programmingLanguageList = null;

		}
		return programmingLanguageList;
	}

	@Override
	public boolean isPageComplete() {
		getModel().setId(projectNameText.getText().toUpperCase());
		getModel().setName(projectNameText.getText());
		return true;
	}

}
