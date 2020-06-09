package de.drazil.nerdsuite.storagemedia;

public class TrackAvailability {
			
	private int[] entityArray;

	public TrackAvailability(int entityCount) {
		entityArray = new int[entityCount];
	}

	public int getEntityByIndex(int index) {
		return entityArray[index];
	}

	public int getSize() {
		return entityArray.length;
	}
}
 