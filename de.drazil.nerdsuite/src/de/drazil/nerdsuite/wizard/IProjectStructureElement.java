package de.drazil.nerdsuite.wizard;

public interface IProjectStructureElement
{
	public IProjectStructureElement[] getElements();

	public void setParent(IProjectStructureElement parent);

	public IProjectStructureElement getParent();
}
