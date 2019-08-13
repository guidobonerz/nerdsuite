package de.drazil.nerdsuite.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

@AllArgsConstructor
public class Project {

	private String id;
	private String name;
	private boolean open;
	private String targetPlaform;
	private String projectType;
	private String projectSubType;

	private List<ProjectFolder> folderList;

	public Project() {
		folderList = new ArrayList<ProjectFolder>();
	}

}
