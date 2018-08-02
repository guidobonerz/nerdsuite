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

public class BigSpriteEditor implements IColorProvider {

	@PostConstruct
	public void postConstruct(Composite parent) {
		boolean multiColorMode = false;
		byte binaryData[] = new byte[0xffff];
		for (int i = 0; i < binaryData.length; i++)
			binaryData[i] = 0;
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		ImagingWidget singleSpritePainter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		singleSpritePainter.setWidgetName("SpritePainter :");
		singleSpritePainter.setWidgetMode(WidgetMode.PAINTER);
		singleSpritePainter.setWidth(24);
		singleSpritePainter.setHeight(21);
		singleSpritePainter.setPixelSize(10);
		singleSpritePainter.setTileColumns(2);
		singleSpritePainter.setTileRows(2);

		singleSpritePainter.setPixelGridEnabled(true);
		singleSpritePainter.setGridStyle(GridStyle.PIXEL);
		singleSpritePainter.setTileGridEnabled(true);
		singleSpritePainter.setTileCursorEnabled(false);
		singleSpritePainter.setMultiColorEnabled(multiColorMode);
		singleSpritePainter.setSelectedTileOffset(0);
		singleSpritePainter.setColorProvider(null);

		singleSpritePainter.setContent(binaryData);
		singleSpritePainter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		singleSpritePainter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		singleSpritePainter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		singleSpritePainter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		singleSpritePainter.setSelectedColor(1);

		ImagingWidget spriteSelector = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		spriteSelector.setWidgetName("SpriteSelector:");
		spriteSelector.setWidgetMode(WidgetMode.SELECTOR);
		spriteSelector.setWidth(24);
		spriteSelector.setHeight(21);
		spriteSelector.setTileColumns(2);
		spriteSelector.setTileRows(2);
		spriteSelector.setColumns(10);
		spriteSelector.setRows(2);
		spriteSelector.setPixelSize(1);
		spriteSelector.setPixelGridEnabled(false);
		spriteSelector.setTileGridEnabled(false);
		spriteSelector.setTileSubGridEnabled(true);
		spriteSelector.setTileCursorEnabled(true);
		spriteSelector.setSeparatorEnabled(false);
		spriteSelector.setMultiColorEnabled(multiColorMode);

		spriteSelector.setSelectedTileOffset(0);
		spriteSelector.setColorProvider(null);

		spriteSelector.setContent(binaryData);
		spriteSelector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		spriteSelector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		spriteSelector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		spriteSelector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
		spriteSelector.setSelectedColor(1);

		singleSpritePainter.addDrawListener(spriteSelector);
		spriteSelector.addDrawListener(singleSpritePainter);

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				singleSpritePainter.setMultiColorEnabled(multicolor.getSelection());
				spriteSelector.setMultiColorEnabled(multicolor.getSelection());

			}
		});
	}

	@Override
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int offset, int index) {
		return InstructionSet.getPlatformData().getColorPalette().get(6).getColor();
	}
}
