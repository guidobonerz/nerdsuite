package de.drazil.nerdsuite.wizard;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.model.SimpleEntity;
import de.drazil.nerdsuite.model.TargetPlatform;
import de.drazil.nerdsuite.widget.GraphicFormatFactory;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.ProjectTypeFactory;

public class GraphicsProjectWizardPage extends AbstractBoundWizardPage {

	private Label projectNameLabel;
	private Label targetPlatformLabel;
	private Label gfxFormatLabel;
	private Label gfxFormatVariantLabel;
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
	private ComboViewer gfxFormatCombo;
	private ComboViewer gfxFormatVariantCombo;
	private SimpleEntity projectType;

	/**
	 * Create the wizard.
	 */
	public GraphicsProjectWizardPage(Map<String, Object> userData) {
		super("wizardPage", userData);
		projectType = ProjectTypeFactory.getProjectTypeByName((String) userData.get(ProjectWizard.PROJECT_TYPE_ID));
		setTitle("New " + projectType.getName());
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
		gfxFormatLabel = new Label(container, SWT.NONE);
		gfxFormatLabel.setText("Format");
		gfxFormatVariantLabel = new Label(container, SWT.NONE);
		gfxFormatVariantLabel.setText("Format Variant");

		List<TargetPlatform> targetPlatformList = PlatformFactory.getTargetPlatFormList();
		userData.put(ProjectWizard.TARGET_PLATFORM, targetPlatformList.get(0).getId());
		List<GraphicFormat> graphicFormatList = GraphicFormatFactory
				.getFormatByPrefix(targetPlatformList.get(0).getId());
		userData.put(ProjectWizard.PROJECT_TYPE, graphicFormatList.get(0).getId());
		List<GraphicFormatVariant> graphicFormatVariantList = graphicFormatList.get(0).getVariants();
		userData.put(ProjectWizard.PROJECT_VARIANT, graphicFormatVariantList.get(0).getId());

		projectNameText = new Text(container, SWT.BORDER);
		projectNameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setText(projectNameText.getText());
			}
		});
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
				List<GraphicFormat> l = GraphicFormatFactory.getFormatByPrefix(targetPlatform.getId());
				gfxFormatCombo.setInput(l);

				if (l.size() > 0) {
					gfxFormatCombo.getCombo().setEnabled(true);
					gfxFormatCombo.setSelection(new StructuredSelection(l.get(0)));
				} else {
					gfxFormatCombo.getCombo().setEnabled(false);
				}
			}
		});

		gfxFormatCombo = new ComboViewer(container, SWT.NONE);
		gfxFormatCombo.setContentProvider(ArrayContentProvider.getInstance());
		gfxFormatCombo.setInput(graphicFormatList);
		gfxFormatCombo.setSelection(new StructuredSelection(graphicFormatList.get(0)));
		gfxFormatCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof GraphicFormat) {
					GraphicFormat current = (GraphicFormat) element;

					return current.getName();
				}
				return super.getText(element);
			}
		});
		gfxFormatCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				GraphicFormat graphicFormat = (GraphicFormat) selection.getFirstElement();
				userData.put(ProjectWizard.PROJECT_TYPE, graphicFormat.getId());
				List<GraphicFormatVariant> l = GraphicFormatFactory.getFormatVariantListByPrefix(graphicFormat.getId());
				gfxFormatVariantCombo.setInput(l);
				gfxFormatVariantCombo.setSelection(new StructuredSelection(l.get(0)));

			}
		});

		gfxFormatVariantCombo = new ComboViewer(container, SWT.NONE);
		gfxFormatVariantCombo.setContentProvider(ArrayContentProvider.getInstance());
		gfxFormatVariantCombo.setInput(graphicFormatVariantList);
		gfxFormatVariantCombo.setSelection(new StructuredSelection(graphicFormatVariantList.get(0)));
		gfxFormatVariantCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof GraphicFormatVariant) {
					GraphicFormatVariant current = (GraphicFormatVariant) element;
					return current.getName();
				}
				return super.getText(element);
			}
		});
		gfxFormatVariantCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				GraphicFormatVariant graphicFormatVariant = (GraphicFormatVariant) selection.getFirstElement();
				userData.put(ProjectWizard.PROJECT_VARIANT, graphicFormatVariant.getId());
			}
		});

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
		gfxFormatLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(gfxFormatLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(gfxFormatLabel, 0, SWT.LEFT);
		gfxFormatVariantLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(projectNameLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		projectNameText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(targetPlatformLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		targetPlatformCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(gfxFormatLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		gfxFormatCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(gfxFormatVariantLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		gfxFormatVariantCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(gfxFormatVariantLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
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
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		basePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(sourcePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		sourcePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(binaryPathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		binaryPathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(includePathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		includePathText.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(symbolPathLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 100, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		symbolPathText.setLayoutData(formData);
	}

	private void setText(String fileName) {
		userData.put(ProjectWizard.PROJECT_ID, projectNameText.getText().toUpperCase());
		userData.put(ProjectWizard.PROJECT_NAME, projectNameText.getText());
		setPageComplete(!projectNameText.getText().isEmpty());
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			String fileName = FilenameUtils.getBaseName((String) userData.get(ProjectWizard.FILE_NAME));
			if (fileName != null && !fileName.equals("")) {
				projectNameText.setText(fileName);
				setText(fileName);
			}
		}
	}
}
