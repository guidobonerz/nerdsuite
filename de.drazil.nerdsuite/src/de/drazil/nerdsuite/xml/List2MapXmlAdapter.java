package de.drazil.nerdsuite.xml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.drazil.nerdsuite.xml.List2MapXmlAdapter.ListProvider;

public abstract class List2MapXmlAdapter<LP extends ListProvider<Value>, Value> extends XmlAdapter<LP, Map<String, Value>>
{
	protected abstract String getKey(Value value);

	protected abstract LP createListProvider();

	@Override
	public Map<String, Value> unmarshal(LP listProvider) throws Exception
	{
		if (null == listProvider)
		{
			return null;
		}
		Map<String, Value> map = new LinkedHashMap<String, Value>(listProvider.getList().size());
		for (Value value : listProvider.getList())
		{
			map.put(getKey(value), value);
		}
		return map;
	}

	@Override
	public LP marshal(Map<String, Value> map) throws Exception
	{
		if (null == map)
		{
			return null;
		}
		LP listProvider = createListProvider();
		for (Entry<String, Value> entry : map.entrySet())
		{
			listProvider.getList().add(entry.getValue());
		}
		return listProvider;
	}

	protected abstract static class ListProvider<Value>
	{
		public abstract List<Value> getList();
	}
}
