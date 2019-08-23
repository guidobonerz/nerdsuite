package de.drazil.nerdsuite.storagemedia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MediaEntry {
	private String name;
	private int size;
	private String type;
	private int track;
	private int sector;

}
