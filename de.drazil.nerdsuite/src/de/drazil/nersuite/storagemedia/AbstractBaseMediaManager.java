package de.drazil.nersuite.storagemedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;

public abstract class AbstractBaseMediaManager implements IMediaManager {

	protected byte[] content;
	protected List<MediaEntry> mediaEntryList;

	public AbstractBaseMediaManager() {
		mediaEntryList = new ArrayList<>();
	}

	@Override
	public MediaEntry[] getEntries() {
		return mediaEntryList.toArray(new MediaEntry[mediaEntryList.size()]);
	}

	@Override
	public boolean hasEntries() {
		return true;
	}

	@Override
	public byte[] read(File file) throws Exception {
		content = BinaryFileHandler.readFile(file, 0);
		readStructure();
		return content;
	}

	protected abstract void readStructure();

	protected abstract byte[] readContent(MediaEntry entry);
}
