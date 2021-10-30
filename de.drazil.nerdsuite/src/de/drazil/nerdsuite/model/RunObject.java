package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class RunObject {
	public static enum Source {
		Program, DiskImage
	}

	public static enum Mode {
		None, Run, Mount
	}

	private byte payload[];
	private int startAdress = -1;
	private Source source;
	private Mode mode;
}
