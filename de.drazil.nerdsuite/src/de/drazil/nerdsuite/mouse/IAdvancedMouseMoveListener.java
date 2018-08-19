package de.drazil.nerdsuite.mouse;

public interface IAdvancedMouseMoveListener {
	public void mouseMove(int modifierMask, int x, int y);

	public void mouseDragged(int modifierMask, int x, int y);

	public void mouseDropped(int modifierMask, int x, int y);
}
