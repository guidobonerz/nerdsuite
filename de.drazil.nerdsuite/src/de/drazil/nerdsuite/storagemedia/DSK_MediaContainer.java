package de.drazil.nerdsuite.storagemedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import de.drazil.nerdsuite.util.NumericConverter;
import lombok.Getter;

public class DSK_MediaContainer extends AbstractBaseMediaContainer {

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

	public DSK_MediaContainer(File file) {
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
	private List<Integer> sectorIdList;

	@Override
	protected void readHeader() {

		diskInfo = getString(0x00, 0x21, true);
		diskInfo = StringEscapeUtils.escapeJava(diskInfo);
		diskFormat = getDiskFormat(diskInfo);
		creator = getString(0x22, 0x2f, true);
		tracks = getByte(0x30);
		sides = getByte(0x31);
		sectorInfobase = trackInfoBaseOffset + 0x18;

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
					trackSizes[t][s] = content[0x34 + (t + s * 2)] * 0x100;
				}
			}
			break;
		default: {
		}
		}

		base = directoryBaseOffset + ((diskType == 0x41 ? 2 : 0) * trackSizes[0][0]);
		System.out.printf("Directory Offset : $%05x\n", base);
		System.out.printf("DiskType         : %s\n", ((diskType == 0x41) ? "System Disk" : "Data Disk"));
		System.out.printf("DiskInfo         : %s\n", diskInfo);
		System.out.printf("Creator          : %s\n", creator);
		System.out.printf("Tracks           : %02d\n", tracks);
		System.out.printf("Sides            : %02d\n", sides);
		System.out.printf("TrackSize        : $%05x / %02d\n", trackSizes[0][0], trackSizes[0][0]);
		sectorIdList = new ArrayList<Integer>();

		for (int tc = 0; tc < tracks; tc++) {
			for (int sc = 0; sc < sides; sc++) {
				sectorInfobase = trackInfoBaseOffset + 0x18;
				trackInfoText = getString(trackInfoBaseOffset, trackInfoBaseOffset + 0x0b, true);
				System.out.printf("\n\nTrackInfo        : $%05x - %s\n", trackInfoBaseOffset, trackInfoText);
				System.out.printf("Unused           : 0c-0f  %04x %04x\n", getWord(trackInfoBaseOffset + 0x0c),
						getWord(trackInfoBaseOffset + 0x0e));
				System.out.printf("TrackNo          : %02d\n", getByte(trackInfoBaseOffset + 0x10));
				System.out.printf("SideNo           : %02d\n", getByte(trackInfoBaseOffset + 0x11));
				System.out.printf("Unused           : 12-13  %04x\n", getWord(trackInfoBaseOffset + 0x12));
				System.out.printf("SectorSize       : %02d\n", sectorSize);
				System.out.printf("SectorCount      : %02d\n", +sectorCount);
				System.out.printf("GAP#3 Length     : %03d\n", getByte(trackInfoBaseOffset + 0x16));
				System.out.printf("Filler Byte      : %02d / %02x\n", getByte(trackInfoBaseOffset + 0x17),
						getByte(trackInfoBaseOffset + 0x17));
				System.out.printf("SectorInfo Start : $%05x\n",
						getByte(trackInfoBaseOffset + 0x14) * getByte(trackInfoBaseOffset + 0x15));

				for (int i = sectorInfobase; i < sectorInfobase + (8 * sectorCount); i += 8) {

					int track = getByte(i + 0x00);
					int side = getByte(i + 0x01);
					int id = getByte(i + 0x02);
					int size = getByte(i + 0x03);
					int SR1 = getByte(i + 0x04);
					int SR2 = getByte(i + 0x05);
					int dataLength = getWord(i + 0x06);
					boolean en = (SR1 & 128) == 128;
					boolean de = (SR1 & 32) == 32;
					boolean nd = (SR1 & 4) == 4;
					boolean ma = (SR1 & 1) == 1;
					boolean cm = (SR2 & 32) == 32;
					boolean dd = (SR2 & 32) == 32;
					boolean md = (SR2 & 1) == 1;
					if (tc == 0) {
						sectorIdList.add(id & 0x0f);
					}
					System.out.printf(
							"  Track: %02d Side: %01d Id: $%02x Size: %04d Byte DataLength: %d EN(%b) DE(%b) ND(%b) MA(%b) CM(%b) DD(%b) MD(%b)\n",
							track, side, id, (128 << size), dataLength, en, de, nd, ma, cm, dd, md);
				}
				trackInfoBaseOffset += trackSizes[tc][sc];
			}
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
			int userLevel = content[currentDirectoryEntryOffset] & 0xff;
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

	private int getSectorIndex(List<Integer> idList, int sector) {
		int id = -1;
		for (int i = 0; i < idList.size(); i++) {
			if (idList.get(i) == sector) {
				id = i;
				break;
			}
		}
		return id;
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
	public byte[] readContent(MediaEntry entry, IMediaEntryWriter writer) throws Exception {
		List<Integer> blockList = (List<Integer>) entry.getDataLocation();
		System.out.println(entry.getFullName());
		int totalSize = 0;
		boolean finished = false;
		boolean isBinary = false;
		boolean checkedHeader = false;

		for (int i = 0; i < blockList.size(); i++) {
			if (finished) {
				break;
			}
			int size = 0x80 << sectorSize;
			int block = blockList.get(i) & 0xff;
			int track = ((int) ((block * 2 + 18) / sectorCount)) - (diskType == 0x41 ? 0 : 2);
			int sector = (int) ((block * 2 + 18) % sectorCount);
			int sectorIndex = getSectorIndex(sectorIdList, sector + 1);
			int trackOffset = 0x100 + trackSizes[track][0] * track;
			int sectorOffset = 0x100 + size * sectorIndex;

			for (int s = sector; s < sector + 2 && !finished; s++) {
				int offset = trackOffset + sectorOffset;

				if (!checkedHeader) {
					if (isBinary = hasHeader(content, offset) && i == 0) {
						totalSize = getWord(offset + 0x18) + 0x80;
						System.out.printf("Name  : %s\n", getString(offset + 0x01, offset + 0x08, true));
						System.out.printf("Type  : %s\n", getString(offset + 0x09, offset + 0x0b, true));
						System.out.printf("Adress: %04x\n", getWord(offset + 0x15));
						System.out.printf("Length: %04x\n", getWord(offset + 0x18));
						System.out.printf("Length: %04x\n", getWord(offset + 0x40));
					}
					if (!isBinary) {
						isBinary = !isAscii(content, offset, 0x80);
						totalSize = entry.getSize();
					}
					checkedHeader = true;
				}
				if (isBinary) {
					if (totalSize <= size) {
						size = totalSize;
						finished = true;
					}
				} else {
					boolean found = false;
					for (int j = offset; j < offset + size; j++) {
						if (content[j] == 0x1a) {
							found = true;
							size = j - offset;
							finished = true;
							break;
						}
					}
					if (!found) {
						size = 0x200;
					}
				}

				writer.write(entry, offset, size, finished);
				System.out.printf("%02d %02x T:%02x S:%02x %05x size:%d \n", i, block, track, sector, offset, size);

				sectorOffset += (size * 2);
				if (sectorOffset > trackSizes[track][0]) {
					sectorOffset = 0x100 + 0x200;
				} else if (sectorOffset == trackSizes[track][0]) {
					sectorOffset += 0x100;
				}
				totalSize -= size;
			}
		}
		return null;
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

	private boolean hasHeader(byte[] content, int offset) {
		int checkSum = 0;
		int headerSum = 0;

		checkSum = NumericConverter.getWordAsInt(content, offset + 0x43);
		for (int i = offset; i < offset + 0x42; i++) {
			headerSum += NumericConverter.getByteAsInt(content, i);
		}

		return checkSum == headerSum;
	}

	private boolean isAscii(byte[] content, int offset, int length) {
		boolean isAscii = true;

		for (int i = offset; i < offset + length; i++) {
			if (!Character.isAlphabetic(content[i])) {
				isAscii = false;
				break;
			}
		}
		return isAscii;
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
