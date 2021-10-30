package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public class CRT_MediaContainer extends AbstractBaseMediaContainer {

	public CRT_MediaContainer(File file) {
		super(file);

	}

	@Override
	protected void readHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] readContent(MediaEntry entry, IMediaEntryWriter writer) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readEntries(MediaEntry parent) {
		// TODO Auto-generated method stub

	}

}
