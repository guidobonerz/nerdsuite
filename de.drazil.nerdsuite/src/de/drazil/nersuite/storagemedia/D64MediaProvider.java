package de.drazil.nersuite.storagemedia;

import java.util.HashMap;
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

	private Map<String, Integer> trackOffsetMap = null;
	private int sectorSize = 0x100;
	private int directoryTrack = 18;
	private List<AsciiMap> list;

	public D64MediaProvider() {
		super();
		trackOffsetMap = new HashMap<>();
		trackOffsetMap.put("1", 0x0);
		trackOffsetMap.put("2", 0x1500);
		trackOffsetMap.put("3", 0x2a00);
		trackOffsetMap.put("4", 0x3f00);
		trackOffsetMap.put("5", 0x5400);
		trackOffsetMap.put("6", 0x6900);
		trackOffsetMap.put("7", 0x7e00);
		trackOffsetMap.put("8", 0x9300);
		trackOffsetMap.put("9", 0xa800);
		trackOffsetMap.put("10", 0xbd00);
		trackOffsetMap.put("11", 0xd200);
		trackOffsetMap.put("12", 0xe700);
		trackOffsetMap.put("13", 0xfc00);
		trackOffsetMap.put("14", 0x11100);
		trackOffsetMap.put("15", 0x12600);
		trackOffsetMap.put("16", 0x13b00);
		trackOffsetMap.put("17", 0x15000);
		trackOffsetMap.put("18", 0x16500);
		trackOffsetMap.put("19", 0x17800);
		trackOffsetMap.put("20", 0x18b00);
		trackOffsetMap.put("21", 0x19e00);
		trackOffsetMap.put("22", 0x1b100);
		trackOffsetMap.put("23", 0x1c400);
		trackOffsetMap.put("24", 0x1d700);
		trackOffsetMap.put("25", 0x1ea00);
		trackOffsetMap.put("26", 0x1fc00);
		trackOffsetMap.put("27", 0x20e00);
		trackOffsetMap.put("28", 0x22000);
		trackOffsetMap.put("29", 0x23200);
		trackOffsetMap.put("30", 0x24400);
		trackOffsetMap.put("31", 0x25600);
		trackOffsetMap.put("32", 0x26700);
		trackOffsetMap.put("33", 0x27800);
		trackOffsetMap.put("34", 0x28900);
		trackOffsetMap.put("35", 0x29a00);
		trackOffsetMap.put("36", 0x2ab00);
		trackOffsetMap.put("37", 0x2bc00);
		trackOffsetMap.put("38", 0x2cd00);
		trackOffsetMap.put("39", 0x2de00);
		trackOffsetMap.put("30", 0x2ef00);
	}

	@Override
	public MediaEntry[] getEntries() {
		return mediaEntryList.toArray(new MediaEntry[mediaEntryList.size()]);
	}

	@Override
	public boolean hasEntries() {
		return !mediaEntryList.isEmpty();
	}

	@Override
	protected void parse() {
		ICPU cpu = new CPU_6510();

		int currentTrackBamOffset = trackOffsetMap.get(String.valueOf(directoryTrack));
		int currentDirTrack = directoryTrack;

		int currentDirEntryBaseOffset = currentTrackBamOffset + sectorSize;
		int currentDirEntryOffset = currentDirEntryBaseOffset;

		System.out.printf("Next entry at track:%d sector:%d\n", content[currentDirEntryOffset],
				content[currentDirEntryOffset + 0x1]);
		mediaEntryList.add(new MediaEntry(getFilename(currentTrackBamOffset + 0x90, 0x0f, 0x0a, false), 0, ""));

		while (currentDirTrack != 0) {
			currentDirTrack = content[currentDirEntryOffset];
			int nextSector = content[currentDirEntryOffset + 0x1];
			while (currentDirEntryOffset < currentDirEntryBaseOffset + 0xe0) {
				if (content[currentDirEntryOffset + 0x5] != 0) {
					mediaEntryList.add(new MediaEntry(getFilename(currentDirEntryOffset + 0x5, 0x10, 0xa0, false),
							getFileSize(cpu, currentDirEntryOffset + 0x1e),
							getFileType(content[currentDirEntryOffset + 0x2])));
				}
				currentDirEntryOffset += 0x20;
			}
			currentDirEntryBaseOffset = currentTrackBamOffset + (nextSector * sectorSize);
			currentDirEntryOffset = currentDirEntryBaseOffset;
		}
	}

	private int getFileSize(ICPU cpu, int start) {
		return cpu.getWord(content, start);
	}

	private String getFilename(int start, int length, int skipByte, boolean invert) {
		System.out.println("get filename");
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < start + length; i++) {
			int c = content[i];
			if (c != skipByte) {

				// sb.append(new String(Character.toChars(0xe000 + getChar(invert ? (c + 0x40) :
				// c))));
				sb.append(new String(Character.toChars(0xe100 + (invert ? (c | 0x80) : c))));
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

	private int getChar(int c) {
		int result = 0;
		try {

			if (list == null) {
				Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
				ObjectMapper mapper = new ObjectMapper();
				JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, AsciiMap.class);
				list = mapper.readValue(bundle.getEntry("configuration/petascii_map.json"), listType);
			}
			result = Integer.parseInt(list.stream().filter(le -> Integer.parseInt(le.getSource(), 16) == (c & 0xff))
					.findAny().get().getTarget(), 16);
			if (result == 0) {
				System.out.println("symbol:" + c + " not found");
			}

		} catch (Exception e) {

		}
		return result;
	}
}
