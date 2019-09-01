package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public class D64_MediaManager extends CBMDiskImageManager {

	public D64_MediaManager(File file) {
		super(file);
		directorySectorInterleave = 3;
		fileSectorInterleave = 10;
		trackOffsets = new int[] { 0x00000, 0x01500, 0x02a00, 0x03f00, 0x05400, 0x06900, 0x07e00, 0x09300, 0x0a800,
				0x0bd00, 0x0d200, 0x0e700, 0x0fc00, 0x11100, 0x12600, 0x13b00, 0x15000, 0x16500, 0x17800, 0x18b00,
				0x19e00, 0x1b100, 0x1c400, 0x1d700, 0x1ea00, 0x1fc00, 0x20e00, 0x22000, 0x23200, 0x24400, 0x25600,
				0x26700, 0x27800, 0x28900, 0x29a00, 0x2ab00, 0x2bc00, 0x2cd00, 0x2de00, 0x2ef00 };
	}
}
