package de.drazil.nerdsuite.enums;

public enum WizardType {

	NewProject(0, "NEW"), ImportAsNewProject(1, "NEW_IMPORT");

	private int id;
	private String name;

	private WizardType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static WizardType getWizardTypeById(int id) {
		WizardType result = null;
		for (WizardType sv : values()) {
			if (sv.getId()==id) {
				result = sv;
				break;
			}
		}
		return result;
	}

	public static WizardType getWizardTypeByName(String name) {
		WizardType result = null;
		for (WizardType sv : values()) {
			if (sv.getName().equalsIgnoreCase(name)) {
				result = sv;
				break;
			}
		}
		return result;
	}
}
