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
import de.drazil.nerdsuite.widget.BitmapPainter;
import de.drazil.nerdsuite.widget.IColorProvider;

public class SpriteEditor implements IColorProvider
{

	@PostConstruct
	public void postConstruct(Composite parent)
	{
		boolean multiColorMode = false;
		byte binaryData[] = new byte[0xffff];
		for (int i = 0; i < binaryData.length; i++)
			binaryData[i] = 0;
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		BitmapPainter singleSpritePainter = new BitmapPainter(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED)
		{
			@Override
			public String getPainterName()
			{
				return "SpritePainter       ";
			}
		};

		singleSpritePainter.setWidth(24);
		singleSpritePainter.setHeight(21);
		singleSpritePainter.setPixelSize(10);
		singleSpritePainter.setTileColumns(2);
		singleSpritePainter.setTileRows(2);

		singleSpritePainter.setPixelGridEnabled(true);
		singleSpritePainter.setGridStyle(BitmapPainter.PIXELGRID);
		singleSpritePainter.setTileGridEnabled(true);
		singleSpritePainter.setTileCursorEnabled(false);
		singleSpritePainter.setMultiColorEnabled(multiColorMode);
		singleSpritePainter.setReadOnly(false);
		singleSpritePainter.setOffset(0);
		singleSpritePainter.setColorProvider(this);

		singleSpritePainter.setContent(binaryData);
		singleSpritePainter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		singleSpritePainter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(4).getColor());
		singleSpritePainter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(10).getColor());
		singleSpritePainter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(15).getColor());
		singleSpritePainter.setSelectedColor(3);

		BitmapPainter spriteSelector = new BitmapPainter(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED)
		{
			@Override
			public String getPainterName()
			{
				return "SpritePainterPreview";
			}
		};

		spriteSelector.setWidth(24);
		spriteSelector.setHeight(21);
		spriteSelector.setTileColumns(2);
		spriteSelector.setTileRows(2);
		spriteSelector.setColumns(10);
		spriteSelector.setRows(2);
		spriteSelector.setPixelSize(2);
		spriteSelector.setPixelGridEnabled(false);
		spriteSelector.setTileGridEnabled(false);
		spriteSelector.setTileSubGridEnabled(true);
		spriteSelector.setTileCursorEnabled(true);
		spriteSelector.setSeparatorEnabled(false);
		spriteSelector.setMultiColorEnabled(multiColorMode);

		spriteSelector.setReadOnly(true);
		spriteSelector.setOffset(0);
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
		multicolor.addListener(SWT.Selection, new Listener()
		{

			@Override
			public void handleEvent(Event event)
			{

				singleSpritePainter.setMultiColorEnabled(multicolor.getSelection());
				spriteSelector.setMultiColorEnabled(multicolor.getSelection());

			}
		});
	}

	@Override
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int offset, int index)
	{
		return InstructionSet.getPlatformData().getColorPalette().get(6).getColor();
	}
}
