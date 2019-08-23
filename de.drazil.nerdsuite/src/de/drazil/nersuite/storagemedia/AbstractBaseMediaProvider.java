package de.drazil.nersuite.storagemedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;

public abstract class AbstractBaseMediaProvider implements IMediaProvider {

	protected byte[] content;
	protected List<MediaEntry> mediaEntryList;

	public AbstractBaseMediaProvider() {
		mediaEntryList = new ArrayList<>();
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
