package de.drazil.nerdsuite.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectFolder
{
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;

	private Project parent;

	public ProjectFolder(String id, String name)
	{
		setId(id);
		setName(name);
	}

}
