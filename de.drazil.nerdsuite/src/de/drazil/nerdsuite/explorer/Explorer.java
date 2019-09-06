package de.drazil.nerdsuite.explorer;

import java.io.File;
import java.io.FileFilter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.storagemedia.IMediaReader;
import de.drazil.nerdsuite.storagemedia.MediaEntry;
import de.drazil.nerdsuite.storagemedia.MediaFactory;
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
		treeViewer.setContentProvider(new ProjectStructureProvider());
		treeViewer.setLabelProvider(new ProjectStructureLabelProvider());
		menuService.registerContextMenu(treeViewer.getTree(), "de.drazil.nerdsuite.popupmenu.projectexplorer");

		listFiles();
	}

	@Inject
	@Optional
	void doExportFile(@UIEventTopic("doExportFile") boolean process) {
		TreeSelection treeNode = (TreeSelection) treeViewer.getSelection();
		Object o = treeNode.getFirstElement();
		if (o instanceof MediaEntry && !((MediaEntry) o).isDirectory()) {
			MediaEntry entry = (MediaEntry) o;
			IMediaReader mediaManager = MediaFactory.mount((File) entry.getUserObject());
			FileDialog saveDialog = new FileDialog(treeViewer.getControl().getShell(), SWT.SAVE);
			saveDialog.setFileName(entry.getName() + "." + entry.getType());
			String fileName = saveDialog.open();
			try {
				mediaManager.exportEntry(entry, new File(fileName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openInformation(treeViewer.getControl().getShell(), "Information",
					"Folders can not be exported.");
		}
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
				cell.setText(file.getFullName());
				cell.setImage(ImageFactory
						.createImage(file.isDirectory() ? "icons/folder.png" : "icons/document-binary.png"));
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

				if (MediaFactory.pattern.matcher(file.getName()).find()) {
					cell.setImage(ImageFactory.createImage("icons/disk.png"));
				}

			}
		}
	}

	private class ProjectStructureProvider implements ITreeContentProvider {
		@Override
		public void dispose() {
			int a = 0;
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
			Object[] entries = null;

			if (parentElement instanceof File) {
				File parentFile = (File) parentElement;

				if (MediaFactory.isMountable(parentFile)) {
					IMediaReader mediaManager = MediaFactory.mount(parentFile);
					entries = mediaManager.getEntries(parentElement);
				} else {
					entries = parentFile.listFiles();
				}
			} else {
				MediaEntry me = (MediaEntry) parentElement;
				IMediaReader mediaManager = MediaFactory.mount((File) me.getUserObject());
				entries = mediaManager.getEntries(parentElement);
			}
			return entries;
		}

		@Override
		public Object getParent(Object element) {
			Object parent = null;
			if (element instanceof File) {
				File file = (File) element;
				parent = file.getParentFile();
			} else {
				MediaEntry me = (MediaEntry) element;
				if (me.isRoot()) {
					parent = me.getUserObject();
				} else {
					parent = me.getParent();
				}
			}
			return parent;
		}

		@Override
		public boolean hasChildren(Object element) {
			boolean hasChildren = false;

			if (element instanceof File) {
				File file = (File) element;
				if (file.isDirectory()) {
					hasChildren = true;
				} else if (MediaFactory.isMountable(file)) {
					hasChildren = true;
				} else {
					hasChildren = false;
				}
			} else {
				MediaEntry me = (MediaEntry) element;
				hasChildren = me.isDirectory();

			}
			return hasChildren;
		}

	}

	public static void refreshExplorer(Explorer explorer, Project project) {
		explorer.listFiles();
	}

}
