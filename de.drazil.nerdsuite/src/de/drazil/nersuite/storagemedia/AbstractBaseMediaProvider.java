package de.drazil.nersuite.storagemedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.disassembler.BinaryFileReader;

public abstract class AbstractBaseMediaProvider implements IMediaProvider {

	protected byte[] content;
	protected List<MediaEntry> mediaEntryList;

	public AbstractBaseMediaProvider() {
		mediaEntryList = new ArrayList<>();
	}

	@Override
	public byte[] read(File file) throws Exception {
		content = BinaryFileReader.readFile(file, 0);
		parse();
		return content;
	}

	protected abstract void parse();
}
