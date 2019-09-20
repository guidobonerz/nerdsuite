package de.drazil.nerdsuite.enums;

import lombok.Getter;

public enum ProjectType {
	CharSet("CHAR", ".nsChr", "icons/chr.png"), SpriteSet("SPRITE", ".nsSpr", "icons/spr.png"),
	ScreenSet("SCREEN", ".nsScr", "icons/scr.png"), Assembler("ASM", ".asm", null), Basic("bas", ".bas", null);

	@Getter
	private String id;
	@Getter
	private String suffix;
	@Getter
	private String iconName;

	private ProjectType(String id, String suffix, String iconName) {
		this.id = id;
		this.suffix = suffix;
		this.iconName = iconName;
	}

	public static ProjectType getProjectTypeById(String id) {
		ProjectType result = null;
		for (ProjectType pt : values()) {
			if (pt.getId().equalsIgnoreCase(id)) {
				result = pt;
				break;
			}
		}
		return result;
	}
}
