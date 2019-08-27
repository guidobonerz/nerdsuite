package de.drazil.nerdsuite.storagemedia;

import de.drazil.nerdsuite.disassembler.cpu.Endianness;

public class ATR_MediaManager extends AbstractBaseMediaManager {

	public ATR_MediaManager() {

	}

	@Override
	protected byte[] readContent(MediaEntry entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void readStructure() {
		int atariDiskId = getWord(0, Endianness.LittleEndian);
		int diskImageSize = getWord(2, Endianness.LittleEndian);
		int sectorSize = getWord(4, Endianness.LittleEndian);
		int diskImageSizeHighPart = getWord(6, Endianness.LittleEndian);
		int diskFlag = getByte(8);
		int firstTypicalSector = getWord(9, Endianness.LittleEndian);
		int vtocStartOffset = 0x167;
		int directoryBaseOffset = (sectorSize == 0x80 ? (0x168 * sectorSize) : (3 * 0x80 + 0x165 * sectorSize)) + 0x10;
		int currentDirectoryEntryOffset = directoryBaseOffset;

		while (currentDirectoryEntryOffset < directoryBaseOffset + 0x100) {
			int entryFlag = content[currentDirectoryEntryOffset];
			int entrySectorCount = getWord(currentDirectoryEntryOffset + 0x01, Endianness.LittleEndian);
			int entrySector = getWord(currentDirectoryEntryOffset + 0x03, Endianness.LittleEndian);
			String fileName = getString(currentDirectoryEntryOffset + 0x05, currentDirectoryEntryOffset + 0x0c, false);
			String fileExtension = getString(currentDirectoryEntryOffset + 0x0d, currentDirectoryEntryOffset + 0x0f,
					false);
			int usedSectorBytes = getByte(currentDirectoryEntryOffset);
			if (entryFlag != 0x00 && entryFlag != 0x80) {
				mediaEntryList.add(new MediaEntry(fileName + "." + fileExtension, 0, null, 0, 0, null, null));
			}
			currentDirectoryEntryOffset += 0x10;
		}

	}

}
