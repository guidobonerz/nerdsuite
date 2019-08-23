package de.drazil.nersuite.storagemedia;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.model.AsciiMap;

public class D64MediaProvider extends AbstractBaseMediaProvider {

	private int sectorSize = 0x100;
	private int directoryTrack = 18;
	private List<AsciiMap> list;
	int[] trackOffsets;

	public D64MediaProvider() {
		super();
		trackOffsets = new int[] { 0x0, 0x1500, 0x2a00, 0x3f00, 0x5400, 0x6900, 0x7e00, 0x9300, 0xa800, 0xbd00, 0xd200,
				0xe700, 0xfc00, 0x11100, 0x12600, 0x13b00, 0x15000, 0x16500, 0x17800, 0x18b00, 0x19e00, 0x1b100,
				0x1c400, 0x1d700, 0x1ea00, 0x1fc00, 0x20e00, 0x22000, 0x23200, 0x24400, 0x25600, 0x26700, 0x27800,
				0x28900, 0x29a00, 0x2ab00, 0x2bc00, 0x2cd00, 0x2de00, 0x2ef00 };
	}

	@Override
	public MediaEntry[] getEntries() {
		return mediaEntryList.toArray(new MediaEntry[mediaEntryList.size()]);
	}

	@Override
	public boolean hasEntries() {
		return true;
	}

	@Override
	protected void readStructure() {

		ICPU cpu = new CPU_6510();

		int bamOffset = trackOffsets[directoryTrack - 1];
		int currentDirTrack = directoryTrack;

		int currentDirEntryBaseOffset = bamOffset + sectorSize;
		int currentDirEntryOffset = currentDirEntryBaseOffset;

		String diskName = getFilename(bamOffset + 0x90, 0x0f, 0x0a, false, true);
		String diskId = "" + new String(Character.toChars(getChar(content[bamOffset + 0xa2], true, true)))
				+ new String(Character.toChars(getChar(content[bamOffset + 0xa3], true, true)));
		String dummy = "" + new String(Character.toChars(getChar(content[bamOffset + 0xa4], true, true)));
		String dosType = "" + new String(Character.toChars(getChar(content[bamOffset + 0xa5], true, true)))
				+ new String(Character.toChars(getChar(content[bamOffset + 0xa6], true, true)));
		mediaEntryList.add(new MediaEntry(diskName + " " + diskId + dummy + dosType, 0, ""));
		System.out.println("-------------------------");
		while (currentDirTrack != 0) {
			currentDirTrack = content[currentDirEntryOffset];
			int nextSector = content[currentDirEntryOffset + 0x1];
			while (currentDirEntryOffset < currentDirEntryBaseOffset + 0xe0) {
				if (content[currentDirEntryOffset + 0x5] != 0) {
					String fileName = getFilename(currentDirEntryOffset + 0x5, 0x0f, 0xa0, true, false);
					int fileSize = getFileSize(cpu, currentDirEntryOffset + 0x1e);
					int fileTrack = content[currentDirEntryBaseOffset + 0x03];
					int fileSector = content[currentDirEntryBaseOffset + 0x04];
					int block = 1;
					String fileType = getFileType(content[currentDirEntryOffset + 0x02]);
					mediaEntryList.add(new MediaEntry(fileName, fileSize, fileType));
					if (!fileType.equals("DEL")) {
						while (fileTrack != 0) {
							int fileSectorOffset = trackOffsets[fileTrack - 1] + fileSector * 0x100;
							fileTrack = content[fileSectorOffset];
							fileSector = content[fileSectorOffset + 0x01];
							System.out.printf("%03d  Next:%05x %02d / %02d\n", block, fileSectorOffset, fileTrack,
									fileSector);
							block++;
						}
					}
				}
				currentDirEntryOffset += 0x20;
				System.out.println("-------------------------");
			}
			currentDirEntryBaseOffset = bamOffset + (nextSector * sectorSize);
			currentDirEntryOffset = currentDirEntryBaseOffset;
		}
	}

	@Override
	protected void readContent() {
		// TODO Auto-generated method stub

	}

	private int getFileSize(ICPU cpu, int start) {
		return cpu.getWord(content, start);
	}

	private String getFilename(int start, int length, int skipByte, boolean shift, boolean invers) {

		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= start + length; i++) {
			int c = content[i];
			if (c != skipByte) {
				sb.append(new String(Character.toChars(getChar(c, shift, invers))));
			}
		}
		return sb.toString();
	}

	private String getFileType(byte type) {
		String fileType = "unkown";
		boolean locked = (type & 64) == 64;
		boolean closed = (type & 128) == 128;

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
		return fileType + (locked ? "<" : " ");
	}

	private int getChar(int c, boolean shift, boolean invers) {
		int result = 0;
		try {
			int cx = c & 0x7f;
			if (list == null) {
				Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
				ObjectMapper mapper = new ObjectMapper();
				JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, AsciiMap.class);
				list = mapper.readValue(bundle.getEntry("configuration/petascii_map.json"), listType);
			}

			/*
			 * for (AsciiMap am : list) { int v = Integer.parseInt(am.getSource(), 16); if
			 * (v == (cx & 0xff)) { int r = Integer.parseInt(am.getTarget(), 16); result =
			 * r; break; } }
			 */
			/*
			 * result = Integer.parseInt(list.stream().filter(le ->
			 * Integer.parseInt(le.getSource(), 16) == (cx & 0xff))
			 * .findAny().get().getTarget(), 16);
			 * 
			 * if (result == 0) { System.out.println("symbol:" + c + " not found"); }
			 */
			int base = 0;
			if (!shift && !invers) {
				base = 0xe000;
			} else if (shift && !invers) {
				base = 0xe100;
			} else if (!shift && invers) {
				base = 0xe200;
			} else if (shift && invers) {
				base = 0xe300;
			}

			result = base + (cx | 0x80);

			System.out.println(Integer.toHexString(cx) + " " + Integer.toHexString(result));
		} catch (Exception e) {

		}
		return result;
	}

	@Override
	public byte[] getContentById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
