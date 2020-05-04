package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

public class DSK_MediaReader extends AbstractBaseMediaReader {

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

	public DSK_MediaReader(File file) {
		super(file);
	}

	private String diskInfo;
	private String creator;
	private int tracks;
	private int sides;
	private int diskType;
	private DiskFormat diskFormat;
	private int trackInfoBaseOffset = 0x100;
	private int directoryBaseOffset = trackInfoBaseOffset + 0x100;
	private int base;
	// private int tempBase;
	// private int currentDirectoryEntryOffset;
	private int sectorInfobase;
	private String trackInfoText;
	private int sectorSize;
	private int sectorCount;

	@Override
	protected void readHeader() {

		diskInfo = getString(0x00, 0x21, true);
		diskFormat = getDiskFormat(diskInfo);
		creator = getString(0x22, 0x2f, true);
		tracks = getByte(0x30);
		sides = getByte(0x31);
		sectorInfobase = trackInfoBaseOffset + 0x18;
		trackInfoText = getString(trackInfoBaseOffset, trackInfoBaseOffset + 0x0b, true);
		sectorSize = getByte(trackInfoBaseOffset + 0x14);
		sectorCount = getByte(trackInfoBaseOffset + 0x15);
		diskType = getByte(sectorInfobase + 0x02);

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

		base = directoryBaseOffset + ((diskType == 0x41 ? 2 : 0) * trackSizes[0][0]);
		System.out.printf("Directory Offset: $%05x\n", base);
		System.out.println("DiskMode: " + ((diskType == 0x41) ? "System Disk" : "Data Disk"));
		System.out.println("DiskInfo:" + diskInfo);
		System.out.println("Creator:" + creator);
		System.out.println("Tracks:" + tracks);
		System.out.println("Sides:" + sides);
		System.out.printf("TrackSize: $%05x / %02d\n", trackSizes[0][0], trackSizes[0][0]);

		System.out.printf("TrackInfo: $%05x - %s\n", trackInfoBaseOffset, trackInfoText);
		System.out.printf("unused: 0c-0f  %04x %04x\n", getWord(trackInfoBaseOffset + 0x0c),
				getWord(trackInfoBaseOffset + 0x0e));
		System.out.println("TrackNo:" + getByte(trackInfoBaseOffset + 0x10));
		System.out.println("SideNo:" + getByte(trackInfoBaseOffset + 0x11));
		System.out.printf("unused: 12-13  %04x\n", getWord(trackInfoBaseOffset + 0x12));
		System.out.println("SectorSize:" + sectorSize);
		System.out.println("SectorCount:" + sectorCount);
		System.out.println("GAP#3 Length:" + getByte(trackInfoBaseOffset + 0x16));
		System.out.printf("Filler Byte: %02d / %02x\n", getByte(trackInfoBaseOffset + 0x17),
				getByte(trackInfoBaseOffset + 0x17));
		System.out.printf("SectorInfo Start: $%05x\n",
				getByte(trackInfoBaseOffset + 0x14) * getByte(trackInfoBaseOffset + 0x15));

		for (int i = sectorInfobase; i < sectorInfobase + (8 * sectorCount); i += 8) {
			int sectorId = getByte(i + 0x02);
			int sectorSize2 = getByte(i + 0x03);
			System.out.printf("  SectorInfo: Id:$%05x - Size:%s \n", sectorId, sectorSize2);
		}
	}

	@Override
	public void readEntries(MediaEntry parent) {
		int tempBase = base;
		int currentDirectoryEntryOffset = base;
		int id = 0;
		boolean hasMoreEntries = true;
		List<Integer> contentOffsetList = null;
		while (hasMoreEntries) {
			MediaEntry entry = null;
			String fileName = getString(currentDirectoryEntryOffset + 0x01, currentDirectoryEntryOffset + 0x8, false);
			String fileType = getString(currentDirectoryEntryOffset + 0x09, currentDirectoryEntryOffset + 0xb, false);
			int extent = content[currentDirectoryEntryOffset + 0x0c] & 0xff;
			int fileSize = getByte(currentDirectoryEntryOffset + 0x0f) * 0x80;
			if (extent > 0) {
				for (MediaEntry me : parent.getChildrenList()) {
					if (me.getName().equals(fileName) && me.getType().equals(fileType)) {
						fileSize = me.getSize() + fileSize;
						entry = me;
						entry.setSize(fileSize);
						addContentOffset(contentOffsetList, currentDirectoryEntryOffset);
						break;
					}
				}
			}
			String fullName = String.format(
					"%1$s" + (StringUtils.isAsciiPrintable(fileType) ? ".%3$s" : "") + " (%2$4d K )", fileName,
					(int) 1 + (fileSize / 1024), fileType);

			if (isVisibleInCatalog(currentDirectoryEntryOffset) && extent == 0 && !StringUtils.isBlank(fileName)) {
				contentOffsetList = new ArrayList<>();
				entry = new MediaEntry(id, fullName, fileName, fileType, fileSize, 0, 0,
						currentDirectoryEntryOffset + 0x10, null);
				entry.setUserObject(getContainer());
				entry.setDataLocation(contentOffsetList);
				addContentOffset(contentOffsetList, currentDirectoryEntryOffset);
				MediaFactory.addChildEntry(parent, entry);

			}
			if (entry != null) {
				entry.setFullName(fullName);
			}
			currentDirectoryEntryOffset += 0x20;
			id++;
			if (isEmptyEntry(currentDirectoryEntryOffset, 0x10, 0)
					|| isEmptyEntry(currentDirectoryEntryOffset, 0x10, 0xe5)) {
				hasMoreEntries = true;
				tempBase = currentDirectoryEntryOffset - 0x10 + (tempBase + 0x200 - currentDirectoryEntryOffset) + 0x10;
				currentDirectoryEntryOffset = tempBase;
			}
			if (currentDirectoryEntryOffset > base + 0x800) {
				hasMoreEntries = false;
			}
		}
	}

	private void addContentOffset(List<Integer> offsetList, int offset) {
		for (int i = (offset + 0x10); i < (offset + 0x20); i++) {
			int v = content[i] & 0xff;
			if (v == 0) {
				break;
			}
			offsetList.add(new Integer(v));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readContent(MediaEntry entry, IContentReader writer) throws Exception {
		List<Integer> blockList = (List<Integer>) entry.getDataLocation();
		System.out.println(entry.getFullName());
		for (int i = 0; i < blockList.size(); i++) {
			int block = blockList.get(i) & 0xff;
			int track = (int) ((block * 2 + 18) / 9);
			int sector = (int) ((block * 2 + 18) % 9);
			// int offset = (trackSizes[0][0] * track + 0x200 * sector) - (trackSizes[0][0]
			// * (sides + 1))
			// + (diskFormat == DiskFormat.Standard ? 0x200 : 0) + base;
			int offset = base + (trackSizes[0][0] * track + 0x200 * sector) - (trackSizes[0][0] * (sides + 1))
					+ (diskFormat == DiskFormat.Extended ? 0x300 : 0);
			System.out.printf("%02d %02x T:%02x S:%02x %05x\n", i, block, track, sector, offset);
		}
	}

	private boolean isVisibleInCatalog(int directoryOffset) {
		return (content[directoryOffset + 0x0a] & 0x80) == 0 && content[directoryOffset] == 0;
	}

	private boolean isDeletable(int directoryOffset) {
		return (content[directoryOffset + 0x09] & 0x80) == 0;
	}

	private int getExtent(int directoryOffset) {
		return content[directoryOffset + 0x0c];
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
