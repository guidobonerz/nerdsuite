package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicToken {
	private List<Integer> version;
	private String prefix;
	private String token;
}
