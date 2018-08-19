package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.mouse.AdvancedMouseAdaper;
import de.drazil.nerdsuite.mouse.IAdvancedMouseListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseMoveListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseTrackListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseWheelListener;

public abstract class BaseImagingWidget extends Canvas implements IAdvancedMouseListener, IAdvancedMouseMoveListener,
		IAdvancedMouseTrackListener, IAdvancedMouseWheelListener {
	protected AdvancedMouseAdaper ama = null;

	protected ImagingWidgetConfiguration configuration = null;

	public BaseImagingWidget(Composite parent, int style) {
		super(parent, style);
		configuration = new ImagingWidgetConfiguration();
		ama = new AdvancedMouseAdaper(this);
		ama.addMouseListener(this);
		ama.addMouseMoveListener(this);
		ama.addMouseTrackListener(this);
		ama.addMouseWheelListener(this);
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		// System.out.println("right clicked");
	}

	@Override
	public void rightMouseButtonReleased(int modifierMask, int x, int y) {
		// System.out.println("right released");
	}

	@Override
	public void rightMouseButtonPressed(int modifierMask, int x, int y) {
		// System.out.println("right pressed");
	}

	@Override
	public void leftMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		// System.out.println("left doubleclick");
	}

	@Override
	public void rightMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		// System.out.println("right doubleclick");
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		// System.out.println("left click");
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		// System.out.println("moved");
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		// System.out.println("exit");
	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {
		// System.out.println("enter");
	}

	@Override
	public void mouseDropped(int modifierMask, int x, int y) {
		// System.out.println("dropped");
	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		// System.out.println("dragged");
	}

	@Override
	public void middleMouseButtonReleased(int modifierMask, int x, int y) {
		// System.out.println("middle released");
	}

	@Override
	public void middleMouseButtonPressed(int modifierMask, int x, int y) {
		// System.out.println("middle pressed");
	}

	@Override
	public void leftMouseButtonReleased(int modifierMask, int x, int y) {
		// System.out.println("left released");
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		// System.out.println("left pressed");
	}

	@Override
	public void leftMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		// System.out.println("left " + count + " times clicked");
	}

	@Override
	public void rightMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		// System.out.println("right " + count + " times clicked");
	}

	@Override
	public void mouseScrolled(int modifierMask, int x, int y, int count) {
		// System.out.println("scrolled " + count);
	}
}
