package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.disassembler.cpu.Endianness;
import de.drazil.nerdsuite.util.NumericConverter;

public abstract class AbstractBaseMediaReader implements IMediaReader {

	protected byte[] content;
	private MediaEntry root;
	private File container;

	public AbstractBaseMediaReader(File file) {
		container = file;
		root = new MediaEntry();
		root.setRoot(true);
		root.setUserObject(file);
	}

	public File getContainer() {
		return container;
	}

	@Override
	public MediaEntry[] getEntries(Object parentEntry) {
		MediaEntry[] list = new MediaEntry[] {};
		MediaEntry mediaEntry = getRoot();
		if (parentEntry instanceof MediaEntry) {
			mediaEntry = (MediaEntry) parentEntry;
		}
		readEntries(mediaEntry);
		Collections.sort(mediaEntry.getChildrenList(), new Comparator<MediaEntry>() {
			@Override
			public int compare(MediaEntry me1, MediaEntry me2) {
				String s1 = me1.getName() + me1.getType();
				String s2 = me2.getName() + me2.getType();
				return s1.compareTo(s2);
			}
		});
		list = mediaEntry.getChildrenList().toArray(new MediaEntry[mediaEntry.getChildrenCount()]);

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
			char c = (char) (content[i] & 0x7f);
			if (Character.isLetter(c) || Character.isDigit(c) || skipCharCheck) {
				sb.append(Character.toString(c));
			}
		}
		return sb.toString();
	}

	public boolean isEmptyEntry(int base, int maxCount, int checkValue) {
		int count = 0;
		for (int i = base; i < base + maxCount; i++) {
			count += ((content[i] & 0xff) == checkValue ? 1 : 0);
		}
		return count == maxCount;
	}

	protected abstract void readHeader();

	@Override
	public abstract byte[] readContent(MediaEntry entry);

	@Override
	public abstract void readEntries(MediaEntry parent);

}
