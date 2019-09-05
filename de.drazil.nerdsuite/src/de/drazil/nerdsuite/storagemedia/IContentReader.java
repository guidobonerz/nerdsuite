package de.drazil.nerdsuite.storagemedia;

public interface IContentReader {
	public void read(MediaEntry entry, int start, int len, boolean finished);
}
