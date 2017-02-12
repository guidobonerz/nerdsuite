package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.log.Console;

public class BitmapPainter extends Canvas implements IDrawListener, PaintListener
{
	private final static int MOUSE_BUTTON_LEFT = 1;
	private final static int MOUSE_BUTTON_MIDDLE = 2;
	private final static int MOUSE_BUTTON_RIGHT = 3;
	public final static int PIXELGRID = 1;
	public final static int LINEGRID = 2;
	private int width = 8;
	private int currentWidth = 0;
	private int height = 8;
	private int tileColumns = 1;
	private int tileRows = 1;
	private int columns = 1;
	private int rows = 1;
	private int pixelSize = 15;
	private int currentPixelWidth;
	private int currentPixelHeight;
	private boolean pixelGridEnabled = true;
	private boolean tileGridEnabled = true;
	private boolean tileSubGridEnabled = true;
	private boolean multiColorEnabled = true;
	private boolean tileCursorEnabled = false;
	private boolean separatorEnabled = true;
	private boolean mouseIn = false;
	private int selectedTileIndexX = 0;
	private int selectedTileIndexY = 0;
	private int bytesPerRow;
	private byte byteArray[];
	private int cursorX = 0;
	private int cursorY = 0;
	private int tileX = 0;
	private int tileY = 0;
	private int tileCursorX = 0;
	private int tileCursorY = 0;
	private int gridStyle = LINEGRID;

	private boolean paintMode = true;
	private boolean readOnly = false;
	private Map<String, Color> palette;
	private int selectedColorIndex;
	private int colorCount;
	private int offset = 0;
	private IColorProvider colorProvider;
	private ScrollBar hBar = null;
	private ScrollBar vBar = null;
	private List<IDrawListener> drawListenerList = null;

	private final static int DRAW_NOTHING = 0;
	private final static int SET_DRAW_ALL = 1;
	private final static int SET_DRAW_AREA = 2;
	private final static int SET_DRAW_PIXEL = 4;

	private final static int LEFT_BUTTON_PRESSED = 1;
	private final static int LEFT_BUTTON_RELEASED = 2;
	private final static int RIGHT_BUTTON_PRESSED = 4;
	private final static int RIGHT_BUTTON_RELEASED = 8;

	private int leftButtonMode = 0;
	private int rightButtonMode = 0;

	private int drawMode = DRAW_NOTHING;

