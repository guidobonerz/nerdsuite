package de.drazil.nerdsuite.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ProjectFile {
	private String id;
	private String name;
	private String mountLocation;

	public ProjectFile(String id, String name) {
		setId(id);
		setName(name);
	}
}
