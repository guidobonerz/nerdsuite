package de.drazil.nerdsuite.storagemedia;

public interface IContentWriter {
	public void write(MediaEntry entry, int start, int len, boolean finished) throws Exception;
}
