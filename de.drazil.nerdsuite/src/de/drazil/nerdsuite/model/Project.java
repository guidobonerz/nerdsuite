package de.drazil.nerdsuite.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import de.drazil.nerdsuite.xml.ProjectFolderAdapter;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Project
{
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private boolean open;
	@XmlElement(name = "folders")
	@XmlJavaTypeAdapter(ProjectFolderAdapter.class)
	private Map<String, ProjectFolder> projectFolderMap;

	public Project()
	{
		projectFolderMap = new LinkedHashMap<String, ProjectFolder>();
	}

	public void add(ProjectFolder projectFolder)
	{
		projectFolderMap.put(projectFolder.getName(), projectFolder);
	}
}
