package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import de.drazil.nerdsuite.Constants;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class AssemblerDirective extends AbstractInstruction
{
	private List<String> alias;
	private String nodename;
	private String param;
	private boolean hasOutline;
	private String pattern;
	private String matcher;

	@Override
	public int getIconIndex()
	{
		return Constants.DIRECTIVE;
	}
}
