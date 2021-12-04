package de.drazil.nerdsuite.mouse;

import de.drazil.nerdsuite.mouse.AdvancedMouseAdaper.MouseButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedMouseAdapterEvent {
	private MouseButton button;
	private int modifierMask;
	private int x;
	private int y;
}
