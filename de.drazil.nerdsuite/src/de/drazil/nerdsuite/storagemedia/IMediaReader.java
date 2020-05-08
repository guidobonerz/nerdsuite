package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public interface IMediaReader {

	public MediaEntry getRoot();

	public byte[] read(File file) throws Exception;

	public boolean hasEntries(Object entry);

	public MediaEntry[] getEntries(Object parent);

	public void readEntries(MediaEntry parent);

	public void readContent(MediaEntry entry, IContentWriter writer) throws Exception;

	public void exportEntry(MediaEntry entry, File file) throws Exception;
}
