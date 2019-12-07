package de.drazil.nerdsuite.widget;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.mouse.AdvancedMouseAdaper;
import de.drazil.nerdsuite.mouse.IAdvancedMouseListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseMoveListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseTrackListener;
import de.drazil.nerdsuite.mouse.IAdvancedMouseWheelListener;

public abstract class BaseWidget extends Canvas {

	protected AdvancedMouseAdaper ama = null;
	protected int modifierMask = 0;

	public BaseWidget(Composite parent, int style) {
		super(parent, style);

		ama = new AdvancedMouseAdaper(this);
		ama.addMouseListener(new IAdvancedMouseListener() {
			@Override
			public void rightMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
				BaseWidget.this.modifierMask = modifierMask;
				rightMouseButtonTimesClickedInternal(modifierMask, x, y, count);
			}

			@Override
			public void rightMouseButtonReleased(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				rightMouseButtonReleasedInternal(modifierMask, x, y);
			}

			@Override
			public void rightMouseButtonPressed(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				rightMouseButtonPressedInternal(modifierMask, x, y);
			}

			@Override
			public void rightMouseButtonDoubleClicked(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				rightMouseButtonDoubleClickedInternal(modifierMask, x, y);
			}

			@Override
			public void rightMouseButtonClicked(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				rightMouseButtonClickedInternal(modifierMask, x, y);
			}

			@Override
			public void middleMouseButtonReleased(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				middleMouseButtonReleasedInternal(modifierMask, x, y);
			}

			@Override
			public void middleMouseButtonPressed(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				middleMouseButtonPressedInternal(modifierMask, x, y);
			}

			@Override
			public void leftMouseButtonTimesClicked(int modifierMask, int x, int y, int count) {
				BaseWidget.this.modifierMask = modifierMask;
				leftMouseButtonTimesClickedInternal(modifierMask, x, y, count);
			}

			@Override
			public void leftMouseButtonReleased(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				leftMouseButtonReleasedInternal(modifierMask, x, y);
			}

			@Override
			public void leftMouseButtonPressed(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				leftMouseButtonPressedInternal(modifierMask, x, y);
			}

			@Override
			public void leftMouseButtonDoubleClicked(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				leftMouseButtonDoubleClickedInternal(modifierMask, x, y);
			}

			@Override
			public void leftMouseButtonClicked(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				leftMouseButtonClickedInternal(modifierMask, x, y);
			}
		});
		ama.addMouseMoveListener(new IAdvancedMouseMoveListener() {

			@Override
			public void mouseMove(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				mouseMoveInternal(modifierMask, x, y);
			}

			@Override
			public void mouseDropped(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				mouseDraggedInternal(modifierMask, x, y);
			}

			@Override
			public void mouseDragged(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				mouseDraggedInternal(modifierMask, x, y);
			}
		});
		ama.addMouseTrackListener(new IAdvancedMouseTrackListener() {

			@Override
			public void mouseExit(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				mouseExitInternal(modifierMask, x, y);
			}

			@Override
			public void mouseEnter(int modifierMask, int x, int y) {
				BaseWidget.this.modifierMask = modifierMask;
				mouseEnterInternal(modifierMask, x, y);
			}
		});
		ama.addMouseWheelListener(new IAdvancedMouseWheelListener() {
			@Override
			public void mouseScrolled(int modifierMask, int x, int y, int count) {
				BaseWidget.this.modifierMask = modifierMask;
				mouseScrolledInternal(modifierMask, x, y, count);
			}
		});
	}

	protected void setMouseActionEnabled(boolean mouseActionEnabled) {
		ama.setMouseActionEnabled(mouseActionEnabled);
	}

	protected void rightMouseButtonClickedInternal(int modifierMask, int x, int y) {

	}

	protected void rightMouseButtonReleasedInternal(int modifierMask, int x, int y) {

	}

	protected void rightMouseButtonPressedInternal(int modifierMask, int x, int y) {

	}

	protected void leftMouseButtonDoubleClickedInternal(int modifierMask, int x, int y) {

	}

	protected void rightMouseButtonDoubleClickedInternal(int modifierMask, int x, int y) {

	}

	protected void leftMouseButtonClickedInternal(int modifierMask, int x, int y) {

	}

	protected void mouseMoveInternal(int modifierMask, int x, int y) {

	}

	protected void mouseExitInternal(int modifierMask, int x, int y) {

	}

	protected void mouseEnterInternal(int modifierMask, int x, int y) {

	}

	protected void mouseDroppedInternal(int modifierMask, int x, int y) {

	}

	protected void mouseDraggedInternal(int modifierMask, int x, int y) {

	}

	protected void middleMouseButtonReleasedInternal(int modifierMask, int x, int y) {

	}

	protected void middleMouseButtonPressedInternal(int modifierMask, int x, int y) {

	}

	protected void leftMouseButtonReleasedInternal(int modifierMask, int x, int y) {

	}

	protected void leftMouseButtonPressedInternal(int modifierMask, int x, int y) {

	}

	protected void leftMouseButtonTimesClickedInternal(int modifierMask, int x, int y, int count) {

	}

	protected void rightMouseButtonTimesClickedInternal(int modifierMask, int x, int y, int count) {

	}

	protected void mouseScrolledInternal(int modifierMask, int x, int y, int count) {
	};
}
