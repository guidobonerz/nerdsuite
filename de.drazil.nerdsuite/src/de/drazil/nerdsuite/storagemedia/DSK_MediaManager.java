package de.drazil.nerdsuite.storagemedia;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

public class DSK_MediaManager extends AbstractBaseMediaManager {

	private final static int SECTOR_SIZE = 512;
	private final static int RECORD_SIZE = SECTOR_SIZE >> 2;

	enum DiskFormat {
		Standard(0), Extended(2), Unkown(-1);

		@Getter
		private int directoryTrack;

		private DiskFormat(int directoryTrack) {
			this.directoryTrack = directoryTrack;
		}
	}

	int[][] trackSizes = null;

	protected int directoryTrack = 2;

	public DSK_MediaManager(File file) {
		super(file);
	}

	private String diskInfo;
	private String creator;
	private int tracks;
	private int sides;
	private DiskFormat diskFormat;
	private int trackInfoBaseOffset = 0x100;
	private int directoryBaseOffset = trackInfoBaseOffset + 0x100;
	private int base;
	private int tempBase;
	private int currentDirectoryEntryOffset;
	private int sectorInfobase;
	private String trackInfoText;
	private int sectorSize;
	private int sectorCount;

	@Override
	protected void readHeader() {

		diskInfo = getString(0x00, 0x21, true);
		creator = getString(0x22, 0x2f, true);
		tracks = getByte(0x30);
		sides = getByte(0x31);

		System.out.println("DiskInfo:" + diskInfo);
		System.out.println("Creator:" + creator);
		System.out.println("Tracks:" + tracks);
		System.out.println("Sides:" + sides);

		diskFormat = getDiskFormat(diskInfo);

		trackSizes = new int[tracks][sides];

		switch (diskFormat) {
		case Standard:
			for (int s = 0; s < sides; s++) {
				for (int t = 0; t < tracks; t++) {
					trackSizes[t][s] = getWord(0x32);
				}
			}
			break;
		case Extended:
			for (int t = 0; t < tracks; t++) {
				for (int s = 0; s < sides; s++) {
					trackSizes[t][s] = content[0x34 + (t * 2) + s] * 0x100;
				}
			}
			break;
		}

		base = getDirectoryOffset(directoryBaseOffset, sides, tracks);
		tempBase = base;
		currentDirectoryEntryOffset = base;

		System.out.printf("Directory Offset: $%05x\n", currentDirectoryEntryOffset);

		System.out.printf("TrackSize: $%05x / %02d\n", trackSizes[0][0], trackSizes[0][0]);

		sectorInfobase = trackInfoBaseOffset + 0x18;
		trackInfoText = getString(trackInfoBaseOffset, trackInfoBaseOffset + 0x0b, true);
		sectorSize = getByte(trackInfoBaseOffset + 0x14);
		sectorCount = getByte(trackInfoBaseOffset + 0x15);

		System.out.printf("TrackInfo: $%05x - %s\n", trackInfoBaseOffset, trackInfoText);
		System.out.println("TrackNo:" + getByte(trackInfoBaseOffset + 0x10));
		System.out.println("SideNo:" + getByte(trackInfoBaseOffset + 0x11));
		System.out.println("SectorSize:" + sectorSize);
		System.out.println("SectorCount:" + sectorCount);
		System.out.println("GAP#3 Length:" + getByte(trackInfoBaseOffset + 0x16));
		System.out.println("Filler Byte:" + getByte(trackInfoBaseOffset + 0x17));
		System.out.printf("SectorInfo Start: $%05x\n",
				getByte(trackInfoBaseOffset + 0x14) * getByte(trackInfoBaseOffset + 0x15));
		for (int i = sectorInfobase; i < sectorInfobase + (8 * sectorCount); i += 8) {
			int sectorId = getByte(i + 0x02);
			int sectorSize2 = getByte(i + 0x03);
			System.out.printf("  SectorInfo: Id:$%05x - Size:%s \n", sectorId, sectorSize2);
		}
	}

	@Override
	protected void readEntries(MediaEntry parent) {
		int id = 0;
		while (currentDirectoryEntryOffset < base + 0x1200) {
			MediaEntry entry = null;
			if (isEmptyEntry(currentDirectoryEntryOffset, 8)) {
				tempBase += (trackSizes[0][0] - 0x100) / sectorSize;
				currentDirectoryEntryOffset = tempBase;
			} else {
				String fileName = getString(currentDirectoryEntryOffset + 0x01, currentDirectoryEntryOffset + 0x8,
						false);
				String fileType = getString(currentDirectoryEntryOffset + 0x09, currentDirectoryEntryOffset + 0xb,
						false);
				int extent = content[currentDirectoryEntryOffset + 0x0c];
				int fileSize = getByte(currentDirectoryEntryOffset + 0x0f) * 0x80;
				if (extent > 0) {
					for (MediaEntry me : getRoot().getChildrenList()) {
						if (me.getName().equals(fileName) && me.getType().equals(fileType)) {
							fileSize = me.getSize() + fileSize;
							entry = me;
							entry.setSize(fileSize);
							break;
						}
					}
				}

				String fullName = String.format(
						"%1$s" + (StringUtils.isAsciiPrintable(fileType) ? ".%3$s" : "") + " (%2$4d K )", fileName,
						(int) 1 + (fileSize / 1024), fileType);

				// readContent(entry);
				if (isVisibleInCatalog(currentDirectoryEntryOffset) && extent == 0) {
					entry = new MediaEntry(id, fullName, fileName, fileType, fileSize, 0, 0,
							currentDirectoryEntryOffset + 0x10, null);
					entry.setUserObject(getContainer());
					MediaMountFactory.addChildEntry(parent, entry);
					id++;
				}
				if (entry != null) {
					entry.setFullName(fullName);
				}
				currentDirectoryEntryOffset += 0x20;
			}
		}
	}

	@Override
	protected byte[] readContent(MediaEntry entry) {
		int offset = entry.getOffset();
		int i = 0;
		int block = 0;
		while ((block = content[offset + i]) != 0x00) {
			int track = (int) ((block * 2 + 18) / 9);
			int sector = (int) ((block * 2 + 18) % 9);

			i++;
		}
		return null;
	}

	private boolean isVisibleInCatalog(int directoryOffset) {
		return (content[directoryOffset + 0x0a] & 0x80) == 0;
	}

	private boolean isDeletable(int directoryOffset) {
		return (content[directoryOffset + 0x09] & 0x80) == 0;
	}

	private int getExtent(int directoryOffset) {
		return content[directoryOffset + 0x0c];
	}

	private int getDirectoryOffset(int directoryBaseOffset, int sides, int tracks) {
		int trackOffset = directoryBaseOffset;
		for (int s = 0; s < sides; s++) {
			for (int t = 0; t < tracks; t++) {
				if (!isEmptyEntry(trackOffset, 8)) {
					break;
				}

				trackOffset += trackSizes[t][s];
			}
		}
		return trackOffset;
	}

	private DiskFormat getDiskFormat(String diskInfo) {
		if (diskInfo.contains("EXTENDED CPC DSK")) {
			return DiskFormat.Extended;
		} else if (diskInfo.contains("MV - CPCEMU")) {
			return DiskFormat.Standard;
		} else {
			return DiskFormat.Unkown;
		}

	}

}
