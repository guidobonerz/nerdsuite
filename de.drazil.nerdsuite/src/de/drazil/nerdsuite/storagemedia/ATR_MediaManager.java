package de.drazil.nerdsuite.storagemedia;

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
		int atariDiskId = getWord(0);
		int diskImageSize = getWord(2);
		int sectorSize = getWord(4);
		int diskImageSizeHighPart = getWord(6);
		int diskFlag = getByte(8);
		int firstTypicalSector = getWord(9);
		int vtocStartOffset = 0x167;
		int directoryBaseOffset = (sectorSize == 0x80 ? (0x168 * sectorSize) : (3 * 0x80 + 0x165 * sectorSize)) + 0x10;
		int currentDirectoryEntryOffset = directoryBaseOffset;
		int id = 0;
		while (currentDirectoryEntryOffset < directoryBaseOffset + 0xd810 ) //sectorSize) 
			{
			int entryFlag = content[currentDirectoryEntryOffset];
			int entrySectorCount = getWord(currentDirectoryEntryOffset + 0x01);
			int entrySector = getWord(currentDirectoryEntryOffset + 0x03);
			String fileName = getString(currentDirectoryEntryOffset + 0x05, currentDirectoryEntryOffset + 0x0c, false);
			String fileExtension = getString(currentDirectoryEntryOffset + 0x0d, currentDirectoryEntryOffset + 0x0f,
					false);
			int usedSectorBytes = getByte(currentDirectoryEntryOffset);
			if (entryFlag != 0x00 && entryFlag != 0x80) {
				fileName = String.format("%1$s.%2$s (%3$3d )", fileName, fileExtension, entrySectorCount);
				mediaEntryList.add(new MediaEntry(id, fileName, fileName, fileExtension, 0, 0, 0, 0, null, null));
			}
			currentDirectoryEntryOffset += 0x10;
			id++;
		}

	}

}
