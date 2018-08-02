package de.drazil.nerdsuite.imaging;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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
		ImagingWidget charPainter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		charPainter.setWidgetName("CharPainter :");
		charPainter.setWidgetMode(WidgetMode.PAINTER);
		charPainter.setWidth(8);
		charPainter.setHeight(8);
		charPainter.setPixelSize(20);
		charPainter.setTileColumns(1);
		charPainter.setTileRows(2);

		charPainter.setPixelGridEnabled(true);
		charPainter.setGridStyle(GridStyle.PIXEL);
		charPainter.setTileGridEnabled(true);
		charPainter.setTileCursorEnabled(false);
		charPainter.setMultiColorEnabled(multiColorMode);
		charPainter.setSelectedTileOffset(0);
		charPainter.setColorProvider(null);

		charPainter.setContent(binaryData);
		charPainter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		charPainter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		charPainter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		charPainter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		charPainter.setSelectedColor(1);

		ImagingWidget charSelector = new ImagingWidget(parent,
				SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);
		charSelector.setWidgetName("CharSelector:");
		charSelector.setWidgetMode(WidgetMode.SELECTOR);
		charSelector.setWidth(8);
		charSelector.setHeight(8);
		charSelector.setTileColumns(1);
		charSelector.setTileRows(2);
		charSelector.setColumns(20);
		charSelector.setRows(20);
		charSelector.setPixelSize(3);
		charSelector.setPixelGridEnabled(false);
		charSelector.setTileGridEnabled(false);
		charSelector.setTileSubGridEnabled(true);
		charSelector.setTileCursorEnabled(true);
		charSelector.setSeparatorEnabled(false);
		charSelector.setMultiColorEnabled(multiColorMode);

		charSelector.setSelectedTileOffset(0);
		charSelector.setColorProvider(null);

		charSelector.setContent(binaryData);
		charSelector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		charSelector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		charSelector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		charSelector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		charSelector.setSelectedColor(1);

		charPainter.addDrawListener(charSelector);
		charSelector.addDrawListener(charPainter);

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				charPainter.setMultiColorEnabled(multicolor.getSelection());
				charSelector.setMultiColorEnabled(multicolor.getSelection());

			}
		});

		Button startAnimation = new Button(parent, SWT.PUSH);
		startAnimation.setText("Start Animation");
		startAnimation.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				charSelector.startAnimation();
			}
		});

		Button stopAnimation = new Button(parent, SWT.PUSH);
		stopAnimation.setText("Stop Animation");
		stopAnimation.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				charSelector.stopAnimation();
			}
		});
		Button clearMemory = new Button(parent, SWT.PUSH);
		clearMemory.setText("Clear Momory");
		clearMemory.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				charSelector.stopAnimation();
			}
		});
	}

	@Override
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int offset, int index) {
		return InstructionSet.getPlatformData().getColorPalette().get(index).getColor();
	}
}
