package de.drazil.nerdsuite.spriteeditor;

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
		ImagingWidget singleSpritePainter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		singleSpritePainter.setWidgetName("SpritePainter :");
		singleSpritePainter.setWidgetMode(WidgetMode.PAINTER);
		singleSpritePainter.setWidth(8);
		singleSpritePainter.setHeight(8);
		singleSpritePainter.setPixelSize(20);
		singleSpritePainter.setTileColumns(1);
		singleSpritePainter.setTileRows(1);

		singleSpritePainter.setPixelGridEnabled(true);
		singleSpritePainter.setGridStyle(GridStyle.PIXEL);
		singleSpritePainter.setTileGridEnabled(true);
		singleSpritePainter.setTileCursorEnabled(false);
		singleSpritePainter.setMultiColorEnabled(multiColorMode);
		singleSpritePainter.setSelectedTileOffset(0);
		singleSpritePainter.setColorProvider(this);

		singleSpritePainter.setContent(binaryData);
		singleSpritePainter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		singleSpritePainter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(4).getColor());
		singleSpritePainter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(10).getColor());
		singleSpritePainter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(15).getColor());
		singleSpritePainter.setSelectedColor(3);

		ImagingWidget spriteSelector = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		spriteSelector.setWidgetName("SpriteSelector:");
		spriteSelector.setWidgetMode(WidgetMode.SELECTOR);
		spriteSelector.setWidth(8);
		spriteSelector.setHeight(8);
		spriteSelector.setTileColumns(1);
		spriteSelector.setTileRows(1);
		spriteSelector.setColumns(20);
		spriteSelector.setRows(20);
		spriteSelector.setPixelSize(2);
		spriteSelector.setPixelGridEnabled(false);
		spriteSelector.setTileGridEnabled(false);
		spriteSelector.setTileSubGridEnabled(true);
		spriteSelector.setTileCursorEnabled(true);
		spriteSelector.setSeparatorEnabled(false);
		spriteSelector.setMultiColorEnabled(multiColorMode);

		spriteSelector.setSelectedTileOffset(0);
		spriteSelector.setColorProvider(this);

		spriteSelector.setContent(binaryData);
		spriteSelector.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		spriteSelector.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(4).getColor());
		spriteSelector.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(10).getColor());
		spriteSelector.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(15).getColor());
		spriteSelector.setSelectedColor(3);

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
