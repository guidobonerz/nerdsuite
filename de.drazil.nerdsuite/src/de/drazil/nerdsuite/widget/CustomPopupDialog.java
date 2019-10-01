package de.drazil.nerdsuite.widget;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.widgets.Shell;

public class CustomPopupDialog extends PopupDialog {

	public CustomPopupDialog(Shell parent, int shellStyle, boolean takeFocusOnOpen, boolean persistSize,
			boolean persistLocation, boolean showDialogMenu, boolean showPersistActions, String titleText,
			String infoText) {
		super(parent, shellStyle, takeFocusOnOpen, persistSize, persistLocation, showDialogMenu, showPersistActions,
				titleText, infoText);
	}
}
