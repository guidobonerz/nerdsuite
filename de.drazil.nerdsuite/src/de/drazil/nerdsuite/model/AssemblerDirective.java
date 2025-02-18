package de.drazil.nerdsuite.model;

import java.util.List;

import de.drazil.nerdsuite.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class AssemblerDirective extends AbstractInstruction {
	private List<String> alias;
	private String nodename;
	private String param;
	private boolean hasOutline;
	private String pattern;
	private String matcher;

	@Override
	public int getIconIndex() {
		return Constants.DIRECTIVE;
	}
}
