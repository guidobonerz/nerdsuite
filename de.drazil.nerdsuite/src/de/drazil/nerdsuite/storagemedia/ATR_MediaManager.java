package de.drazil.nerdsuite.storagemedia;

import java.io.File;

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
	private int currentDirectorySectorOffset;

	public ATR_MediaManager(File file) {
		super(file);
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
		currentDirectorySectorOffset = directoryBaseOffset;

	}

	@Override
	protected void readEntries(MediaEntry parent) {
		int id = 1;
		boolean hasMoreEntries = true;
		currentDirectoryEntryOffset = currentDirectorySectorOffset;
		while (hasMoreEntries) {

			int entryFlag = content[currentDirectoryEntryOffset];
			int entrySectorCount = getWord(currentDirectoryEntryOffset + 0x01);
			int entrySector = getWord(currentDirectoryEntryOffset + 0x03);
			int subfolderOffset = (sectorSize == 0x80 ? (entrySector * sectorSize)
					: (3 * 0x80 + entrySector * sectorSize)) + 0x10 - 0x270;
			String fileName = getString(currentDirectoryEntryOffset + 0x05, currentDirectoryEntryOffset + 0x0c, false);
			String fileExtension = getString(currentDirectoryEntryOffset + 0x0d, currentDirectoryEntryOffset + 0x0f,
					false);
			int usedSectorBytes = getByte(currentDirectoryEntryOffset);
			if (entryFlag != 0x00 && entryFlag != 0x80) {
				fileName = String.format("%1$s.%2$s (%3$3d )", fileName, fileExtension, entrySectorCount);
				MediaEntry entry = new MediaEntry(id, fileName, fileName, fileExtension, 0, 0, 0, 0, null);
				entry.setDirectory((entryFlag & 0x10) == 0x10);
				entry.setUserObject(getContainer());
				MediaMountFactory.addChildEntry(parent, entry);
			}
			currentDirectoryEntryOffset += 0x10;

			if (id % 8 == 0) {
				currentDirectorySectorOffset += sectorSize;
				currentDirectoryEntryOffset = currentDirectorySectorOffset;
			}

			hasMoreEntries = !isEmptyEntry(currentDirectoryEntryOffset, 0x10);

			id++;
		}

	}

	@Override
	protected byte[] readContent(MediaEntry entry) {
		// TODO Auto-generated method stub
		return null;
	}

}
