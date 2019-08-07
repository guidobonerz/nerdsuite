
package de.drazil.nerdsuite.imaging;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.model.PlatformColor;

public class ColorPaletteView {
	@Inject
	public ColorPaletteView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		List<PlatformColor> colorList = InstructionSet.getPlatformData().getColorPalette();
		TableViewer viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		TableViewerColumn colorIndexColumn = new TableViewerColumn(viewer, SWT.NONE);
		colorIndexColumn.getColumn().setText("Index");
		colorIndexColumn.getColumn().setWidth(40);
		colorIndexColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(colorList.indexOf(element));
			}
		});
		TableViewerColumn visibilityColumn = new TableViewerColumn(viewer, SWT.CHECK);
		visibilityColumn.getColumn().setText("Name");
		visibilityColumn.getColumn().setWidth(65);
		visibilityColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PlatformColor platformColor = (PlatformColor) element;
				return platformColor.getName();
			}
		});
		TableViewerColumn colorCodeColumn = new TableViewerColumn(viewer, SWT.NONE);
		colorCodeColumn.getColumn().setText("Code");
		colorCodeColumn.getColumn().setWidth(80);
		colorCodeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PlatformColor platformColor = (PlatformColor) element;
				return platformColor.getValue().toUpperCase();
			}
		});

		TableViewerColumn colorColumn = new TableViewerColumn(viewer, SWT.NONE);
		colorColumn.getColumn().setText("Color");
		colorColumn.getColumn().setWidth(100);
		colorColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return null;
			}

			@Override
			public Color getBackground(Object element) {
				PlatformColor platformColor = (PlatformColor) element;
				return platformColor.getColor();
			}
		});
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(colorList);
	}
}