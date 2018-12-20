
package de.drazil.nerdsuite.imaging;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class LayerView {
	@Inject
	public LayerView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		final Table table = new Table(parent, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		final Image image = parent.getDisplay().getSystemImage(SWT.ICON_INFORMATION);
		Tile tile = new Tile(new ImagingWidgetConfiguration());
		tile.addLayer("Frame");
		tile.addLayer("Body");
		tile.addLayer("Eyes");
		tile.addLayer();

		TableColumn layerImage = new TableColumn(table, SWT.NONE);
		layerImage.setWidth(50);
		layerImage.setText("Layer");
		TableColumn layerVisible = new TableColumn(table, SWT.CHECK);
		layerVisible.setWidth(50);
		layerVisible.setText("Visible");
		TableColumn layerName = new TableColumn(table, SWT.NONE);
		layerName.setWidth(100);
		layerName.setText("Name");

		for (Layer layer : tile.getLayerList()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { "", "",layer.getName() });
		}

		final TableEditor editor = new TableEditor(table);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		Listener paintListener = event -> {
			switch (event.type) {
			case SWT.MeasureItem: {
				Rectangle rect1 = image.getBounds();
				event.width += rect1.width;
				event.height = Math.max(event.height, rect1.height + 2);
				break;
			}
			case SWT.PaintItem: {
				if (event.index == 0) {
					int x = event.x + event.width;
					Rectangle rect2 = image.getBounds();
					int offset = Math.max(0, (event.height - rect2.height) / 2);
					event.gc.drawImage(image, x, event.y + offset);
					break;
				}
			}
			}
		};
		table.addListener(SWT.MeasureItem, paintListener);
		table.addListener(SWT.PaintItem, paintListener);

		table.addSelectionListener(widgetSelectedAdapter(e -> {
			// Clean up any previous editor control
			Control oldEditor = editor.getEditor();
			if (oldEditor != null)
				oldEditor.dispose();

			// Identify the selected row
			TableItem item = (TableItem) e.item;
			if (item == null)
				return;

			// The control that will be the editor must be a child of the Table
			Text newEditor = new Text(table, SWT.NONE);
			newEditor.setText(item.getText(EDITABLECOLUMN));
			newEditor.addModifyListener(me -> {
				Text text = (Text) editor.getEditor();
				editor.getItem().setText(EDITABLECOLUMN, text.getText());
			});
			newEditor.selectAll();
			newEditor.setFocus();
			editor.setEditor(newEditor, item, EDITABLECOLUMN);
		}));

	}

}