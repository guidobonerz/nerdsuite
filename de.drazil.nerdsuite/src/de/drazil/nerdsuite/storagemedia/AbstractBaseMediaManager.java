package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.disassembler.cpu.Endianness;
import de.drazil.nerdsuite.util.NumericConverter;

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
		mediaEntryList.clear();
		content = BinaryFileHandler.readFile(file, 0);
		readStructure();
		return content;
	}

	protected int getWord(int start, Endianness endianess) {
		return NumericConverter.getWordAsInt(content, start, endianess);
	}

	protected int getByte(int start) {
		return NumericConverter.getByteAsInt(content, start);
	}

	protected String getString(int start, int end, boolean skipCharCheck) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= end; i++) {
			char c = (char) content[i];
			if (Character.isLetter(c) || Character.isDigit(c) || skipCharCheck) {
				sb.append(Character.toString(c));
			}
		}
		return sb.toString();
	}

	protected abstract void readStructure();

	protected abstract byte[] readContent(MediaEntry entry);
}
