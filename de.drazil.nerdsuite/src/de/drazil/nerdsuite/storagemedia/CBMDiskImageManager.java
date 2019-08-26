package de.drazil.nerdsuite.storagemedia;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.model.AsciiMap;

public abstract class CBMDiskImageManager extends AbstractBaseMediaManager {

	private int sectorSize = 0x100;
	protected int directoryTrack = 18;
	protected int directorySectorInterleave = 1;
	protected int fileSectorInterleave = 1;
	private List<AsciiMap> list;
	protected int[] trackOffsets;

	public CBMDiskImageManager() {
		super();
	}

	@Override
	protected void readStructure() {
		ICPU cpu = new CPU_6510();
		int bamOffset = trackOffsets[directoryTrack - 1];
		int currentDirTrack = directoryTrack;
		int currentDirEntryBaseOffset = bamOffset + sectorSize;
		int currentDirEntryOffset = currentDirEntryBaseOffset;
		String name = getFilename(bamOffset + 0x90, 0x0f, 0x0a, false, true);
		String diskId = new String(Character.toChars(getChar(content[bamOffset + 0xa2], false, true)))
				+ new String(Character.toChars(getChar(content[bamOffset + 0xa3], false, true)));
		String dummy = new String(Character.toChars(getChar(content[bamOffset + 0xa4], false, true)));
		String dosType = new String(Character.toChars(getChar(content[bamOffset + 0xa5], false, true)))
				+ new String(Character.toChars(getChar(content[bamOffset + 0xa6], false, true)));
		String diskName = name + "\uee20" + diskId + dummy + dosType;
		diskName = StringUtils.rightPad(diskName, 22, "\uee20");
		mediaEntryList.add(new MediaEntry(diskName, 0, "\uee20", 0, 0, new CBMFileAttributes(false, false)));

		while (currentDirTrack != 0) {
			currentDirTrack = content[currentDirEntryOffset] & 0xff;
			int nextSector = content[currentDirEntryOffset + 0x1] & 0xff;
			int id = 0;
			// while (currentDirEntryOffset < currentDirEntryBaseOffset + 0xe0) {
			while (currentDirEntryOffset < currentDirEntryBaseOffset + 0x100) {
				if (content[currentDirEntryOffset + 0x5] != 0) {
					String fileName = getFilename(currentDirEntryOffset + 0x5, 0x0f, 0xa0, false, false);
					int fileSize = getFileSize(cpu, currentDirEntryOffset + 0x1e);
					byte fileType = content[currentDirEntryOffset + 0x02];
					int fileTrack = content[currentDirEntryOffset + 0x03];
					int fileSector = content[currentDirEntryOffset + 0x04];

					String fileTypeName = getFileType(fileType);
					boolean isClosed = isClosed(fileType);
					boolean isLocked = isLocked(fileType);

					fileName = StringUtils.rightPad(fileName, 19, "\uee20");
					if (content[currentDirEntryOffset + 0x02] != 0) {
						MediaEntry me = new MediaEntry(fileName, fileSize, fileTypeName, fileTrack, fileSector,
								new CBMFileAttributes(isLocked, isClosed));
						mediaEntryList.add(me);
					}
					// byte[] data = readContent(me);
					try {
						// BinaryFileHandler.write(
						// new File(Configuration.WORKSPACE_PATH.getAbsolutePath(), "test" + id +
						// ".prg"), data);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				currentDirEntryOffset += 0x20;
				id++;
			}
			currentDirEntryBaseOffset = bamOffset + (nextSector * sectorSize);
			currentDirEntryOffset = currentDirEntryBaseOffset;

		}
	}

	@Override
	protected byte[] readContent(MediaEntry entry) {
		byte[] fileContent = null;
		if (!entry.getType().trim().equals("DEL")) {
			int fileTrack = entry.getTrack();
			int fileSector = entry.getSector();
			while (fileTrack != 0) {
				int fileSectorOffset = trackOffsets[fileTrack - 1] + fileSector * 0x100;
				fileTrack = content[fileSectorOffset] & 0xff;
				fileSector = content[fileSectorOffset + 0x01] & 0xff;
				int copySize = (fileTrack != 0 ? 0xfe : fileSector) & 0xff;
				if (fileContent == null) {
					fileContent = new byte[copySize];
					System.arraycopy(content, fileSectorOffset + 0x02, fileContent, 0, copySize);
				} else {
					byte[] sectorData = new byte[copySize];
					System.arraycopy(content, fileSectorOffset + 0x02, sectorData, 0, copySize);
					byte[] temp = new byte[fileContent.length + sectorData.length];
					System.arraycopy(fileContent, 0, temp, 0, fileContent.length);
					System.arraycopy(sectorData, 0, temp, fileContent.length, copySize);
					fileContent = temp;
				}
				System.out.printf("Next:%05x %02d / %02d\n", fileSectorOffset, fileTrack, fileSector);
			}
		}
		return fileContent;
	}

	private int getFileSize(ICPU cpu, int start) {
		return cpu.getWord(content, start);
	}

	private String getFilename(int start, int length, int skipByte, boolean shift, boolean invers) {

		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= start + length; i++) {
			int c = content[i];
			if (c != skipByte) {
				// System.out.printf("char: %02x\n", c);
				sb.append(new String(Character.toChars(getChar(c & 0xff, shift, invers))));
			}
		}
		return sb.toString();
	}

	private boolean isLocked(byte type) {
		return (type & 64) == 64;
	}

	private boolean isClosed(byte type) {
		return (type & 128) == 128;
	}

	private String getFileType(byte type) {
		String fileType = "unkown";

		switch ((int) type & 0b111) {
		case 0x0: {
			fileType = "DEL";
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
		return fileType;
	}

	private int getChar(int c, boolean shift, boolean invers) {
		int mappedChar = 0;

		try {

			if (list == null) {
				Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
				ObjectMapper mapper = new ObjectMapper();
				JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, AsciiMap.class);
				list = mapper.readValue(bundle.getEntry("configuration/petascii_map.json"), listType);
			}

			AsciiMap map = list.stream().filter(le -> le.getId() == (c & 0xff)).findFirst().get();

			int cx = map.getScreenCode();
			if (cx == 0) {
				System.out.println(c + " is empty");
			}

			mappedChar = (!shift ? 0xee00 : 0xef00) + cx | (invers ? 0x80 : 0);

			// System.out.println(Integer.toHexString(cx) + " " +
			// Integer.toHexString(result));
		} catch (Exception e) {

		}
		return mappedChar;
	}

}
