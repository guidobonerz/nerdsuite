package de.drazil.nerdsuite.wizard;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class FolderSelectionWizardPage extends AbstractBoundWizardPage {

	private Label fileNameLabel;
	private Text fileNameText;
	private Button button;

	public FolderSelectionWizardPage(Map<String, Object> userData) {
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
		container.setLayout(new RowLayout());

		fileNameLabel = new Label(container, SWT.NONE);
		fileNameLabel.setText("Foldername");
		fileNameText = new Text(container, SWT.BORDER);
		fileNameText.setEnabled(false);

		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				DirectoryDialog folderDialog = new DirectoryDialog(container.getShell(), SWT.OPEN);
				String folderName = folderDialog.open();
				setText(folderName);
			}
		});
	}

	private void setText(String fileName) {
		fileNameText.setText(fileName);
		userData.put(ProjectWizard.FILE_NAME, fileName);
		setPageComplete(!fileNameText.getText().isEmpty());
	}
}
