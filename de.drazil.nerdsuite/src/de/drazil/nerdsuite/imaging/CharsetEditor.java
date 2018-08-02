package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;
import javax.swing.SwingConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.widget.IColorProvider;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidget.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidget.WidgetMode;

public class CharsetEditor implements IColorProvider {

	@PostConstruct
	public void postConstruct(Composite parent) {
		boolean multiColorMode = false;
		byte binaryData[] = new byte[0xffff];
		for (int i = 0; i < binaryData.length; i++)
			binaryData[i] = 0;
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		ImagingWidget painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		painter.setWidgetName("CharPainter :");
		painter.setWidgetMode(WidgetMode.PAINTER);
		painter.setWidth(8);
		painter.setHeight(8);
		painter.setPixelSize(20);
		painter.setTileColumns(1);
		painter.setTileRows(2);

		painter.setPixelGridEnabled(true);
		painter.setGridStyle(GridStyle.PIXEL);
		painter.setTileGridEnabled(true);
		painter.setTileCursorEnabled(false);
		painter.setMultiColorEnabled(multiColorMode);
		painter.setSelectedTileOffset(0);
		painter.setColorProvider(null);

		painter.setContent(binaryData);
		painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		painter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		painter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		painter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		painter.setSelectedColor(1);

		ImagingWidget selector = new ImagingWidget(parent,
				SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);
		selector.setWidgetName("CharSelector:");
		selector.setWidgetMode(WidgetMode.SELECTOR);
		selector.setWidth(8);
		selector.setHeight(8);
		selector.setTileColumns(1);
		selector.setTileRows(2);
		selector.setColumns(8);
		selector.setRows(3);
		selector.setPixelSize(3);
		selector.setPixelGridEnabled(false);
		selector.setTileGridEnabled(false);
		selector.setTileSubGridEnabled(true);
		selector.setTileCursorEnabled(true);
		selector.setSeparatorEnabled(false);
		selector.setMultiColorEnabled(multiColorMode);

		selector.setSelectedTileOffset(0);
		selector.setColorProvider(null);

		selector.setContent(binaryData);
		selector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		selector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		selector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		selector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		selector.setSelectedColor(1);

		painter.addDrawListener(selector);
		selector.addDrawListener(painter);

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				painter.setMultiColorEnabled(multicolor.getSelection());
				selector.setMultiColorEnabled(multicolor.getSelection());

			}
		});

		Button startAnimation = new Button(parent, SWT.PUSH);
		startAnimation.setText("Start Animation");
		startAnimation.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				selector.startAnimation();
			}
		});

		Button stopAnimation = new Button(parent, SWT.PUSH);
		stopAnimation.setText("Stop Animation");
		stopAnimation.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				selector.stopAnimation();
			}
		});
		Button clearMemory = new Button(parent, SWT.PUSH);
		clearMemory.setText("Clear Momory");
		clearMemory.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				selector.stopAnimation();
			}
		});
		Combo formatSelector = new Combo(parent, SWT.DROP_DOWN);
		formatSelector.setItems(new String[] { "Char", "Char 2X", "Char 2Y", "Char 2XY", "Sprite", "Sprite 2X",
				"Sprite 2Y", "Sprite 2XY" });
		formatSelector.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo c = ((Combo) e.getSource());
				int index = c.getSelectionIndex();
				switch (c.getItem(index)) {
				case "Char": {
					painter.setWidth(8);
					painter.setHeight(8);
					painter.setTileColumns(1);
					painter.setTileRows(1);
					selector.setWidth(8);
					selector.setHeight(8);
					selector.setTileColumns(1);
					selector.setTileRows(1);
					parent.layout();
					break;
				}
				case "Char 2X": {
					painter.setWidth(8);
					painter.setHeight(8);
					painter.setTileColumns(2);
					painter.setTileRows(1);
					selector.setWidth(8);
					selector.setHeight(8);
					selector.setTileColumns(2);
					selector.setTileRows(1);
					
					parent.layout();
					break;
				}

				case "Char 2Y": {
					painter.setWidth(8);
					painter.setHeight(8);
					painter.setTileColumns(1);
					painter.setTileRows(2);
					selector.setWidth(8);
					selector.setHeight(8);
					selector.setTileColumns(1);
					selector.setTileRows(2);
					parent.layout();
					break;
				}

				case "Char 2XY": {
					painter.setWidth(8);
					painter.setHeight(8);
					painter.setTileColumns(2);
					painter.setTileRows(2);
					selector.setWidth(8);
					selector.setHeight(8);
					selector.setTileColumns(2);
					selector.setTileRows(2);
					parent.layout();
					break;
				}

				case "Sprite": {
					painter.setWidth(24);
					painter.setHeight(21);
					painter.setTileColumns(1);
					painter.setTileRows(1);
					selector.setWidth(24);
					selector.setHeight(21);
					selector.setTileColumns(1);
					selector.setTileRows(1);
					parent.layout();
					break;
				}

				case "Sprite 2X": {
					painter.setWidth(24);
					painter.setHeight(21);
					painter.setTileColumns(2);
					painter.setTileRows(1);
					selector.setWidth(24);
					selector.setHeight(21);
					selector.setTileColumns(2);
					selector.setTileRows(1);
					parent.layout();
					break;
				}

				case "Sprite 2Y": {
					painter.setWidth(24);
					painter.setHeight(21);
					painter.setTileColumns(1);
					painter.setTileRows(2);
					selector.setWidth(24);
					selector.setHeight(21);
					selector.setTileColumns(1);
					selector.setTileRows(2);
					parent.layout();
					break;
				}

				case "Sprite 2XY": {
					painter.setWidth(24);
					painter.setHeight(21);
					painter.setTileColumns(2);
					painter.setTileRows(2);
					selector.setWidth(24);
					selector.setHeight(21);
					selector.setTileColumns(2);
					selector.setTileRows(2);
					parent.layout();
					break;
				}

				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int offset, int index) {
		return InstructionSet.getPlatformData().getColorPalette().get(index).getColor();
	}
}
