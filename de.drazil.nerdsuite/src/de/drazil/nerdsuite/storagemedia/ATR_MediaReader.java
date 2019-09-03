package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public class ATR_MediaReader extends AbstractBaseMediaReader {

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
	// private int directoryBaseOffset;

	public ATR_MediaReader(File file) {
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
		getRoot().setSector(directorySector);
	}

	private int getSectorOffset(int sector) {
		return (sectorSize == 0x80 ? (sector * sectorSize)
				: (largeDiskCorrectionCount * 0x80 + (sector - largeDiskCorrectionCount) * sectorSize)) + 0x10;
	}

	@Override
	public void readEntries(MediaEntry parent) {
		int id = 1;
		boolean hasMoreEntries = true;
		int currentDirectorySectorOffset = getSectorOffset(parent.getSector());
		if (parent.isDirectory()) {
			currentDirectorySectorOffset -= 0x100;
		}
		int currentDirectoryEntryOffset = currentDirectorySectorOffset;

		while (hasMoreEntries) {
			int entryFlag = getByte(currentDirectoryEntryOffset);
			int entrySectorCount = getWord(currentDirectoryEntryOffset + 0x01);
			int entrySector = getWord(currentDirectoryEntryOffset + 0x03);
			String fileName = getString(currentDirectoryEntryOffset + 0x05, currentDirectoryEntryOffset + 0x0c, false);
			String fileExtension = getString(currentDirectoryEntryOffset + 0x0d, currentDirectoryEntryOffset + 0x0f,
					false);
			int usedSectorBytes = getByte(currentDirectoryEntryOffset);
			if (entryFlag != 0x00 && entryFlag != 0x80) {
				fileName = String.format("%1$s.%2$s (%3$3d )", fileName, fileExtension, entrySectorCount);
				MediaEntry entry = new MediaEntry(id, fileName, fileName, fileExtension, entrySectorCount, 0, 0, 0,
						null);
				entry.setDirectory((entryFlag & 0x10) == 0x10);
				entry.setUserObject(getContainer());
				entry.setSector(entrySector);
				MediaFactory.addChildEntry(parent, entry);
			}
			currentDirectoryEntryOffset += 0x10;
			if (id % 8 == 0) {
				currentDirectorySectorOffset += sectorSize;
				currentDirectoryEntryOffset = currentDirectorySectorOffset;
			}

			id++;
			hasMoreEntries = !isEmptyEntry(currentDirectoryEntryOffset, 0x10, 0) && !(id > 64);
		}
	}

	@Override
	public byte[] readContent(MediaEntry entry) {
		int dataOffset = getSectorOffset(509 - 1);
		System.out.printf("dataoffset  $%04x\n", dataOffset);
		/*
		 * System.out.printf("bytes used  $%02x\n", content[dataOffset + 0x7d]);
		 * System.out.printf("file no     $%02x\n", (content[dataOffset + 0x7e] >> 2));
		 * 
		 * int h = (content[dataOffset + 0x7e] & 0x03) << 8; int l = content[dataOffset
		 * + 0x7f];
		 * 
		 * int nextSectorDataOffset = getSectorOffset(h + l - 1);
		 * System.out.printf("next sector $%02x\n", h + l);
		 * System.out.printf("next sector data $%04x\n", nextSectorDataOffset);
		 */
		int exeHeader = getWord(dataOffset);
		System.out.printf("exeheader $%04x\n", exeHeader);
		dataOffset += 2;

		int start = 0;
		int end = 0;
		int c = 0;
		while (c < 10) {
			start = getWord(dataOffset);
			dataOffset += 2;
			end = getWord(dataOffset);
			System.out.printf("\n$%04x $%04x\n", start, end);
			dataOffset += 2;
			int x = end - start + 1;
			int id = 1;
			for (int i = 0; i < x; i++) {
				System.out.printf("%02x ", content[dataOffset]);
				if (id % 16 == 0) {
					System.out.printf(" $%04x\n",dataOffset);
				}
				id++;
				dataOffset++;
			}
			c++;
		}
		// int bytesUsedInSector = getByte(dataOffset + 125);
		// int nextDataSector = getWord(dataOffset + 126);
		return null;
	}

}
