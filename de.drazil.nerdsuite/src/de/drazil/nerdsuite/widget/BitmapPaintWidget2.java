package de.drazil.nerdsuite.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.log.Console;

public class BitmapPaintWidget2 extends Canvas
{
	private int width = 8;
	private int currentWidth = 0;
	private int height = 8;
	private int columns = 1;
	private int rows = 1;
	private int pixelSize = 15;
	private int currentPixelWidth;
	private int currentPixelHeight;
	private boolean pixelGridEnabled = true;
	private boolean tileGridEnabled = true;
	private boolean multiColorEnabled = true;
	private boolean tileCursorEnabled = true;
	private int bytesPerRow;
	private byte byteArray[];
	private int cursorX = 0;
	private int cursorY = 0;
	private boolean paintMode = true;
	private boolean leftDown = false;
	private boolean redrawAll = true;

	private Map<String, Color> palette;
	private int selectedColorIndex;
	private int colorCount;

	public BitmapPaintWidget2(Composite parent, int style)
	{
		super(parent, style);

		setBackground(Constants.WHITE);
		addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				BitmapPaintWidget2.this.paintControl(e);
			}
		});

		addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseScrolled(MouseEvent e)
			{
				colorCount += e.count;
				selectedColorIndex = Math.abs(colorCount % 4);

			}
		});

		addMouseMoveListener(new MouseMoveListener()
		{
			@Override
			public void mouseMove(MouseEvent e)
			{
				cursorX = e.x / currentPixelWidth;
				cursorY = e.y / currentPixelHeight;
				//Console.println(cursorX + " " + cursorY);
				System.out.println(cursorX + " " + cursorY);

				if (leftDown)
				{
					redrawAll = false;
					// redraw();
					redraw((cursorX * currentPixelWidth) + 1, (cursorY * currentPixelHeight) + 1, currentPixelWidth - 1, currentPixelHeight - 1, true);

				}
			}
		});

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseUp(MouseEvent e)
			{
				if (e.button == 3)
				{
					paintMode = !paintMode;
					Console.println("drawMode:" + paintMode);
				}
				else if (e.button == 1)
				{
					leftDown = false;
				}
			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				if (e.button == 1)
				{
					cursorX = e.x / currentPixelWidth;
					cursorY = e.y / pixelSize;
					leftDown = true;
					redrawAll = false;
					// redraw();
					redraw((cursorX * currentPixelWidth) + 1, (cursorY * currentPixelHeight) + 1, currentPixelWidth - 1, currentPixelHeight - 1, true);
				}
			}
		});

		addMouseWheelListener(new MouseWheelListener()
		{

			@Override
			public void mouseScrolled(MouseEvent e)
			{

			}
		});
	}

	void paintControl(PaintEvent e)
	{
		drawPixel(e.gc, cursorX, cursorY, paintMode);
		if (redrawAll)
		{
			drawImage(e.gc);
			Console.println("drawAll");
			if (isPixelGridEnabled())
			{
				drawPixelGrid(e.gc);
			}
		}
		if (tileGridEnabled)
		{
			drawTileGrid(e.gc);
		}
		if (tileCursorEnabled)
		{

		}
		redrawAll = true;
	}

	public void drawPixelGrid(GC gc)
	{
		for (int x = 0; x <= currentWidth * columns; x++)
		{
			for (int y = 0; y <= height * rows; y++)
			{
				gc.setForeground(Constants.PIXEL_GRID_COLOR);
				gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * currentPixelHeight * rows);
				gc.drawLine(0, y * pixelSize, width * pixelSize * columns, y * pixelSize);
			}
		}
	}

	public void drawTileGrid(GC gc)
	{

		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int step = (4 * (isMultiColorEnabled() ? 1 : 2));
		for (int x = step; x < width * columns; x += step)
		{
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * rows * pixelSize);
		}
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int y = height; y < height * rows; y += height)
		{
			gc.drawLine(0, y * pixelSize, width * columns * pixelSize, y * pixelSize);
		}
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = width; x < width * columns; x += width)
		{
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * rows * pixelSize);
		}
	}

	public void drawImage(GC gc)
	{
		int x = 0;
		int y = 0;
		int b1 = bytesPerRow * height;
		int b2 = b1 * columns;
		for (int i = 0; i < byteArray.length; i++)
		{
			int b = (byteArray[i] & 0xff);
			int xi = (i % bytesPerRow) * (8 / (isMultiColorEnabled() ? 2 : 1));
			int xo = (i / b1) % columns;
			x = xi + (xo * currentWidth);

			int yi = (i / bytesPerRow) % height;
			int yo = (i / b2) % rows;
			y = yi + (yo * height);

			if (isMultiColorEnabled())
			{
				for (int j = 6; j >= 0; j -= 2)
				{
					int bi = b;
					String s = String.valueOf((bi >> j) & 3);
					gc.setBackground(palette.get(s));
					gc.fillRectangle((x * currentPixelWidth) + 1, (y * currentPixelHeight) + 1, currentPixelWidth - 1, currentPixelHeight - 1);
					x++;
				}
			}
			else
			{
				for (int j = 128; j > 0; j >>= 1)
				{
					gc.setBackground((b & j) == j ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);
					gc.fillRectangle((x * currentPixelWidth) + 1, (y * currentPixelHeight) + 1, currentPixelWidth - 1, currentPixelHeight - 1);
					x++;
				}
			}
		}
		Console.println("drawImage");
	}

	private void drawPixel(GC gc, int x, int y, boolean paintMode)
	{
		if (cursorX < currentWidth * columns && cursorY < height * rows)
		{
			int ix = cursorX % currentWidth;
			int iy = cursorY % height;
			int ax = (cursorX / currentWidth);
			int ay = (cursorY / height) * columns;
			int offset = (ax + ay) * (height * bytesPerRow);
			if (isMultiColorEnabled())
			{
				int index = (((iy * currentWidth) + ix) >> 2) + offset;
				ix &= 3;
				int mask = (3 << ((3 - ix) * 2) ^ 0xff) & 0xff;
				byte byteMask = (byte) ((byteArray[index] & mask));
				byteMask |= selectedColorIndex << ((3 - ix) * 2);
				byteArray[index] = byteMask;
				gc.setBackground(paintMode ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);
			}
			else
			{
				int index = (((iy * currentWidth) + ix) >> 3) + offset;
				byte byteMask = byteArray[index];
				int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
				byteArray[index] = paintMode ? (byte) (byteMask | pixelMask) : (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
				gc.setBackground(paintMode ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);
			}
			gc.fillRectangle((x * currentPixelWidth) + 1, (y * currentPixelHeight) + 1, currentPixelWidth - 1, currentPixelHeight - 1);
		}
	}

	public void setContent(byte byteArray[])
	{
		this.byteArray = byteArray;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
		reset();
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
		reset();
	}

	public int getColumns()
	{
		return columns;
	}

	public void setColumns(int columns)
	{
		this.columns = columns;
		reset();
	}

	public int getRows()
	{
		return rows;
	}

	public void setRows(int rows)
	{
		this.rows = rows;
		reset();
	}

	public int getPixelSize()
	{
		return pixelSize;
	}

	public void setPixelSize(int pixelSize)
	{
		this.pixelSize = pixelSize;
		this.currentPixelWidth = pixelSize;
		this.currentPixelHeight = pixelSize;
		reset();
	}

	public boolean isPixelGridEnabled()
	{
		return pixelGridEnabled;
	}

	public void setPixelGridEnabled(boolean pixelGridEnabled)
	{
		this.pixelGridEnabled = pixelGridEnabled;
		redraw();
	}

	public boolean isTileGridEnabled()
	{
		return tileGridEnabled;
	}

	public void setTileGridEnabled(boolean tileGridEnabled)
	{
		this.tileGridEnabled = tileGridEnabled;
		redraw();
	}

	public boolean isMultiColorEnabled()
	{
		return multiColorEnabled;
	}

	public void setMultiColorEnabled(boolean multiColorEnabled)
	{
		this.multiColorEnabled = multiColorEnabled;
		currentPixelWidth = getPixelSize() * (isMultiColorEnabled() ? 2 : 1);
		currentWidth = getWidth() / (isMultiColorEnabled() ? 2 : 1);
		redraw();
	}

	private void reset()
	{
		bytesPerRow = width >> 3;
		byteArray = new byte[(height * bytesPerRow * columns * rows)];
		redraw();
	}

	public void setColor(int index, Color color)
	{
		if (palette == null)
		{
			palette = new HashMap<String, Color>();
		}
		palette.put(String.valueOf(index), color);
		redraw();
	}

	public void setSelectedColor(int index)
	{
		selectedColorIndex = index;
	}
}