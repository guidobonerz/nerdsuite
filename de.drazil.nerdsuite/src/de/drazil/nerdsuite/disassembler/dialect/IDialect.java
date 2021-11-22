package de.drazil.nerdsuite.disassembler.dialect;

public abstract class IDialect
{

	public abstract String getTextDirective();

	public abstract String getByteDirective();

	public abstract String getWordDirective();

	public abstract String getLabelDirectivePattern();

	public abstract String getProgramCounterDirective();

	public abstract String getCommentPrefix();

	public abstract boolean supportsRelativeJumps();

	public abstract String getRelativeJumpLabel(int offset);

	public abstract String getRelativeJumpLabel();

}
