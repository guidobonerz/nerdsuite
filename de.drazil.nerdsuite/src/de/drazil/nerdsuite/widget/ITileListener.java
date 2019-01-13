package de.drazil.nerdsuite.widget;

public interface ITileListener {
	public void layerAdded();

	public void layerRemoved();

	public void layerReordered();

	public void layerVisibilityChanged(int layer);

	public void activeLayerChanged(int layer);

	public void layerContentChanged(int layer);

	
}
