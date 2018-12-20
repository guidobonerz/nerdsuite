
package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.model.PlatformColor;

public class ColorPalette {
	@Inject
	public ColorPalette() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		Table table = new Table(parent, SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn colorName = new TableColumn(table, SWT.NONE);
		colorName.setWidth(100);
		colorName.setText("Name");
		TableColumn colorCode = new TableColumn(table, SWT.NONE);
		colorCode.setWidth(100);
		colorCode.setText("Color Code");
		TableColumn color = new TableColumn(table, SWT.NONE);
		color.setWidth(100);
		color.setText("Color");

		for (int i = 0; i < 16; i++) {
			TableItem item = new TableItem(table, 0);
			int index = table.indexOf(item);
			PlatformColor platformColor = InstructionSet.getPlatformData().getColorPalette().get(index);
			item.setText(new String[] { platformColor.getName(), platformColor.getValue().toUpperCase(), "" });
			item.setBackground(2, platformColor.getColor());
		}
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.index == 1) {
					GC gc = event.gc;
					TableItem item = (TableItem) event.item;
					int index = table.indexOf(item);
					Color c = InstructionSet.getPlatformData().getColorPalette().get(index).getColor();
					gc.setForeground(c);
					gc.setBackground(c);
				}
			}
		});
	}
}