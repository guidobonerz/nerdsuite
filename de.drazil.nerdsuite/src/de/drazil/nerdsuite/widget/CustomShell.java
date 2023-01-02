package de.drazil.nerdsuite.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.mouse.AdvancedMouseAdaper;
import de.drazil.nerdsuite.mouse.IAdvancedMouseListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseMoveListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseTrackListener;

public class CustomShell
		implements IAdvancedMouseMoveListener, IAdvancedMouseListener, IAdvancedMouseTrackListener, PaintListener {
	private Shell shell;
	private AdvancedMouseAdaper ama;
	private boolean leftPressed = false;
	private boolean onHeader = false;
	private boolean oldHeader = false;
	private int xp, yp;

	public CustomShell() {

	}

	public void open(Composite composite) {
		shell = new Shell(composite.getShell(), SWT.NO_TRIM | SWT.ON_TOP | SWT.DOUBLE_BUFFERED);
		shell.setSize(200, 200);
		shell.addPaintListener(this);
		shell.setBackground(Constants.LIGHT_BLUE);
		ama = new AdvancedMouseAdaper(shell);
		ama.addMouseListener(this);
		ama.addMouseMoveListener(this);
		ama.addMouseTrackListener(this);

		shell.open();
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		if (onHeader) {
			leftPressed = true;
			shell.redraw(0, 0, shell.getSize().x, 20, false);
		}
		System.out.print("pressed");
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leftMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leftMouseButtonPressedDelayed(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leftMouseButtonReleased(int modifierMask, int x, int y) {
		leftPressed = false;
		shell.redraw(0, 0, shell.getSize().x, 20, false);
		System.out.print("released");

	}

	@Override
	public void leftMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void middleMouseButtonPressed(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void middleMouseButtonPressedDelayed(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void middleMouseButtonReleased(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightMouseButtonPressed(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightMouseButtonPressedDelayed(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightMouseButtonReleased(int modifierMask, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rightMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		if (leftPressed) {
			System.out.printf("x:%d/y:%d\n", x, y);
			shell.setLocation(shell.getLocation().x + (x - xp), shell.getLocation().y + (y - yp));
		}
	}

	@Override
	public void mouseDropped(int modifierMask, int x, int y) {

	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		xp = x;
		yp = y;
		onHeader = (yp >= 0 && yp <= 20);
		if (oldHeader != onHeader) {
			oldHeader = onHeader;
			shell.redraw(0, 0, shell.getSize().x, 20, false);
		}

	}

	@Override
	public void mouseDraggedDelayed(int modifierMask, int x, int y) {

	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {

	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		onHeader = false;
		oldHeader = false;
		shell.redraw(0, 0, shell.getSize().x, 20, false);
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		if (onHeader && !leftPressed) {
			gc.setBackground(Constants.DARK_GREY);
			gc.fillRectangle(0, 0, shell.getSize().x, 20);
		} else if (onHeader && leftPressed) {
			gc.setBackground(Constants.LIGHT_RED);
			gc.fillRectangle(0, 0, shell.getSize().x, 20);
		} else {
			gc.setBackground(Constants.LIGHT_BLUE);
			gc.fillRectangle(0, 0, shell.getSize().x, 20);
		}

	}
}
