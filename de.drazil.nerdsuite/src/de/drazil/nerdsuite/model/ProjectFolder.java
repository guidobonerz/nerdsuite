package de.drazil.nerdsuite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ProjectFolder {
	private String id;
	private String parentId;
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String mountLocation;
	@JsonInclude(Include.NON_NULL)
	private List<ProjectFile> openFiles;

	public ProjectFolder(String id, String parentId, String name) {
		setId(id);
		setParentId(parentId);
		setName(name);
	}
}
