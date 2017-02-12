package de.drazil.nerdsuite.viewer;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.util.IFont;

public class TableLabelProvider implements ITableLabelProvider, IColorProvider
{

	private IFont font = null;

	public TableLabelProvider(IFont font)
	{
		this.font = font;

	}

	@Override
	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		RowDescriber rowDescriber = (RowDescriber) element;
		byte binaryData[] = rowDescriber.getBinaryData();
		switch (columnIndex)
		{
			case 0:
			{
				return String.format("%04x", rowDescriber.getStart() + rowDescriber.getOffset());
			}
			case 17:
			{
				StringBuffer sb = new StringBuffer();

				for (int i = rowDescriber.getOffset(); i < rowDescriber.getOffset() + rowDescriber.getLength(); i++)
				{

					if (i < binaryData.length)
					{
						// char c = (char) binaryData[i];
						// sb.append(isPrintableCharacter(c) ? c : ' ');
						sb.append(Character.toChars(font.getUnicodePrefix() | ((int) binaryData[i]) & 0xff));
					}
					else
					{
						sb.append("");
					}
				}

				return sb.toString();
			}
			default:
			{
				int offset = rowDescriber.getOffset() + columnIndex - 1;
				if (offset < binaryData.length)
				{
					return String.format("%02x", binaryData[offset]);
				}
				else
				{
					return "";
				}

			}
		}
	}

	@Override
	public Color getBackground(Object element)
	{
		RowDescriber rowDescriber = (RowDescriber) element;
		return rowDescriber.isOdd() ? Constants.LIGHT_GREEN : Constants.WHITE;
	}

	@Override
	public Color getForeground(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private static boolean isPrintableCharacter(char c)
	{
		return c >= 32 && c < 127;
	}
}
