package de.drazil.nerdsuite.sourceeditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;

import de.drazil.nerdsuite.Constants;

public class DocumentStyler implements LineStyleListener
{
	private final static String MULTI_LINE_RULE = "MultiLineRule";
	private final static String SINGLE_LINE_RULE = "SingleLineRule";
	private final static String WORD_RULE = "WordRule";
	private final static String PATTERN_RULE = "PatternRule";
	private List<StyleRange> styleRangeList;
	private List<Token> lineTokenList;
	private List<Token> multiLineTokenList;
	private Map<String, TextStyle> styleMap;
	private Map<String, List<IRule>> ruleMap;
	private IDocument document = null;

	public DocumentStyler(IDocument document)
	{
		this.document = document;
		System.out.println("linefeed length:" + System.getProperty("line.separator").toCharArray().length);
		this.multiLineTokenList = new ArrayList<Token>();
		ruleMap = new HashMap<String, List<IRule>>();
		this.styleMap = new HashMap<>();
		this.styleMap.put(Constants.T_COMMENT, Constants.TEXTSTYLE_COMMENT);
		this.styleMap.put(Constants.T_STRING, Constants.TEXTSTYLE_STRING);
		this.styleMap.put(Constants.T_DECIMAL, Constants.TEXTSTYLE_DECIMAL);
		this.styleMap.put(Constants.T_HEXADECIMAL, Constants.TEXTSTYLE_HEXADECIMAL);
		this.styleMap.put(Constants.T_ADRESS, Constants.TEXTSTYLE_HEXADECIMAL);
		this.styleMap.put(Constants.T_BINARY, Constants.TEXTSTYLE_BINARY);
		this.styleMap.put(Constants.T_LABEL, Constants.TEXTSTYLE_LABEL);
		this.styleMap.put(Constants.T_DIRECTIVE, Constants.TEXTSTYLE_DIRECTIVE);
		this.styleMap.put(Constants.T_OPCODE, Constants.TEXTSTYLE_OPCODE);
		this.styleMap.put(Constants.T_ILLEGAL_OPCODE, Constants.TEXTSTYLE_ILLEGAL_OPCODE);
		this.styleMap.put(Constants.T_UNSTABLE_ILLEGAL_OPCODE, Constants.TEXTSTYLE_UNSTABLE_ILLEGAL_OPCODE);

	}

	public void refreshMultilineComments(String text)
	{
		multiLineTokenList.clear();

		processTokensById(MULTI_LINE_RULE, text);
	}

	@Override
	public void lineGetStyle(LineStyleEvent event)
	{
		this.lineTokenList = new ArrayList<Token>();
		this.styleRangeList = new ArrayList<>();
		Color backgroundColor = (document.getCurrentLineIndex() == document.getLineAtOffset(event.lineOffset) ? Constants.SOURCE_EDITOR_HIGHLIGHTED_BACKGROUND_COLOR
				: Constants.SOURCE_EDITOR_BACKGROUND_COLOR);
		Token token = isInMultiLineBlock(event.lineOffset, event.lineText.length());
		if (token == null)
		{

			for (String id : ruleMap.keySet())
			{
				if (!id.equals(MULTI_LINE_RULE))
				{
					processTokensById(id, event.lineText);
				}
			}
			buildStyles(lineTokenList, styleRangeList, event.lineOffset, backgroundColor);
		}
		else
		// this is the multiline block
		{

			StyleRange styleRange = new StyleRange(styleMap.get(token.getKey()));
			styleRange.start = token.getStart() >= event.lineOffset ? token.getStart() : event.lineOffset;
			styleRange.length = token.getStart() > event.lineOffset && token.getStart() + token.getLength() < event.lineOffset + event.lineText.length() ? token
					.getStart() + token.getLength() - token.getStart() : token.getStart() + token.getLength() - event.lineOffset;
			styleRange.background = backgroundColor;
			styleRangeList.add(styleRange);

		}

		event.styles = styleRangeList.toArray(new StyleRange[styleRangeList.size()]);

	}

	private void buildStyles(List<Token> tokenList, List<StyleRange> styleRangeList, int lineOffset, Color backgoundColor)
	{
		for (Token token : tokenList)
		{
			StyleRange styleRange = new StyleRange(styleMap.get(token.getKey()));
			styleRange.start = lineOffset + token.getStart();
			styleRange.length = token.getLength();
			styleRange.background = backgoundColor;
			styleRangeList.add(styleRange);
		}
	}

	private void processTokensById(String id, String text)
	{
		List<IRule> ruleList = ruleMap.get(id);
		if (ruleList != null && ruleList.size() > 0)
		{
			for (IRule rule : ruleList)
			{
				findTokens(rule, text);
			}
		}

	}

	private void findTokens(IRule rule, String text)
	{
		while (rule.hasMatch(text))
		{
			Token token = rule.getToken();
			Token tokenCopy = Token.copy(token);
			if (rule instanceof MultiLineRule)
			{
				multiLineTokenList.add(tokenCopy);
				// System.out.println("foundMultiLineMatch:" + token.toString());
			}
			else
			{
				// System.out.println("foundSingleLineMatch:" + token.toString());
				lineTokenList.add(tokenCopy);
			}
			System.out.println(token);

		}

	}

	private Token isInMultiLineBlock(int lineOffset, int length)
	{
		for (Token token : multiLineTokenList)
		{
			if (lineOffset + length >= token.getStart() && lineOffset <= token.getStart() + token.getLength())
			{
				return token;
			}
		}
		return null;
	}

	public StyleRange[] getStyleRanges()
	{
		return this.styleRangeList.toArray(new StyleRange[styleRangeList.size()]);
	}

	public void addRule(IRule rule)
	{
		if (rule instanceof MultiLineRule)
		{
			_addRule(MULTI_LINE_RULE, rule);
		}
		else if (rule instanceof SingleLineRule)
		{
			_addRule(SINGLE_LINE_RULE, rule);
		}
		else if (rule instanceof WordRule)
		{
			_addRule(WORD_RULE, rule);
		}
		else if (rule instanceof ValueRule)
		{
			_addRule(PATTERN_RULE, rule);
		}
	}

	private void _addRule(String ruleName, IRule rule)
	{
		List<IRule> ruleList = ruleMap.get(ruleName);
		if (ruleList == null)
		{
			ruleList = new ArrayList<IRule>();
			ruleMap.put(ruleName, ruleList);
		}
		ruleList.add(rule);
		Collections.sort(ruleList, new Comparator<IRule>()
		{
			@Override
			public int compare(IRule o1, IRule o2)
			{
				return o1.getPrefix().compareToIgnoreCase(o2.getPrefix());
			}
		});
	}
}
