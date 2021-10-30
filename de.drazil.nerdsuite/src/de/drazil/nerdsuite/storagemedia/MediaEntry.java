package de.drazil.nerdsuite.storagemedia;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaEntry {

	private List<MediaEntry> childrenList;
	private int id;
	private String fullName;
	private String name;
	private String type;
	private int size;
	private int track;
	private int sector;
	private IAttributes attributes;
	private String fontName;
	private MediaEntry parent;
	private boolean isDirectory;
	private boolean isRoot;
	private boolean isSystemDisk;
	private Object dataLocation;
	private Object userObject;
	private int contentOffset;

	public MediaEntry() {
		childrenList = new ArrayList<>();
	}

	public MediaEntry(int id, String fullName, String name, String type, int size, int track, int sector,
			Object dataLocation, IAttributes attributes) {
		this();
		this.id = id;
		this.fullName = fullName;
		this.name = name;
		this.type = type;
		this.size = size;
		this.track = track;
		this.sector = sector;
		this.dataLocation = dataLocation;
	}

	public int getChildrenCount() {
		return childrenList.size();
	}

	public boolean hasChildren() {
		return !childrenList.isEmpty();
	}

	public List<MediaEntry> getChildrenList() {
		return childrenList;
	}

	public void addChildrenEntry(MediaEntry entry) {
		childrenList.add(entry);
	}

	public void clear() {
		childrenList.clear();
	}
}
