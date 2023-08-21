package de.drazil.nerdsuite.enums;


public enum AnimationMode {
	Slower(0, "slower"), Backward(1, "backward"), Stop(2, "stop"), Forward(3, "forward"), Faster(4, "faster");

	private int id;
	private String name;

	private AnimationMode(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static AnimationMode getAnimationModeByName(String name) {
		AnimationMode result = null;
		for (AnimationMode sv : values()) {
			if (sv.getName().equalsIgnoreCase(name)) {
				result = sv;
				break;
			}
		}
		return result;
	}
}
