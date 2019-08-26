package de.drazil.nerdsuite.storagemedia;

import org.apache.commons.lang3.StringUtils;

import de.drazil.nerdsuite.util.NumericConverter;
import lombok.Getter;

public class DSK_MediaManager extends AbstractBaseMediaManager {

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

	public DSK_MediaManager() {
	}

	@Override
	protected byte[] readContent(MediaEntry entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void readStructure() {

		String diskInfo = getString(0x00, 0x21, true);
		String creator = getString(0x22, 0x2f, true);
		int tracks = getByte(0x30);
		int sides = getByte(0x31);

		System.out.println("DiskInfo:" + diskInfo);
		System.out.println("Creator:" + creator);
		System.out.println("Tracks:" + tracks);
		System.out.println("Sides:" + sides);

		DiskFormat diskFormat = getDiskFormat(diskInfo);

		int trackInfoBaseOffset = 0x100;
		int directoryBaseOffset = trackInfoBaseOffset + 0x100;

		trackSizes = new int[sides][tracks];

		switch (diskFormat) {
		case Standard:
			for (int s = 0; s < sides; s++) {
				for (int t = 0; t < tracks; t++) {
					trackSizes[s][t] = getWord(0x32, true);
				}
			}
			break;
		case Extended:
			for (int t = 0; t < tracks; t++) {
				for (int s = 0; s < sides; s++) {
					trackSizes[s][t] = content[0x34 + (t * 2) + s] * 0x100;
				}
			}
			break;
		}
		int currentDirectoryOffset = getDirectoryOffset(directoryBaseOffset, sides, tracks);

		while (!isEmptyTrack(currentDirectoryOffset)) {
			if (isVisibleInCatalog(currentDirectoryOffset)) {
				int fileSize = (content[currentDirectoryOffset + 0x0f] * 0x80) & 0xfffff;
				String fileName = getString(currentDirectoryOffset + 0x01, currentDirectoryOffset + 0x8, false);
				String fileType = getString(currentDirectoryOffset + 0x09, currentDirectoryOffset + 0xb, false);

				fileName = String.format("%1$s.%2$3s%3$1s  %4$4dK", StringUtils.rightPad(fileName, 8, ' '), fileType,
						!isDeletable(currentDirectoryOffset) ? "*" : " ", (int) 1 + (fileSize / 1024));
				MediaEntry entry = new MediaEntry(fileName, fileSize, "", 0, 0, new CPMFileAttributes(false, false, 0),
						"Amstrad CPC correct|6");
				mediaEntryList.add(entry);
			}
			currentDirectoryOffset += 0x20;
		}

		System.out.printf("Directory Offset: $%05x\n", currentDirectoryOffset);

		System.out.printf("TrackSize: $%05x / %02d\n", trackSizes[0][0], trackSizes[0][0]);

		int sectorInfobase = trackInfoBaseOffset + 0x18;
		String trackInfoText = getString(trackInfoBaseOffset, trackInfoBaseOffset + 0x0b, true);
		int sectorSize = getByte(trackInfoBaseOffset + 0x14);
		int sectorCount = getByte(trackInfoBaseOffset + 0x15);

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

	private boolean isVisibleInCatalog(int directoryOffset) {
		return (content[directoryOffset + 0x0a] & 0x80) == 0;
	}

	private boolean isDeletable(int directoryOffset) {
		return (content[directoryOffset + 0x09] & 0x80) == 0;
	}

	private int getDirectoryOffset(int directoryBaseOffset, int sides, int tracks) {
		int trackOffset = directoryBaseOffset;
		for (int s = 0; s < sides; s++) {
			for (int t = 0; t < tracks; t++) {
				if (!isEmptyTrack(trackOffset)) {
					break;
				}

				trackOffset += trackSizes[s][t];
			}
		}
		return trackOffset;
	}

	private boolean isEmptyTrack(int trackInfoBase) {
		int lastValue = 0;
		int count = 0;
		for (int i = trackInfoBase; i <= trackInfoBase + 0x5; i++) {
			if (i > 0) {
				count += (content[i] == lastValue ? 1 : 0);
			}
			lastValue = content[i];
		}
		return count == 0x5;
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

	private String getString(int start, int end, boolean skipCharCheck) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= end; i++) {
			char c = (char) content[i];
			if (Character.isLetter(c) || Character.isDigit(c) || skipCharCheck) {
				sb.append(Character.toString(c));
			}
		}
		return sb.toString();
	}

	private int getWord(int start, boolean swap) {
		return NumericConverter.getWordAsInt(content, start, swap);
	}

	private int getByte(int start) {
		return NumericConverter.getByteAsInt(content, start);
	}

}
