package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class PlatformData
{
	private List<Address> platformAddressList;
	private List<Pointer> platformPointerList;
	private String cpuInstructionSource;
	private List<PlatformColor> colorPalette;
}
