package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class PlatformData {
	private String cpuInstructionSource;
	private String basicInstructionSource;
	private int basicVersion;
	private String imagingServiceClass;
	private List<ImageType> imageTypes;
	private List<Address> platformAddressList;
	private List<Pointer> platformPointerList;
	private List<PlatformColor> colorPalette;

}
