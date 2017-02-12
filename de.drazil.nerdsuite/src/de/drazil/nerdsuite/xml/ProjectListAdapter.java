package de.drazil.nerdsuite.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.xml.ProjectListAdapter.ProjectListProvider;

public class ProjectListAdapter extends List2MapXmlAdapter<ProjectListProvider, Project>
{
	public static class ProjectListProvider extends List2MapXmlAdapter.ListProvider<Project>
	{
		@XmlElement(name = "project")
		@Getter
		public List<Project> list = new ArrayList<Project>();
	}

	@Override
	protected ProjectListProvider createListProvider()
	{
		return new ProjectListProvider();
	}

	@Override
	protected String getKey(Project value)
	{
		return value.getId().toUpperCase();
	}
}
