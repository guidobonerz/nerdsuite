package de.drazil.nerdsuite.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import de.drazil.nerdsuite.Constants;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class CpuInstruction extends AbstractInstruction
{
	private List<String> alias;
	private String type;
	private String flags;
	private String category;
	private boolean illegal;
	private boolean stable = true;
	private List<Opcode> opcodeList;

	public CpuInstruction()
	{
		opcodeList = new ArrayList<Opcode>();
	}

	@Override
	public int getIconIndex()
	{
		int index = -1;
		if (isIllegal() && !isStable())
			index = Constants.UNSTABLE_ILLEGAL_OPCODE;
		else if (isIllegal())
			index = Constants.ILLEGAL_OPCODE;
		else
			index = Constants.OPCODE;
		return index;
	}

}
