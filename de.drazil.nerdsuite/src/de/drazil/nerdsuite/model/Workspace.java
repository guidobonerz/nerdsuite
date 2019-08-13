package de.drazil.nerdsuite.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Workspace {

	private List<Project> projects;

	public Workspace() {
		projects = new ArrayList<Project>();
	}

	public void add(Project project) {
		projects.add(project);
	}

	public void remove(Project project) {
		projects.remove(project);
	}
}
