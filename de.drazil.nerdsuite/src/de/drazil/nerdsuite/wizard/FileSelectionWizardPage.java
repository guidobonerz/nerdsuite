package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class FileSelectionWizardPage extends AbstractBoundWizardPage {

	private Label fileNameLabel;
	private Text fileNameText;
	private Button button;

	public FileSelectionWizardPage(Map<String, Object> userData) {
		super("wizardPage", userData);
		setTitle((String) userData.get("FILE_SELECTION_TITLE"));
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

		fileNameLabel = new Label(container, SWT.NONE);
		fileNameLabel.setText("Filename");
		fileNameText = new Text(container, SWT.BORDER);
		fileNameText.setEnabled(false);

		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				FileDialog openDialog = new FileDialog(container.getShell(), SWT.OPEN);
				openDialog.setFilterNames(new String[] { "Nerdsuite files", "DSK Image (CPC/Spectrum)",
						"ATR Image (Atari)", "D64 Image (Commodore)", "D71 Image (Commodore)", "D81 Image (Commodore)",
						"Cart Image", "All files" });
				openDialog.setFilterExtensions(
						new String[] { "*.ns_*", "*.dsk", "*.atr", "*.d64", "*.d71", "*.d81", "*.crt", "*.*" }); // Windows
				String fileName = openDialog.open();
				setText(fileName);
			}
		});

		formData = new FormData();
		formData.top = new FormAttachment(container, 0);
		formData.left = new FormAttachment(container, 0);
		fileNameLabel.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(fileNameLabel, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 140, SWT.RIGHT);
		formData.right = new FormAttachment(container, 300);
		fileNameText.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(fileNameText, 0, SWT.TOP);
		formData.left = new FormAttachment(container, 310, SWT.RIGHT);
		formData.right = new FormAttachment(container, 400);
		button.setLayoutData(formData);
	}

	private void setText(String fileName) {
		fileNameText.setText(fileName);
		userData.put(ProjectWizard.FILE_NAME, fileName);
		setPageComplete(!fileNameText.getText().isEmpty());
	}
}
