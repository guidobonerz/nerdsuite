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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
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
	private Label maxItemsLabel;
	private Label widthLabel;
	private Label heightLabel;
	private Label rowsLabel;
	private Label columnsLabel;
	private Text projectNameText;
	private Spinner tileWidthSpinner;
	private Spinner tileHeightSpinner;
	private Spinner tileColumnsSpinner;
	private Spinner tileRowsSpinner;
	private Spinner maxItemsSpinner;
	private ComboViewer targetPlatformCombo;
	private ComboViewer gfxFormatCombo;
	private ComboViewer gfxFormatVariantCombo;
	private SimpleEntity projectType;
	private GraphicFormat gf;
	private GraphicFormatVariant gfv;

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

		separatorLabel = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);

		maxItemsLabel = new Label(container, SWT.NONE);
		maxItemsLabel.setText("Max items");

		maxItemsSpinner = new Spinner(container, SWT.BORDER);

		projectNameLabel = new Label(container, SWT.NONE);
		projectNameLabel.setText("Project Name");
		targetPlatformLabel = new Label(container, SWT.NONE);
		targetPlatformLabel.setText("Target Platform");
		gfxFormatLabel = new Label(container, SWT.NONE);
		gfxFormatLabel.setText("Format");
		gfxFormatVariantLabel = new Label(container, SWT.NONE);
		gfxFormatVariantLabel.setText("Format Variant");

		widthLabel = new Label(container, SWT.NONE);
		widthLabel.setEnabled(false);
		widthLabel.setText("Width");
		heightLabel = new Label(container, SWT.NONE);
		heightLabel.setEnabled(false);
		heightLabel.setText("Height");
		columnsLabel = new Label(container, SWT.NONE);
		columnsLabel.setEnabled(false);
		columnsLabel.setText("Columns");
		rowsLabel = new Label(container, SWT.NONE);
		rowsLabel.setEnabled(false);
		rowsLabel.setText("Rows");

		List<TargetPlatform> targetPlatformList = PlatformFactory.getTargetPlatFormList();
		userData.put(ProjectWizard.TARGET_PLATFORM, targetPlatformList.get(0).getId());
		List<GraphicFormat> graphicFormatList = GraphicFormatFactory
				.getFormatByPrefix(targetPlatformList.get(0).getId());
		gf = graphicFormatList.get(0);
		userData.put(ProjectWizard.PROJECT_TYPE, graphicFormatList.get(0).getId());
		List<GraphicFormatVariant> graphicFormatVariantList = GraphicFormatFactory
				.getFormatVariantListByPrefix(graphicFormatList.get(0).getId());
		gfv = graphicFormatVariantList.get(0);
		userData.put(ProjectWizard.PROJECT_VARIANT, graphicFormatVariantList.get(0).getId());
		int maxItems = graphicFormatList.get(0).getMaxItems();
		userData.put(ProjectWizard.PROJECT_MAX_ITEMS, maxItems);
		maxItemsSpinner.setValues(maxItems, (maxItems == -1) ? -1 : 1, maxItems, 0, 1, 1);

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
				return ((GraphicFormat) element).getName();
			}
		});
		gfxFormatCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				gf = (GraphicFormat) selection.getFirstElement();
				userData.put(ProjectWizard.PROJECT_TYPE, gf.getId());
				List<GraphicFormatVariant> l = GraphicFormatFactory.getFormatVariantListByPrefix(gf.getId());
				gfxFormatVariantCombo.setInput(l);
				gfxFormatVariantCombo.setSelection(new StructuredSelection(l.get(0)));
				int maxItems = gf.getMaxItems() == -1 ? 1 : gf.getMaxItems();
				maxItemsSpinner.setValues(maxItems, 1, maxItems, 0, 1, 16);
				userData.put(ProjectWizard.PROJECT_MAX_ITEMS, maxItems);
			}
		});

		gfxFormatVariantCombo = new ComboViewer(container, SWT.NONE);
		gfxFormatVariantCombo.setContentProvider(ArrayContentProvider.getInstance());
		gfxFormatVariantCombo.setInput(graphicFormatVariantList);
		gfxFormatVariantCombo.setSelection(new StructuredSelection(graphicFormatVariantList.get(0)));
		gfxFormatVariantCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((GraphicFormatVariant) element).getName();
			}
		});
		gfxFormatVariantCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				gfv = (GraphicFormatVariant) selection.getFirstElement();
				userData.put(ProjectWizard.PROJECT_VARIANT, gfv.getId());
				boolean enable = gfv.getId().equals("CUSTOM");
				boolean isASCII = gf.getId().matches("^(.*PETSCII|.*SCREENSET)$");
				widthLabel.setEnabled(enable && isASCII);
				heightLabel.setEnabled(enable && isASCII);
				rowsLabel.setEnabled(enable);
				columnsLabel.setEnabled(enable);
				tileWidthSpinner.setEnabled(enable && isASCII);
				tileHeightSpinner.setEnabled(enable && isASCII);
				tileColumnsSpinner.setEnabled(enable);
				tileRowsSpinner.setEnabled(enable);
				setSpinnerValues();
			}
		});

		tileWidthSpinner = new Spinner(container, SWT.BORDER);
		tileWidthSpinner.setEnabled(false);
		tileWidthSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				putSpinnerValues();
			}
		});

		tileHeightSpinner = new Spinner(container, SWT.BORDER);
		tileHeightSpinner.setEnabled(false);
		tileHeightSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				putSpinnerValues();
			}
		});

		tileColumnsSpinner = new Spinner(container, SWT.BORDER);
		tileColumnsSpinner.setEnabled(false);
		tileColumnsSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				putSpinnerValues();
			}
		});

		tileRowsSpinner = new Spinner(container, SWT.BORDER);
		tileRowsSpinner.setEnabled(false);
		tileRowsSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				putSpinnerValues();
			}
		});

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
		formData.top = new FormAttachment(gfxFormatVariantLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(gfxFormatVariantLabel, 0, SWT.LEFT);
		widthLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(widthLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(widthLabel, 0, SWT.LEFT);
		heightLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(heightLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(heightLabel, 0, SWT.LEFT);
		columnsLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(columnsLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(columnsLabel, 0, SWT.LEFT);
		rowsLabel.setLayoutData(formData);

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
		formData.top = new FormAttachment(gfxFormatLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		gfxFormatCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(gfxFormatVariantLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		gfxFormatVariantCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(widthLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		tileWidthSpinner.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(heightLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		tileHeightSpinner.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(columnsLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		tileColumnsSpinner.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(rowsLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		tileRowsSpinner.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(rowsLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		separatorLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(separatorLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(separatorLabel, 0, SWT.LEFT);
		maxItemsLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(maxItemsLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		maxItemsSpinner.setLayoutData(formData);

		setSpinnerValues();
	}

	private void setSpinnerValues() {
		tileWidthSpinner.setValues(gf.getWidth(), 1, 1000, 0, 1, 1);
		tileHeightSpinner.setValues(gf.getHeight(), 1, 1000, 0, 1, 1);
		tileColumnsSpinner.setValues(gfv.getTileColumns(), 1, 1000, 0, 1, 1);
		tileRowsSpinner.setValues(gfv.getTileRows(), 1, 1000, 0, 1, 1);
		userData.put("width", tileWidthSpinner.getSelection());
		userData.put("height", tileHeightSpinner.getSelection());
		userData.put("columns", tileColumnsSpinner.getSelection());
		userData.put("rows", tileRowsSpinner.getSelection());
	}

	private void putSpinnerValues() {
		userData.put("width", tileWidthSpinner.getSelection());
		userData.put("height", tileHeightSpinner.getSelection());
		userData.put("columns", tileColumnsSpinner.getSelection());
		userData.put("rows", tileRowsSpinner.getSelection());
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
