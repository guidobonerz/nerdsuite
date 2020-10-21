package de.drazil.nerdsuite.explorer;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectFolder;
import de.drazil.nerdsuite.storagemedia.IMediaContainer;
import de.drazil.nerdsuite.storagemedia.MediaEntry;
import de.drazil.nerdsuite.storagemedia.MediaFactory;
import de.drazil.nerdsuite.util.E4Utils;
import de.drazil.nerdsuite.util.ImageFactory;

public class Explorer implements IDoubleClickListener {
	private TreeViewer treeViewer;

	@Inject
	MApplication app;
	@Inject
	EMenuService menuService;
	@Inject
	EPartService partService;
	@Inject
	EModelService modelService;

	public Explorer() {
	}

	@Inject
	public void postConstruct(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		Transfer[] transferTypes = { LocalSelectionTransfer.getTransfer() };
		treeViewer = new TreeViewer(container, SWT.NONE);
		treeViewer.addDragSupport(DND.DROP_COPY, transferTypes, new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {

				super.dragSetData(event);
			}
		});
		treeViewer.addDropSupport(DND.DROP_COPY, transferTypes, new ViewerDropAdapter(treeViewer) {

			@Override
			public boolean validateDrop(Object target, int operation, TransferData transferType) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean performDrop(Object data) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		treeViewer.setContentProvider(new ProjectStructureProvider());
		treeViewer.setLabelProvider(new ProjectStructureLabelProvider());
		treeViewer.addDoubleClickListener(this);
		menuService.registerContextMenu(treeViewer.getTree(), "de.drazil.nerdsuite.popupmenu.Explorer");

		listFiles();
	}

	@Inject
	@Optional
	void exportFile(@UIEventTopic("Export") BrokerObject brokerObject) {
		// if (brokerObject.getTransferObject().equals("Explorer")) {
		TreeSelection treeNode = (TreeSelection) treeViewer.getSelection();
		Object o = treeNode.getFirstElement();
		if (o instanceof Project) {

		} else if (o instanceof MediaEntry && !((MediaEntry) o).isDirectory()) {
			MediaEntry entry = (MediaEntry) o;
			IMediaContainer mediaManager = MediaFactory.mount((File) entry.getUserObject());
			FileDialog saveDialog = new FileDialog(treeViewer.getControl().getShell(), SWT.SAVE);
			saveDialog.setFileName(entry.getName() + "." + entry.getType());
			String fileName = saveDialog.open();
			try {
				mediaManager.exportEntry(entry, new File(fileName));
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information",
						"\"" + fileName + "\" was successfully exported.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning", "Folders can not be exported.");
		}
		// }
	}

	@Inject
	@Optional
	void U64_LoadAndRun(@UIEventTopic("U64_LoadAndRun") BrokerObject brokerObject, MPart part, IEventBroker broker) {

		TreeSelection treeNode = (TreeSelection) treeViewer.getSelection();
		Object o = treeNode.getFirstElement();

		if (o instanceof Project) {
			try {
				String fileName = ((Project) o).getMountLocation().split("@")[1];
				byte[] data = BinaryFileHandler.readFile(new File(fileName), 0);
				String owner = (String) part.getTransientData().get(Constants.OWNER);
				broker.send("LoadAndRun", new BrokerObject("", data));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (o instanceof MediaEntry && !((MediaEntry) o).isDirectory()) {
			MediaEntry entry = (MediaEntry) o;
			IMediaContainer mediaContainer = MediaFactory.mount((File) entry.getUserObject());
			try {
				byte[] data = mediaContainer.exportEntry(entry);
				String owner = (String) part.getTransientData().get(Constants.OWNER);
				broker.send("LoadAndRun", new BrokerObject("", data));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning",
					"Folders can not be started on Ultimate64");
		}

	}

	private void listFiles() {

		List<Project> projectList = Initializer.getConfiguration().getWorkspace().getProjects();

		File[] files = Configuration.WORKSPACE_PATH.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().startsWith(".");
			}
		});
		treeViewer.setInput(projectList);
	}

	private class ProjectStructureLabelProvider extends StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			Object o = cell.getElement();

