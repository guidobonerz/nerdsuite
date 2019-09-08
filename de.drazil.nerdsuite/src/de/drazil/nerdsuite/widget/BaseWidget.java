package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.mouse.AdvancedMouseAdaper;
import de.drazil.nerdsuite.mouse.IAdvancedMouseListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseMoveListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseTrackListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseWheelListener;

public abstract class BaseWidget extends Canvas implements IAdvancedMouseListener, IAdvancedMouseMoveListener,
		IAdvancedMouseTrackListener, IAdvancedMouseWheelListener {

	protected AdvancedMouseAdaper ama = null;
	protected int modifierMask = 0;

	public BaseWidget(Composite parent, int style) {
		super(parent, style);

		ama = new AdvancedMouseAdaper(this);
		ama.addMouseListener(this);
		ama.addMouseMoveListener(this);
		ama.addMouseTrackListener(this);
		ama.addMouseWheelListener(this);
	}

	public void setMouseActionEnabled(boolean mouseActionEnabled) {
		ama.setMouseActionEnabled(mouseActionEnabled);
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonReleased(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonPressed(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonDoubleClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseDropped(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void middleMouseButtonReleased(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void middleMouseButtonPressed(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonReleased(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void leftMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void rightMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
		this.modifierMask = modifierMask;
	}

	@Override
	public void mouseScrolled(int modifierMask, int x, int y, int count) {
		this.modifierMask = modifierMask;
	}

}