package de.drazil.nersuite.storagemedia;

import java.io.File;

public interface IMediaReader {

	public boolean hasFiles();

	public Object[] getFiles();

	public void mount(File file);
}
