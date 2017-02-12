package de.drazil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.CBanner;
import de.drazil.nerdsuite.widget.BitmapPainter;
import org.eclipse.swt.widgets.ExpandBar;

public class Test
{

	public Test()
	{
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent)
	{
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		CBanner banner = new CBanner(parent, SWT.NONE);
		banner.setSimple(false);
	}

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		// TODO	Set the focus to control
	}
}
