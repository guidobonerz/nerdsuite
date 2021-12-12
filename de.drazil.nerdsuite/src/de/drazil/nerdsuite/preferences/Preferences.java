
package de.drazil.nerdsuite.preferences;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Preferences {
	@Execute
	public void execute() {

		PreferenceNode ultimateNode = new PreferenceNode("Ultimate64", new PreferencePage("Ultimate64") {

			@Override
			protected Control createContents(Composite parent) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		PreferenceNode toolchainNode = new PreferenceNode("Toolchain", new PreferencePage("Toolchain") {

			@Override
			protected Control createContents(Composite parent) {
				// TODO Auto-generated method stub
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