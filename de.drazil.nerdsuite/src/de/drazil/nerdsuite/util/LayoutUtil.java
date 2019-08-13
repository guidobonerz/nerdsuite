package de.drazil.nerdsuite.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

public class LayoutUtil {

	public static void layout(Control source, int top, int loc1, int left, int loc2, Control target) {

		layout(source, top, left, -1, -1, target);
	}

	public static void layout(Control source, int top, int loc1,int left,int loc2, int bottom, int right, Control target) {

		FormData formData = new FormData();
		if (top != -1) {
			formData.top = new FormAttachment(source, top, loc1);
		}
		if (left != -1) {
			formData.left = new FormAttachment(source, left, loc2);
		}
		if (bottom != -1) {
			formData.bottom = new FormAttachment(source, bottom, SWT.BOTTOM);
		}
		if (right != -1) {
			formData.right = new FormAttachment(source, right, SWT.RIGHT);
		}
		target.setLayoutData(formData);
	}
}
