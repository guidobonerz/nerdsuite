package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class ImportFormatWizardPage extends AbstractBoundWizardPage {

	private Label importFormatLabel;
	private ComboViewer importFormatCombo;
	private Label bytesToSkipLabel;
	private Spinner bytesToSkipSpinner;

	public ImportFormatWizardPage(Map<String, Object> userData) {
		super("wizardPage", userData);
		setTitle("Specify what and how to import data");
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

		importFormatLabel = new Label(container, SWT.NONE);
		importFormatLabel.setText("Format");

		bytesToSkipLabel = new Label(container, SWT.NONE);
		bytesToSkipLabel.setText("Bytes To Skip");

		importFormatCombo = new ComboViewer(container, SWT.NONE);
		importFormatCombo.setContentProvider(ArrayContentProvider.getInstance());

		bytesToSkipSpinner = new Spinner(container, SWT.NONE);
		bytesToSkipSpinner.setMinimum(0);
		bytesToSkipSpinner.setMaximum(1000);
		bytesToSkipSpinner.setSelection(0);
		bytesToSkipSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setData(bytesToSkipSpinner.getSelection());
			}
		});

		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(container, 0);
		importFormatLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(importFormatLabel, 15, SWT.BOTTOM);
		formData.left = new FormAttachment(importFormatLabel, 0, SWT.LEFT);
		bytesToSkipLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(importFormatLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		importFormatCombo.getControl().setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(bytesToSkipLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		bytesToSkipSpinner.setLayoutData(formData);

	}

	private void setData(int value) {
		userData.put(ProjectWizard.IMPORT_FORMAT, "BINARY");
		userData.put(ProjectWizard.BYTES_TO_SKIP, value);
		setPageComplete(true);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setData(0);
		}
	}
}
