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

	public Project getProjectByName(String name) {
		return projects.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public Project getProjectById(String id) {
		return projects.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}
}
