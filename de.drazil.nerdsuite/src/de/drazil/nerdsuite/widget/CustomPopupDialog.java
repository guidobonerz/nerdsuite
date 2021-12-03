package de.drazil.nerdsuite.widget;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CustomPopupDialog extends PopupDialog {

	private Composite child;

	public CustomPopupDialog(Shell parent, Composite child) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE | SWT.MODELESS, false, true, false, false, false, null, null);
		this.child = child;
		
	}

	protected Control createDialogArea(Composite parent) {
		child.setParent(parent);
		return child;
	}

	protected void adjustBounds() {
		super.adjustBounds();
		Display d = Display.getCurrent();
		if (d == null) {
			d = Display.getDefault();
		}
		Point point = d.getCursorLocation();
		getShell().setLocation(point.x + 9, point.y + 14);
	}
}