	public BitmapPainter(Composite parent, int style)
	{
		super(parent, style);
		setTileColumns(1);
		setTileRows(1);
		setColumns(1);
		setRows(1);
		/*
		 * hBar = getHorizontalBar(); vBar = getVerticalBar(); final Point origin =
		 * new Point(0, 0); hBar.addListener(SWT.Selection, e -> { int hSelection =
		 * hBar.getSelection(); int destX = -hSelection - origin.x; Rectangle rect =
		 * new Rectangle(0, 0, (8 * 3 * 40) + 18, 8 * 3 * 25); scroll(destX, 0, 0,
		 * 0, rect.width, rect.height, false); origin.x = -hSelection; redraw(); });
		 * 
		 * vBar.addListener(SWT.Selection, e -> { int vSelection =
		 * vBar.getSelection(); int destY = -vSelection - origin.y; Rectangle rect =
		 * new Rectangle(0, 0, (8 * 3 * 40) + 18, 8 * 3 * 25); scroll(0, destY, 0,
		 * 0, rect.width, rect.height, false); origin.y = -vSelection; redraw(); });
		 */

		setBackground(Constants.BLACK);

		drawListenerList = new ArrayList<IDrawListener>();

		addPaintListener(this);

		addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseScrolled(MouseEvent e)
			{
				if (!isReadOnly())
				{
					colorCount += e.count;
					selectedColorIndex = Math.abs(colorCount % 4);
				}
			}
		});

		addMouseTrackListener(new MouseTrackListener()
		{

			@Override
			public void mouseHover(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExit(MouseEvent e)
			{
				mouseIn = false;
				drawAll();
			}

			@Override
			public void mouseEnter(MouseEvent e)
			{
				mouseIn = true;
				drawAll();
			}
		});

		addMouseMoveListener(new MouseMoveListener()
		{

			@Override
			public void mouseMove(MouseEvent e)
			{
				setCursorPosition(e.x, e.y);
				if (leftButtonMode == LEFT_BUTTON_PRESSED)
				{
					if (!isReadOnly())
					{
						System.out.printf(getPainterName() + ": mx:%3d  my:%3d | px:%3d  py:%3d | tx:%3d  ty:%3d | tcx:%3d  tcy:%3d %n", e.x, e.y, cursorX, cursorY, tileX,
								tileY, tileCursorX, tileCursorY);
						drawPixel();
						fireDrawAll();
					}
				}
				if (isReadOnly())
				{
					drawAll();
				}
			}
		});

		addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(MouseEvent e)
			{
				if (e.button == MOUSE_BUTTON_RIGHT)
				{
					paintMode = !paintMode;
					Console.println("drawMode:" + paintMode);
				}
				else if (e.button == MOUSE_BUTTON_LEFT)
				{
					if (leftButtonMode == LEFT_BUTTON_PRESSED)
					{
						leftButtonMode = 0;
						if (isReadOnly())
						{
							drawMode = 0;
							selectedTileIndexX = tileX;
							selectedTileIndexY = tileY;
							offset = (getWidth() / 8) * getHeight() * tileColumns * tileRows * (selectedTileIndexX + (selectedTileIndexY * columns));
							fireSetSelectedTileOffset(offset);
							System.out.printf(getPainterName() + ": Tile selected x:%3d  y:%3d %n", selectedTileIndexX, selectedTileIndexY);
							drawAll();
						}
						else
						{
							drawPixel();
							fireDrawAll();
						}
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				if (e.button == MOUSE_BUTTON_LEFT)
				{
					setCursorPosition(e.x, e.y);
					leftButtonMode = LEFT_BUTTON_PRESSED;
					if (!isReadOnly())
					{
					}
				}
			}
		});
	}

	private void setCursorPosition(int x, int y)
	{
		cursorX = x / currentPixelWidth;
		cursorY = y / currentPixelHeight;
		tileX = x / (width * currentPixelWidth * tileColumns);
		tileY = y / (height * currentPixelHeight * tileRows);
		tileCursorX = (cursorX - (tileX * width));
		tileCursorY = (cursorY - (tileY * height));
	}

	public String getPainterName()
	{
		return this.getClass().getName();
	}

	public void paintControl(PaintEvent e)
	{
		if ((drawMode & SET_DRAW_PIXEL) == SET_DRAW_PIXEL)
		{
			drawPixel(e.gc, cursorX, cursorY);
		}

		if ((drawMode & SET_DRAW_ALL) == SET_DRAW_ALL)
		{
			drawImage(e.gc);
		}

		if (isPixelGridEnabled())
		{
			drawPixelGrid(e.gc);
		}
		if (isSeparatorEnabled())
		{
			drawSeparator(e.gc);
		}
		if (isTileGridEnabled())
		{
			drawTileGrid(e.gc);
		}
		if (isTileSubGridEnabled())
		{
			drawTileSubGrid(e.gc);
		}

		drawTileCursor(e.gc, isTileCursorEnabled(), mouseIn);
		drawMode = DRAW_NOTHING;
	}

	public void drawTileCursor(GC gc, boolean tileCursorEnabled, boolean mouseIn)
	{
		gc.setLineWidth(2);
		gc.setLineStyle(SWT.LINE_SOLID);

		gc.setForeground(Constants.DEFAULT_UNSTABLE_ILLEGAL_OPCODE_COLOR);
		gc.drawRectangle(selectedTileIndexX * width * pixelSize * tileColumns, selectedTileIndexY * height * pixelSize * tileRows, width * pixelSize * tileColumns,
				height * pixelSize * tileRows);

		if (mouseIn)
		{
			gc.setForeground((tileX == selectedTileIndexX && tileY == selectedTileIndexY) ? Constants.LIGHT_RED : Constants.LIGHT_RED);
			gc.drawRectangle(tileX * width * pixelSize * tileColumns, tileY * height * pixelSize * tileRows, width * pixelSize * tileColumns, height * pixelSize
					* tileRows);
		}

	}

	public void drawPixelGrid(GC gc)
	{
		for (int x = 0; x <= currentWidth * tileColumns; x++)
		{
			for (int y = 0; y <= height * tileRows; y++)
			{
				gc.setForeground(Constants.PIXEL_GRID_COLOR);
				if (gridStyle == LINEGRID)
				{
					gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * currentPixelHeight * tileRows);
					gc.drawLine(0, y * pixelSize, width * pixelSize * tileColumns, y * pixelSize);
				}
				else
				{
					gc.drawPoint(x * currentPixelWidth, y * currentPixelHeight);
				}
			}
		}
	}

	public void drawSeparator(GC gc)
	{
		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int step = (4 * (isMultiColorEnabled() ? 1 : 2));
		for (int x = step; x < (width * tileColumns) / ((isMultiColorEnabled() ? 2 : 1)); x += step)
		{
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * tileRows * pixelSize);
		}
	}

	public void drawTileGrid(GC gc)
	{
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int y = height; y < height * tileRows; y += height)
		{
			gc.drawLine(0, y * pixelSize, width * tileColumns * pixelSize, y * pixelSize);
		}
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int x = currentWidth; x < currentWidth * tileColumns; x += currentWidth)
		{
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * tileRows * pixelSize);
		}
	}

	public void drawTileSubGrid(GC gc)
	{
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x < columns; x++)
		{
			for (int y = 0; y < columns; y++)
			{
				gc.drawRectangle(x * width * pixelSize * tileColumns, y * height * pixelSize * tileRows, width * pixelSize * tileColumns, height * pixelSize * tileRows);
			}
		}
	}

	public void drawImage(GC gc)
	{
		int x = 0;
		int y = 0;
		int b1 = bytesPerRow * height;
		int b2 = b1 * tileColumns;
		for (int i = offset, k = 0; i < (offset + getViewportSize()); i++, k++)
		{
			int b = (byteArray[i] & 0xff);
			int xi = (k % bytesPerRow) * (8 / (isMultiColorEnabled() ? 2 : 1));
			int xo = (k / b1) % tileColumns;
			x = xi + (xo * currentWidth) + (selectedTileIndexX * width * tileColumns);

			int yi = (k / bytesPerRow) % height;
			int yo = (k / b2) % tileRows;
			y = yi + (yo * height) + (selectedTileIndexY * height * tileRows);
			int colorMapIndex = k / width;

			if (isMultiColorEnabled())
			{
				for (int j = 6; j >= 0; j -= 2)
				{
					int bi = b;
					int colorIndex = (bi >> j) & 3;
					gc.setBackground(colorProvider.getColorByIndex((byte) colorIndex, byteArray, offset, colorMapIndex));
					int pix = isPixelGridEnabled() ? 1 : 0;
					gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix, currentPixelWidth - pix, currentPixelHeight - pix);
					x++;
				}
			}
			else
			{
				for (int j = 128; j > 0; j >>= 1)
				{
					gc.setBackground((b & j) == j ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);
					int pix = isPixelGridEnabled() ? 1 : 0;
					gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix, currentPixelWidth - pix, currentPixelHeight - pix);
					x++;
				}
			}
		}
	}

	private void drawPixel(GC gc, int x, int y)
	{

		System.out.println(getPainterName() + ":drawPixel x:" + x + "  y:" + y);
		if (x < currentWidth * tileColumns && y < height * tileRows)
		{
			int ix = x % currentWidth;
			int iy = y % height;
			int ax = (x / currentWidth);
			int ay = (y / height) * tileColumns;
			int offset = (ax + ay) * (height * bytesPerRow);
			if (isMultiColorEnabled())
			{
				int index = (((iy * currentWidth) + ix) >> 2) + offset;
				ix &= 3;
				int mask = (3 << ((3 - ix) * 2) ^ 0xff) & 0xff;
				byte byteMask = (byte) ((byteArray[index + getOffset()] & mask));
				byteMask |= selectedColorIndex << ((3 - ix) * 2);
				if (!isReadOnly())
				{
					byteArray[index + getOffset()] = byteMask;
				}
				gc.setBackground(paintMode ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);
			}
			else
			{
				int index = (((iy * currentWidth) + ix) >> 3) + offset;
				byte byteMask = byteArray[index + getOffset()];
				int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
				if (!isReadOnly())
				{
					byteArray[index + getOffset()] = paintMode ? (byte) (byteMask | pixelMask) : (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
				}
				gc.setBackground(paintMode ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);
			}

			int pix = isPixelGridEnabled() ? 1 : 0;
			gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix, currentPixelWidth - pix, currentPixelHeight - pix);
		}

	}

	public void setColorProvider(IColorProvider colorProvider)
	{
		this.colorProvider = colorProvider;
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

	public int getTileColumns()
	{
		return tileColumns;
	}

	public void setTileColumns(int tileColumns)
	{
		this.tileColumns = tileColumns;
		reset();
	}

	public int getTileRows()
	{
		return tileRows;
	}

	public void setTileRows(int tileRows)
	{
		this.tileRows = tileRows;
		reset();
	}

	public int getViewportSize()
	{
		int viewPortSize = (getWidth() / 8) * getHeight() * tileColumns * tileRows;
		return viewPortSize;

		// return getWidth() / 8 * getColumns() * getHeight() * getRows();
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

	public boolean isTileSubGridEnabled()
	{
		return tileSubGridEnabled;
	}

	public void setTileSubGridEnabled(boolean tileSubGridEnabled)
	{
		this.tileSubGridEnabled = tileSubGridEnabled;
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

	public boolean isSeparatorEnabled()
	{
		return separatorEnabled;
	}

	public void setSeparatorEnabled(boolean separatorEnabled)
	{
		this.separatorEnabled = separatorEnabled;
		redraw();
	}

	public boolean isTileCursorEnabled()
	{
		return tileCursorEnabled;
	}

	public void setTileCursorEnabled(boolean tileCursorEnabled)
	{
		this.tileCursorEnabled = tileCursorEnabled;
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

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	public boolean isReadOnly()
	{
		return this.readOnly;
	}

	private void reset()
	{
		bytesPerRow = width >> 3;
		// byteArray = new byte[(height * bytesPerRow * columns * rows)];
		redraw();
	}

	public void setGridStyle(int gridStyle)
	{
		this.gridStyle = gridStyle;
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

	public void setOffset(int offset)
	{
		this.offset = offset;
		drawMode = SET_DRAW_ALL;
		redraw();
	}

	public int getOffset()
	{
		return this.offset;
	}

	public void addDrawListener(IDrawListener redrawListener)
	{
		drawListenerList.add(redrawListener);
	}

	public void removeDrawListener(IDrawListener redrawListener)
	{
		drawListenerList.remove(redrawListener);
	}

	private void fireDrawTile(int x, int y)
	{
		for (IDrawListener listener : drawListenerList)
		{
			listener.drawTile(x, y);
		}
	}

	private void fireDrawAll()
	{
		for (IDrawListener listener : drawListenerList)
		{
			listener.drawAll();
		}
	}

	private void fireSetSelectedTileOffset(int offset)
	{
		for (IDrawListener listener : drawListenerList)
		{
			listener.setSelectedTileOffset(offset);
		}
	}

	private void fireDrawPixel(int x, int y)
	{
		for (IDrawListener listener : drawListenerList)
		{
			listener.drawPixel(x, y);
		}
	}

	private void setDrawMode(int drawMode)
	{
		this.drawMode = drawMode;
	}

	private int getDrawMode()
	{
		return drawMode;
	}

	@Override
	public void drawTile(int x, int y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPixel(int x, int y)
	{
		cursorX = x + (selectedTileIndexX * width);
		cursorY = y + (selectedTileIndexY * height);
		drawPixel();
	}

	private void drawPixel()
	{
		drawMode = SET_DRAW_PIXEL;
		System.out.println(getPainterName() + ":   x:" + cursorX + "    y:" + cursorY);
		int inset = isPixelGridEnabled() ? 1 : 0;
		redraw((cursorX * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset, currentPixelWidth - inset, currentPixelHeight - inset, true);

	}

	@Override
	public void drawAll()
	{
		drawMode = SET_DRAW_ALL;
		redraw();
	}

	@Override
	public void setSelectedTileOffset(int offset)
	{
		System.out.println(getPainterName() + ":offset=" + offset);
		setOffset(offset);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed)
	{
		return new Point((width * currentPixelWidth * tileColumns * columns), (height * currentPixelHeight * tileRows * rows));
	}
}