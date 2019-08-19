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
}
