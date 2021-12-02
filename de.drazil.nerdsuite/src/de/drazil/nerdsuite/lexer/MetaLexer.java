package de.drazil.nerdsuite.lexer;

public class MetaLexer extends AbstractLexer {
	public final static LexicalRule NAME = new LexicalRule("name");
	public final static LexicalRule BASE = new LexicalRule("base");
	public final static LexicalRule IF = new LexicalRule("@if");
	public final static LexicalRule ELSE = new LexicalRule("@else");
	public final static LexicalRule END = new LexicalRule("@end");
	public final static LexicalRule BASIC = new LexicalRule("@basic");
	public final static LexicalRule ASM = new LexicalRule("@asm");

	private final static Expression NAME_ATTRIBUTE = new Expression(NAME, LexicalRule.EQUAL, LexicalRule.SINGLE_QUOTE,
			LexicalRule.ALPHANUMERIC, LexicalRule.SINGLE_QUOTE);
	private final static Expression BASE_ATTRIBUTE = new Expression(false, BASE, LexicalRule.EQUAL,
			LexicalRule.HEX_ADRESS_WORD);

	private final static Expression IF_EXPRESSION = new Expression(IF, LexicalRule.PROPERTY, LexicalRule.DATA, END);
	private final static Expression IF_ELSE_EXPRESSION = new Expression(IF, LexicalRule.PROPERTY, LexicalRule.DATA,
			ELSE, LexicalRule.DATA, END);

	private final static Expression ASM_EXPRESSION = new Expression(ASM, new Set(NAME_ATTRIBUTE, BASE_ATTRIBUTE),
			LexicalRule.DATA, END);
	private final static Expression BASIC_EXPRESSION = new Expression(BASIC, new Set(NAME_ATTRIBUTE), LexicalRule.DATA,
			END);

	public MetaLexer() {
		addExpression(IF_EXPRESSION);
		addExpression(IF_ELSE_EXPRESSION);
		addExpression(ASM_EXPRESSION);
		addExpression(BASIC_EXPRESSION);
	}

	private static String content = "10 print\"hallo\":a=1\n" + "@if ${debug}\n" + "poke 53280,1:poke53281,0\n"
			+ "20 print\"das ist ein test\"\n" + "@end\n" + "30 a=1:b=2:c=3\n " + "@asm name='test'\n" + "lda @#01\n"
			+ "sta $d020\n" + "@end";

	public static void main(String argv[]) {

	}
}
