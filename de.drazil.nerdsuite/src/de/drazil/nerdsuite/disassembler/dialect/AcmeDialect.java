package de.drazil.nerdsuite.disassembler.dialect;

public class AcmeDialect extends IDialect
{
	@Override
	public String getTextDirective()
	{
		return "!text";
	}

	@Override
	public String getByteDirective()
	{
		return "!byte";
	}

	@Override
	public String getWordDirective()
	{
		return "!word";
	}

	@Override
	public String getLabelDirectivePattern()
	{
		return "{labelname}";
	}

	@Override
	public String getProgramCounterDirective()
	{
		return "*=";
	}

	@Override
	public String getCommentPrefix()
	{
		return ";";
	}

	@Override
	public String getRelativeJumpLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRelativeJumpLabel(int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsRelativeJumps()
	{
		return true;
	}
}
