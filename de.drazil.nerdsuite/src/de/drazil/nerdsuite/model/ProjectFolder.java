package de.drazil.nerdsuite.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ProjectFolder {
	private String id;
	private String name;

	public ProjectFolder(String id, String name) {
		setId(id);
		setName(name);
	}
}
