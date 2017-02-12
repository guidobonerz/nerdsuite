package de.drazil.nerdsuite.viewer;

import java.util.List;

public interface ITableModel<BEAN>
{
	public List<BEAN> getRowItems();

	public BEAN[] getRowItem(int rowIndex);

	public BEAN getValueAt(int rowIndex, int columnIndex);

}
