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
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		container.setLayout(layout);

		importFormatLabel = new Label(container, SWT.NONE);
		importFormatLabel.setText("Format");
		FormData formData = null;
		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(container, 0);
		importFormatLabel.setLayoutData(formData);

		importFormatCombo = new ComboViewer(container, SWT.NONE);
		importFormatCombo.setContentProvider(ArrayContentProvider.getInstance());
		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(importFormatLabel, 5);
		importFormatCombo.getControl().setLayoutData(formData);

		bytesToSkipLabel = new Label(container, SWT.NONE);
		bytesToSkipLabel.setText("Bytes To Skip");
		formData = new FormData();
		formData.top = new FormAttachment(importFormatLabel, 5);
		formData.left = new FormAttachment(container, 5);
		bytesToSkipLabel.setLayoutData(formData);

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
		formData.top = new FormAttachment(importFormatCombo.getControl(), 5);
		formData.left = new FormAttachment(bytesToSkipLabel, 5);
		bytesToSkipSpinner.setLayoutData(formData);
	}

	private void setData(int value) {
		//userData.put(ProjectWizard.IMPORT_FORMAT, "BINARY");
		//userData.put(ProjectWizard.BYTES_TO_SKIP, new Integer(value));
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
