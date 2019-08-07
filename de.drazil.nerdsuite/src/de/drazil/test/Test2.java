package de.drazil.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import net.miginfocom.swt.MigLayout;

public class Test2 {
	protected void buildControls(Composite parent) {
		parent.setLayout(new MigLayout("inset 0", "[fill, grow]", "[fill, grow]"));

		Table table = new Table(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setLayoutData("id table, hmin 100, wmin 300");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		Canvas statusLabel = new Canvas(parent, SWT.BORDER);
		statusLabel.setLayoutData("pos table.x table.y");

		for (int i = 0; i < 10; i++) {
			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText("item #" + i);
		}
	}

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		new Test2().buildControls(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
