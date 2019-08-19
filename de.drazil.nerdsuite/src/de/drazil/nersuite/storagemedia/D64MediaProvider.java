package de.drazil.nersuite.storagemedia;

import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;

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

		int bamBase = 0x16500;
		System.out.printf("Filename %s\n", getFilename(bamBase + 0x90, 0x0f, 0x0a));

		int dirEntryBase = bamBase + 0x100;
		while (dirEntryBase < bamBase + 0x100 + 0xe0) {
			System.out.printf("Next entry at track:%d sector:%d\n", content[dirEntryBase], content[dirEntryBase + 0x1]);
			System.out.printf("FileType: %s\n", getFileType(content[dirEntryBase + 0x2]));
			System.out.printf("First file entry at track:%d sector:%d\n", content[dirEntryBase + 0x3],
					content[dirEntryBase + 0x4]);
			System.out.printf("Filename %s\n", getFilename(dirEntryBase + 0x5, 0x10, 0xa0));
			System.out.printf("Filesize %s\n\n", getFileSize(cpu, dirEntryBase + 0x1e));
			mediaEntryList.add(
					new MediaEntry(getFilename(dirEntryBase + 0x5, 0x10, 0xa0), getFileSize(cpu, dirEntryBase + 0x1e)));
			dirEntryBase += 0x20;

		}
	}

	private int getFileSize(ICPU cpu, int start) {
		return cpu.getWord(content, start);
	}

	private String getFilename(int start, int length, int skipByte) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < start + length; i++) {
			int c = content[i];
			if (c != skipByte) {

				sb.append(new String(Character.toChars(0xe100 + c)));

				// sb.append(Character.toChars(0xe051));

			}
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
