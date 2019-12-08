package de.drazil.nerdsuite.model;

import java.util.ArrayList;
import java.util.Date;
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
	private String suffix;
	private boolean open;
	private String targetPlatform;
	private String projectType;
	private String projectVariant;
	private boolean singleFileProject;
	private boolean isMountpoint;
	private String iconName;
	private Date createdOn;
	private Date changedOn;

	@JsonInclude(Include.NON_NULL)
	private String mountLocation;

	@JsonProperty("folders")
	private List<ProjectFolder> folderList;

	public Project() {
		folderList = new ArrayList<>();
	}

}