			if (o instanceof Project) {
				Project project = (Project) o;
				cell.setText(project.getName());

				String iconName = "icons/bricks.png";
				if (project.isSingleFileProject()) {
					iconName = project.getIconName();
				}

				cell.setImage(ImageFactory.createImage(iconName));

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

				if (MediaFactory.filePattern.matcher(file.getName()).find()) {
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

			Object[] array = null;
			if (inputElement instanceof List) {
				List<Object> list = (List<Object>) inputElement;
				array = list.toArray(new Object[list.size()]);
			} else {
				array = (Object[]) inputElement;
			}

			return array;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			Object[] entries = null;
			if (parentElement instanceof Project) {
				Project project = (Project) parentElement;
				if (project.isMountpoint()) {
					File file = new File(project.getMountLocation());
					if (MediaFactory.isMountable(file)) {
						IMediaContainer mediaManager = MediaFactory.mount(file);
						entries = mediaManager.getEntries(parentElement);
					}
				}
			} else if (parentElement instanceof ProjectFolder) {

			} else if (parentElement instanceof File) {
				File parentFile = (File) parentElement;

				if (MediaFactory.isMountable(parentFile)) {
					IMediaContainer mediaManager = MediaFactory.mount(parentFile);
					entries = mediaManager.getEntries(parentElement);
				} else {
					entries = parentFile.listFiles();
				}
			} else if (parentElement instanceof MediaEntry) {
				MediaEntry me = (MediaEntry) parentElement;
				IMediaContainer mediaManager = MediaFactory.mount((File) me.getUserObject());
				entries = mediaManager.getEntries(parentElement);
			} else {
			}
			return entries;
		}

		@Override
		public Object getParent(Object element) {
			Object parent = null;
			if (element instanceof Project) {
				return null;
			} else if (element instanceof ProjectFolder) {
				return null;
			} else if (element instanceof File) {
				File file = (File) element;
				parent = file.getParentFile();
			} else if (element instanceof MediaEntry) {
				MediaEntry me = (MediaEntry) element;
				if (me.isRoot()) {
					parent = me.getUserObject();
				} else {
					parent = me.getParent();
				}
			} else {
			}
			return parent;
		}

		@Override
		public boolean hasChildren(Object element) {
			boolean hasChildren = false;

			if (element instanceof Project) {
				hasChildren = !((Project) element).isSingleFileProject() || ((Project) element).isMountpoint();
			}
			if (element instanceof ProjectFolder) {
				hasChildren = true;
			} else if (element instanceof File) {
				File file = (File) element;
				if (file.isDirectory()) {
					hasChildren = true;
				} else if (MediaFactory.isMountable(file)) {
					hasChildren = true;
				} else {
					hasChildren = false;
				}
			} else if (element instanceof MediaEntry) {
				MediaEntry me = (MediaEntry) element;
				hasChildren = me.isDirectory();

			} else {
			}
			return hasChildren;
		}

	}

	public void refresh() {
		listFiles();
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof Project) {
			Project project = (Project) element;
			System.out.println(project.getName());

			Map<String, Object> projectSetup = new HashMap<String, Object>();
			projectSetup.put("project", project);

			String owner = project.getId();
			MUIElement editor = modelService.find(owner, app);
			if (editor == null) {
				System.out.println("load tiles");
				File file = new File(Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR
						+ project.getId().toLowerCase() + "." + project.getSuffix());
				TileRepositoryService repository = TileRepositoryService.load(file, owner);
				projectSetup.put("repository", owner);
				projectSetup.put("file", file);

				if (repository.getMetadata().getType().equals("SCREENSET")) {
					File referenceFile = new File(
							Configuration.WORKSPACE_PATH + Constants.FILE_SEPARATOR + "c64_upper.ns_chr");
					String referenceOwner = "C64_UPPER";
					projectSetup.put("referenceRepository", referenceOwner);
					TileRepositoryService.load(referenceFile, referenceOwner);
				}
				MPart part = E4Utils.createPart(partService, "de.drazil.nerdsuite.partdescriptor.GfxEditorView",
						"bundleclass://de.drazil.nerdsuite/de.drazil.nerdsuite.imaging.GfxEditorView", owner,
						project.getName(), projectSetup);

				E4Utils.addPart2PartStack(app, modelService, partService, "de.drazil.nerdsuite.partstack.editorStack",
						part, true);
			} else {
				editor.getParent().setSelectedElement(editor);
			}
		}
	}
}
