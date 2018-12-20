
package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Layer {
	@Inject
	public Layer() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		Table table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		String[] titles = { "Column1", "Column2"};
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		int count = 4;
		for (int i = 0; i < count; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, "value1");
			item.setText(1, "value2");
			
		}
		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
	}

}