package de.drazil.nerdsuite.storagemedia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MediaEntry {
	private int id;
	private String fullName;
	private String name;
	private String type;
	private int size;
	private int track;
	private int sector;
	private int offset;
	IAttributes attributes;
	private String fontName;

}
