package de.drazil.nerdsuite.widget;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CustomPopupDialog extends PopupDialog {

	public CustomPopupDialog(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, null, null);
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

	@Override
	protected Point getDefaultSize() {

		return new Point(102, 122);
	}

}
