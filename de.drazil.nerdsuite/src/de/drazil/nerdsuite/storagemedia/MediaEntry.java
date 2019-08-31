package de.drazil.nerdsuite.storagemedia;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MediaEntry {

	private List<MediaEntry> childList;
	private int id;
	private String fullName;
	private String name;
	private String type;
	private int size;
	private int track;
	private int sector;
	private int offset;
	IAttributes attributes;
	private String fontName;

	public MediaEntry() {
		childList = new ArrayList<>();
	}

	public MediaEntry(int id, String fullName, String name, String type, int size, int track, int sector, int offset,
			IAttributes attributes) {
		this();
		this.id = id;
		this.fullName = fullName;
		this.name = name;
		this.type = type;
		this.size = size;
		this.track = track;
		this.sector = sector;
		this.offset = offset;
	}

	public void addChildEntry(MediaEntry entry) {
		childList.add(entry);
	}

	public int getChildrenCount() {
		return childList.size();
	}

	public boolean hasChildren() {
		return !childList.isEmpty();
	}

	public List<MediaEntry> getChildrenList() {
		return childList;
	}

	public void clear() {
		childList.clear();
	}
}
