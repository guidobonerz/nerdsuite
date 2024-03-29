package de.drazil.nerdsuite.storagemedia;

import java.io.File;

public class D71_MediaContainer extends CBMDiskImageContainer {

	public D71_MediaContainer(File file) {
		super(file);
		directorySectorInterleave = 3;
		fileSectorInterleave = 6;
		trackOffsets = new int[] { 0x00000, 0x01500, 0x02A00, 0x03F00, 0x05400, 0x06900, 0x07E00, 0x09300, 0x0A800,
				0x0BD00, 0x0D200, 0x0E700, 0x0FC00, 0x11100, 0x12600, 0x13B00, 0x15000, 0x16500, 0x17800, 0x18B00,
				0x19E00, 0x1B100, 0x1C400, 0x1D700, 0x1EA00, 0x1FC00, 0x20E00, 0x22000, 0x23200, 0x24400, 0x25600,
				0x26700, 0x27800, 0x28900, 0x29A00, 0x2AB00, 0x2C000, 0x2D500, 0x2EA00, 0x2FF00, 0x31400, 0x32900,
				0x33E00, 0x35300, 0x36800, 0x37D00, 0x39200, 0x3A700, 0x3BC00, 0x3D100, 0x3E600, 0x3FB00, 0x41000,
				0x42300, 0x43600, 0x44900, 0x45C00, 0x46F00, 0x48200, 0x49500, 0x4A700, 0x4B900, 0x4CB00, 0x4DD00,
				0x4EF00, 0x50100, 0x51200, 0x52300, 0x53400, 0x54500 };
	}
}
