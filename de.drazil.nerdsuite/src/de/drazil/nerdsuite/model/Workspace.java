package de.drazil.nerdsuite.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import de.drazil.nerdsuite.xml.ProjectListAdapter;

@Data
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class Workspace
{

	@XmlElement(name = "projects")
	@XmlJavaTypeAdapter(ProjectListAdapter.class)
	private Map<String, Project> projectMap;

	public Workspace()
	{
		projectMap = new LinkedHashMap<String, Project>();
	}

	public void add(Project project)
	{
		projectMap.put(project.getId(), project);
	}

	public void removeProject(String projectName)
	{
		projectMap.remove(projectName);
	}

}
