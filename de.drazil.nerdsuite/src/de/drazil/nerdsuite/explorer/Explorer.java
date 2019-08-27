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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.storagemedia.IMediaManager;
import de.drazil.nerdsuite.storagemedia.MediaEntry;
import de.drazil.nerdsuite.storagemedia.MediaMountFactory;
import de.drazil.nerdsuite.util.FontFactory;
import de.drazil.nerdsuite.util.ImageFactory;

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

		treeViewer.getControl().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 10;
			}
		});
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

			} else if (o instanceof MediaEntry) {
				MediaEntry file = (MediaEntry) o;
				cell.setText(file.getName());
				cell.setImage(ImageFactory.createImage("icons/document-binary.png"));
				// Font f = FontFactory.getFont(file.getFontName());
				// cell.setFont(f);

				// cell.setBackground(Constants.CBM_BG_COLOR);
				// cell.setForeground(Constants.CBM_FG_COLOR);

			} else {
				File file = (File) o;

				if (file.getName().startsWith("prj")) {
					cell.setText(file.getName().substring(4));
					cell.setImage(ImageFactory.createImage("icons/bricks.png"));
				} else {
					cell.setText(file.getName());
					cell.setImage(ImageFactory.createImage("icons/folder.png"));
				}

				if (MediaMountFactory.pattern.matcher(file.getName()).find()) {
					cell.setImage(ImageFactory.createImage("icons/disk.png"));
				}

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

			IMediaManager mediaManager = MediaMountFactory.mount(parentFile);

			if (mediaManager != null) {
				MediaMountFactory.read(mediaManager, parentFile);
				entries = mediaManager.getEntries();
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

			if (element instanceof MediaEntry) {
				return false;
			}
			File file = (File) element;

			IMediaManager mediaManager = MediaMountFactory.mount(file);

			if (file.isFile() && mediaManager != null) {
				hasChildren = true;
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
