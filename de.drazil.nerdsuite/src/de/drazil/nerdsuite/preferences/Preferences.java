
package de.drazil.nerdsuite.preferences;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Preferences {
	@Execute
	public void execute() {

		PreferenceNode ultimateNode = new PreferenceNode("Ultimate64",
				new FieldEditorPreferencePage("Ultimate64", FieldEditorPreferencePage.GRID) {

					@Override
					protected void createFieldEditors() {
						BooleanFieldEditor u64FtpEnabledField = new BooleanFieldEditor("U64_FTP_ENABLED",
								"Enable FTP access", getFieldEditorParent());
						addField(u64FtpEnabledField);
						StringFieldEditor u64HostAddress = new StringFieldEditor("U64_HOST_ADRESS", "Host",
								getFieldEditorParent());
						u64HostAddress.setTextLimit(15);
						u64HostAddress.setStringValue("10.100.200.201");
						addField(u64HostAddress);
						IntegerFieldEditor u64HostPort = new IntegerFieldEditor("U64_HOST_PORT", "Port",
								getFieldEditorParent());
						addField(u64HostPort);
						u64HostPort.setStringValue("21");

					}
				});

		PreferenceNode toolchainNode = new PreferenceNode("Toolchain", new PreferencePage("Toolchain") {

			@Override
			protected Control createContents(Composite parent) {

				return null;
			}
		});
		PreferenceNode emulatorNode = new PreferenceNode("Emulators", new PreferencePage("Emulators") {
			@Override
			protected Control createContents(Composite parent) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		PreferenceManager pm = new PreferenceManager();
		pm.addToRoot(ultimateNode);
		pm.addToRoot(toolchainNode);
		pm.addToRoot(emulatorNode);
		PreferenceDialog dp = new PreferenceDialog(null, pm);

		dp.open();
	}

}