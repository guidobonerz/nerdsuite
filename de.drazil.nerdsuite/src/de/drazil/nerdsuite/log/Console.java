package de.drazil.nerdsuite.log;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class Console {
	private static Text textWidget = null;
	private static PrintStream ps = null;

	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
		textWidget = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		textWidget.setEditable(false);
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO Set the focus to control
	}

	public static void setOutputStream(OutputStream out) {
		ps = new PrintStream(out) {
			@Override
			public void println(String message) {
				Console.println(message);
			}

			@Override
			public void print(String message) {
				Console.print(message);
			}
		};
		System.setOut(ps);
	}

	public static void println(String message) {
		print(message + "\n");
	}

	public static void println() {
		print("\n");
	}

	public static void print(String message) {
		textWidget.append(message);
	}

	public static void printf(String message, Object... values) {
		textWidget.append(String.format(message, values));
	}

	public static void clear() {
		textWidget.setText("");
	}
}
