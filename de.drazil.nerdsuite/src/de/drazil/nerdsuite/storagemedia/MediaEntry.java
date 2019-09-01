package de.drazil.nerdsuite.storagemedia;

import java.util.ArrayList;
import java.util.List;

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
	private IAttributes attributes;
	private String fontName;
	private MediaEntry parent;
	private boolean isDirectory;
	private boolean isRoot;
	private Object userObject;

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

	public List<MediaEntry> getChildList() {
		return childList;
	}

	public void setChildList(List<MediaEntry> childList) {
		this.childList = childList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public int getSector() {
		return sector;
	}

	public void setSector(int sector) {
		this.sector = sector;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public IAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(IAttributes attributes) {
		this.attributes = attributes;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public MediaEntry getParent() {
		return parent;
	}

	public void setParent(MediaEntry parent) {
		this.parent = parent;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
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
