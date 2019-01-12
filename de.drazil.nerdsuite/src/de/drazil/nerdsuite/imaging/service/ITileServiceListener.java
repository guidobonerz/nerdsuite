package de.drazil.nerdsuite.imaging.service;

public interface ITileServiceListener {
	public void tileAdded();

	public void tileRemoved();

	public void tileReordered(); 
}
