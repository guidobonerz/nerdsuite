package de.drazil.nersuite.storagemedia;

import java.io.File;

public interface IMediaManager {

	public byte[] read(File file) throws Exception;

	public boolean hasEntries();

	public Object[] getEntries();

}
