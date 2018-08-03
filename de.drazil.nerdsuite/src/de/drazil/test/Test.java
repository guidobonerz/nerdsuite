package de.drazil.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.ToolBar;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import swing2swt.layout.BoxLayout;
import org.eclipse.swt.widgets.Scale;

public class Test extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Test(Composite parent, int style) {
		super(parent, style);
		setLayout(new RowLayout(SWT.VERTICAL));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new RowData(689, 327));
		
		Composite paintPanel = new Composite(composite, SWT.NONE);
		paintPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite controlPanel = new Composite(composite, SWT.NONE);
		controlPanel.setLayout(new GridLayout(3, false));
		
		Label lblNewLabel = new Label(controlPanel, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Format");
		
		Combo combo = new Combo(controlPanel, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblNewLabel_1 = new Label(controlPanel, SWT.NONE);
		lblNewLabel_1.setText("Multicolor");
		
		Button btnCheckButton = new Button(controlPanel, SWT.CHECK);
		new Label(controlPanel, SWT.NONE);
		new Label(controlPanel, SWT.NONE);
		
		Button btnNewButton_1 = new Button(controlPanel, SWT.NONE);
		btnNewButton_1.setText("Start Animation");
		new Label(controlPanel, SWT.NONE);
		new Label(controlPanel, SWT.NONE);
		
		Button btnNewButton = new Button(controlPanel, SWT.NONE);
		btnNewButton.setText("Stop Animation");
		new Label(controlPanel, SWT.NONE);
		
		Composite selectorPanel = new Composite(this, SWT.NONE);
		selectorPanel.setLayoutData(new RowData(689, 140));
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);

	}
}
