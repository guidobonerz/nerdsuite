package de.drazil.nerdsuite.storagemedia;

public class ATR_MediaManager extends AbstractBaseMediaManager {

	private int atariDiskId;
	private int diskImageSize;
	private int sectorSize;
	private int diskImageSizeHighPart;
	private long imageSize;
	private int diskFlag;
	private int firstTypicalSector;
	private int vtocStartOffset;

	private int typeCode;
	private int sectorCount;
	private int unusedSectorCount;

	private int directorySector;
	private int largeDiskCorrectionCount;
	private int directoryBaseOffset;
	private int currentDirectoryEntryOffset;

	public ATR_MediaManager() {

	}

	@Override
	protected void readHeader() {
		atariDiskId = getWord(0);
		diskImageSize = getWord(2);
		sectorSize = getWord(4);
		diskImageSizeHighPart = getByte(6);
		imageSize = (diskImageSizeHighPart << 16) + diskImageSize;
		diskFlag = getByte(8);
		firstTypicalSector = getWord(9);
		vtocStartOffset = 0x167;

		typeCode = content[vtocStartOffset];
		sectorCount = getWord(vtocStartOffset + 0x01);
		unusedSectorCount = getWord(vtocStartOffset + 0x03);

		directorySector = 0x168;
		largeDiskCorrectionCount = 3;
		directoryBaseOffset = (sectorSize == 0x80 ? (directorySector * sectorSize)
				: (largeDiskCorrectionCount * 0x80 + (directorySector - largeDiskCorrectionCount) * sectorSize)) + 0x10;
		currentDirectoryEntryOffset = directoryBaseOffset;

	}

	@Override
	protected void readEntries(MediaEntry parent) {
		int id = 0;
		while (currentDirectoryEntryOffset < directoryBaseOffset + 0xd810) // sectorSize)
		{
			int entryFlag = content[currentDirectoryEntryOffset];
			int entrySectorCount = getWord(currentDirectoryEntryOffset + 0x01);
			int entrySector = getWord(currentDirectoryEntryOffset + 0x03);
			int subfolderOffset = (sectorSize == 0x80 ? (entrySector * sectorSize)
					: (3 * 0x80 + entrySector * sectorSize)) + 0x10 - 0x270;
			if ((entryFlag & 0x010) == 0x010) {
				System.out.println("subdirectory found");
			} else {
				System.out.println("file found");
			}

			String fileName = getString(currentDirectoryEntryOffset + 0x05, currentDirectoryEntryOffset + 0x0c, false);
			String fileExtension = getString(currentDirectoryEntryOffset + 0x0d, currentDirectoryEntryOffset + 0x0f,
					false);
			int usedSectorBytes = getByte(currentDirectoryEntryOffset);
			if (entryFlag != 0x00 && entryFlag != 0x80) {
				fileName = String.format("%1$s.%2$s (%3$3d )", fileName, fileExtension, entrySectorCount);
				getRoot().addChildEntry(new MediaEntry(id, fileName, fileName, fileExtension, 0, 0, 0, 0, null));
			}
			currentDirectoryEntryOffset += 0x10;
			id++;
		}

	}

	@Override
	protected byte[] readContent(MediaEntry entry) {
		// TODO Auto-generated method stub
		return null;
	}

}
