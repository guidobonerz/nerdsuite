package de.drazil.nerdsuite.storagemedia;

public interface IMediaEntryWriter {
	public void write(MediaEntry entry, int start, int len, boolean finished) throws Exception;

	public byte[] getData();
}
