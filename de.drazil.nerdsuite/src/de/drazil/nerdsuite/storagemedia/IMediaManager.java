package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public interface IMediaManager {

	public MediaEntry getRoot();

	public byte[] read(File file) throws Exception;

	public boolean hasEntries(Object entry);

	public MediaEntry[] getEntries(Object parent);

}
