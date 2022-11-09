package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class PlatformData {
    private String platformId;
    private String cpuInstructionSource;
    private String basicInstructionSource;
    private String graphicFormatSource;
    private List<String> commonStartAdresses;
    private String charMapSource;
    private List<Address> platformAddressList;
    private List<Pointer> platformPointerList;
    private List<PlatformColor> colorPalette;

}
