package de.drazil.nerdsuite.viewer;

import java.util.ArrayList;
import java.util.List;

public class BinaryTableModel implements ITableModel<RowDescriber>
{

	//private byte binaryData[] = null;
	private List<RowDescriber> rowDescriberList = null;

	public BinaryTableModel(byte[] binaryData, int start, int bytesPerRow)
	{
		//this.binaryData = binaryData;
		rowDescriberList = new ArrayList<RowDescriber>();
		for (int i = 0, j = 0; i < binaryData.length; i += bytesPerRow, j++)
		{
			rowDescriberList.add(new RowDescriber(0, i, bytesPerRow, binaryData, j % 2 == 0));
		}
	}

	@Override
	public List<RowDescriber> getRowItems()
	{
		return rowDescriberList;
	}

	@Override
	public RowDescriber[] getRowItem(int rowIndex)
	{
		return null;
	}

	@Override
	public RowDescriber getValueAt(int rowIndex, int columnIndex)
	{
		return null;
	}
}
