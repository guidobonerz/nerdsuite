package de.drazil.nersuite.storagemedia;

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
	private byte[] content;

	public MediaEntry(String name, int size, String type) {
		this(name, size, type, null);
	}
}
