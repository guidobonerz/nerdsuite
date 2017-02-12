package de.drazil.nerdsuite.explorer;

import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;

public class Explorer
{
	private TreeViewer treeViewer;

	@Inject
	EMenuService menuService;

	public Explorer()
	{
	}

	@PostConstruct
	public void postConstruct(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		treeViewer = new TreeViewer(container, SWT.NONE);
		menuService.registerContextMenu(treeViewer.getTree(), "de.drazil.nerdsuite.popupmenu.explorer");

		treeViewer.setContentProvider(new ProjectStructureProvider());
		treeViewer.setLabelProvider(new ProjectStructureLabelProvider());
		listFiles();

	}

	private void listFiles()
	{

		Map<String, Project> projectMap = Initializer.getConfiguration().getWorkspace().getProjectMap();

		Project project[] = projectMap.values().toArray(new Project[projectMap.values().size()]);

		treeViewer.setInput(project);

		/*
		 * 
		 * Configuration.WORKSPACE_PATH.listFiles(new FileFilter() {
		 * 
		 * @Override public boolean accept(File pathname) { return
		 * pathname.isDirectory(); } })
		 */

	}

	private class ProjectStructureLabelProvider extends StyledCellLabelProvider
	{
		@Override
		public void update(ViewerCell cell)
		{
			Object o = cell.getElement();

			if (o instanceof Project)
			{
				cell.setText(((Project) o).getName());

			}
			else if (o instanceof ProjectFolder)
			{
				cell.setText(((ProjectFolder) o).getName());
			}
		}
	}

	private class ProjectStructureProvider implements ITreeContentProvider
	{

		@Override
		public void dispose()
		{

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		@Override
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Project[])
			{
				return (Project[]) inputElement;
			}
			else
			{
				return null;
			}

		}

		@Override
		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof Project)
			{
				Project project = (Project) parentElement;
				Map<String, ProjectFolder> map = project.getProjectFolderMap();
				return map.values().toArray(new ProjectFolder[map.values().size()]);

			}
			return null;
		}

		@Override
		public Object getParent(Object element)
		{
			File file = (File) element;
			return file.getParentFile();
		}

		@Override
		public boolean hasChildren(Object element)
		{
			boolean hasChildren = false;
			if (element instanceof Project)
			{
				hasChildren = ((Project) element).getProjectFolderMap().size() > 0;
			}
			else
			{

			}
			return hasChildren;
		}
	}

	public static void refreshExplorer(Explorer explorer, Project project)
	{
		explorer.listFiles();
	}
}
