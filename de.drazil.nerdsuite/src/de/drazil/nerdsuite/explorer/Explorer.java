package de.drazil.nerdsuite.explorer;

import java.io.File;
import java.io.FileFilter;

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

import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.util.ImageFactory;
import de.drazil.nersuite.storagemedia.IMediaProvider;
import de.drazil.nersuite.storagemedia.MediaMountFactory;

public class Explorer {
	private TreeViewer treeViewer;

	@Inject
	EMenuService menuService;

	public Explorer() {
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		treeViewer = new TreeViewer(container, SWT.NONE);
		treeViewer.setContentProvider(new ProjectStructureProvider());
		treeViewer.setLabelProvider(new ProjectStructureLabelProvider());
		menuService.registerContextMenu(treeViewer.getTree(), "de.drazil.nerdsuite.popupmenu.explorer");
		listFiles();
	}

	private void listFiles() {
		File[] files = Configuration.WORKSPACE_PATH.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().startsWith(".");
			}
		});
		treeViewer.setInput(files);
	}

	private class ProjectStructureLabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			Object o = cell.getElement();

			if (o instanceof Project) {
				cell.setText(((Project) o).getName());
				cell.setImage(ImageFactory.createImage("icons/bricks.png"));

			} else if (o instanceof ProjectFolder) {
				cell.setText(((ProjectFolder) o).getName());
				cell.setImage(ImageFactory.createImage("icons/folder.png"));
			} else {
				File file = (File) o;
				cell.setText(file.getName());
			}
		}
	}

	private class ProjectStructureProvider implements ITreeContentProvider {
		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			Object[] entries;
			File parentFile = (File) parentElement;
			if (MediaMountFactory.isMountable(parentFile)) {
				try {
					IMediaProvider mediaProvider = MediaMountFactory.mount(parentFile, null);
					entries = mediaProvider.getEntries();
				} catch (Exception e) {
					entries = null;
				}
			} else {
				entries = parentFile.listFiles();
			}
			return entries;
		}

		@Override
		public Object getParent(Object element) {
			File file = (File) element;
			return file.getParentFile();
		}

		@Override
		public boolean hasChildren(Object element) {
			boolean hasChildren = false;
			File file = (File) element;
			if (MediaMountFactory.isMountable(file)) {
				try {
					IMediaProvider mediaProvider = MediaMountFactory.mount(file, null);
					hasChildren = mediaProvider.hasEntries();
				} catch (Exception e) {
					hasChildren = false;
				}
			} else if (file.isFile()) {
				hasChildren = false;
			} else {
				hasChildren = file.list().length > 0;
			}
			return hasChildren;
		}
	}

	public static void refreshExplorer(Explorer explorer, Project project) {
		explorer.listFiles();
	}
}
