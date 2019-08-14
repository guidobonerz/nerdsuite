package de.drazil.nerdsuite.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Project {

	private String id;
	private String name;
	private boolean open;
	private String targetPlatform;
	private String projectType;
	@JsonInclude(Include.NON_NULL)
	private String projectSubType;

	@JsonProperty("folders")
	private List<ProjectFolder> folderList;

	public Project() {
		folderList = new ArrayList<ProjectFolder>();
	}

}
