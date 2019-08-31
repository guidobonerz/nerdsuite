package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public interface IMediaManager {

	public byte[] read(File file) throws Exception;

	public boolean hasEntries(Object entry);

	public Object[] getEntries(Object parent);

}
