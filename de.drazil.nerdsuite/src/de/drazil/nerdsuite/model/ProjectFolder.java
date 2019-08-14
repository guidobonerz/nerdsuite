package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ProjectFolder {
	private String id;
	private String name;
	private String mountLocation;
	private List<ProjectFile> openFiles;

	public ProjectFolder(String id, String name) {
		setId(id);
		setName(name);
	}
}
