package de.drazil.nerdsuite.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.xml.ProjectFolderAdapter.ProjectFolderListProvider;

public class ProjectFolderAdapter extends List2MapXmlAdapter<ProjectFolderListProvider, ProjectFolder>
{
	public static class ProjectFolderListProvider extends List2MapXmlAdapter.ListProvider<ProjectFolder>
	{
		@XmlElement(name = "folder")
		@Getter
		public List<ProjectFolder> list = new ArrayList<ProjectFolder>();
	}

	@Override
	protected ProjectFolderListProvider createListProvider()
	{
		return new ProjectFolderListProvider();
	}

	@Override
	protected String getKey(ProjectFolder value)
	{
		return value.getId().toUpperCase();
	}

}
