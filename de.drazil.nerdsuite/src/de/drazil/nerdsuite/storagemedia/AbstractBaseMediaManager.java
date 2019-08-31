package de.drazil.nerdsuite.storagemedia;

import java.io.File;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.disassembler.cpu.Endianness;
import de.drazil.nerdsuite.util.NumericConverter;

public abstract class AbstractBaseMediaManager implements IMediaManager {

	protected byte[] content;
	private MediaEntry root;

	public AbstractBaseMediaManager() {
		root = new MediaEntry();
	}

	@Override
	public MediaEntry[] getEntries(Object parentEntry) {
		MediaEntry[] list = new MediaEntry[] {};

		MediaEntry mediaEntry = getRoot();
		if (parentEntry instanceof MediaEntry) {
			mediaEntry = (MediaEntry) parentEntry;
		}
		readEntries(mediaEntry);
		list = root.getChildrenList().toArray(new MediaEntry[mediaEntry.getChildrenCount()]);

		return list;
	}

	@Override
	public boolean hasEntries(Object entry) {
		boolean hasChildren = false;
		if (entry instanceof MediaEntry) {
			MediaEntry me = (MediaEntry) entry;

			hasChildren = me.hasChildren();
		}
		return hasChildren;
	}

	public MediaEntry getRoot() {
		return root;
	}

	@Override
	public byte[] read(File file) throws Exception {
		getRoot().clear();
		content = BinaryFileHandler.readFile(file, 0);
		readHeader();
		return content;
	}

	protected int getWord(int start) {
		return getWord(start, Endianness.LittleEndian);
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

	protected abstract void readHeader();

	protected abstract void readEntries(MediaEntry parent);

	protected abstract byte[] readContent(MediaEntry entry);
}
