package de.drazil.nerdsuite.mouse;

public interface IAdvancedMouseListener {
	public void leftMouseButtonPressed(int modifierMask, int x, int y);

	public void leftMouseButtonReleased(int modifierMask, int x, int y);

	public void middleMouseButtonPressed(int modifierMask, int x, int y);

	public void middleMouseButtonReleased(int modifierMask, int x, int y);

	public void rightMouseButtonPressed(int modifierMask, int x, int y);

	public void rightMouseButtonReleased(int modifierMask, int x, int y);

	public void leftMouseButtonTimesClicked(int modifierMask, int x, int y, int count);

	public void leftMouseButtonClicked(int modifierMask, int x, int y);

	public void leftMouseButtonDoubleClicked(int modifierMask, int x, int y);

	public void rightMouseButtonTimesClicked(int modifierMask, int x, int y, int count);

	public void rightMouseButtonClicked(int modifierMask, int x, int y);

	public void rightMouseButtonDoubleClicked(int modifierMask, int x, int y);
}
