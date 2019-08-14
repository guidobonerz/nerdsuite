package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ProjectFile {
	private String id;
	private String name;
	@JsonInclude(Include.NON_NULL)
	private String mountLocation;

	public ProjectFile(String id, String name) {
		setId(id);
		setName(name);
	}
}
