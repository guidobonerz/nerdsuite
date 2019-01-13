
package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class LayerView {
	@Inject
	public LayerView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		Tile tile = new Tile("name", 0);
		tile.addLayer("Frame");
		tile.addLayer("Body");
		tile.addLayer("Eyes");
		tile.addLayer();

		TableViewer viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		TableViewerColumn layerImageColumn = new TableViewerColumn(viewer, SWT.NONE);
		layerImageColumn.getColumn().setText("Layer");
		layerImageColumn.getColumn().setWidth(60);
		layerImageColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {

				return null;
			}

			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		TableViewerColumn visibilityColumn = new TableViewerColumn(viewer, SWT.CHECK);
		visibilityColumn.getColumn().setText("Visible");
		visibilityColumn.getColumn().setWidth(50);
		visibilityColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Layer layer = (Layer) element;
				return layer.getName();
			}
		});
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(100);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Layer layer = (Layer) element;
				return layer.getName();
			}
		});
		viewer.setContentProvider(new ArrayContentProvider());
		// viewer.setInput(tile.getLayerList());
	}
}