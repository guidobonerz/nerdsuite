package de.drazil.nersuite.storagemedia;

import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.util.NumericConverter;

public class D64MediaProvider extends AbstractBaseMediaProvider {

	@Override
	public MediaEntry[] getEntries() {
		return mediaEntryList.toArray(new MediaEntry[mediaEntryList.size()]);
	}

	@Override
	public boolean hasEntries() {
		return !mediaEntryList.isEmpty();
	}

	@Override
	protected void parse() {
		ICPU cpu = new CPU_6510();

		int base = 0x16500;
		int i = base;
		while (!(content[i] == 0 && content[i + 0x1] == 0)) {
			System.out.printf("Next entry at track:%d sector:%d\n", content[i], content[i + 0x1]);
			System.out.printf("FileType: %s\n", getFileType(content[i + 0x2]));
			System.out.printf("First file entry at track:%d sector:%d\n", content[i + 0x3], content[i + 0x4]);
			System.out.printf("Filename %s\n", getFilename(i + 0x5, 0x10));
			System.out.printf("Filesize %s\n\n", getFileSize(cpu, i + 0x1e));
			i += 0x20;
		}
	}

	private int getFileSize(ICPU cpu, int start) {
		return cpu.getWord(content, start);
	}

	private String getFilename(int start, int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < start + length; i++) {
			int c = content[i];
			sb.append((char) c);
		}
		return sb.toString();
	}

	private String getFileType(byte type) {
		String fileType = "unkown";
		boolean locked = (type & 64) == 64;
		boolean closed = (type & 128) == 128;

		switch ((int) type & 0b111) {
		case 0x0: {
			fileType = "Scratched";
			break;
		}
		case 0x1: {
			fileType = "SEQ";
			break;
		}
		case 0x2: {
			fileType = "PRG";
			break;
		}
		case 0x3: {
			fileType = "USR";
			break;
		}
		case 0x4: {
			fileType = "REL";
			break;
		}
		}
		return (locked ? ">" : "") + fileType + (closed ? "*" : "");
	}

}
