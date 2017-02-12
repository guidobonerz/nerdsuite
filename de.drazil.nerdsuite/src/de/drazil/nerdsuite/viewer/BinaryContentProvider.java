package de.drazil.nerdsuite.viewer;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class BinaryContentProvider implements IStructuredContentProvider
{

	public BinaryContentProvider()
	{

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement == null)
			return null;
		else
		{
			List<Object> dataList = ((ITableModel) inputElement).getRowItems();
			return dataList.toArray(new Object[dataList.size()]);
		}
	}
}
