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

public class DocumentStyler implements LineStyleListener {
	private final static String MULTI_LINE_RULE = "MultiLineRule";
	private final static String SINGLE_LINE_RULE = "SingleLineRule";
	private final static String WORD_RULE = "WordRule";
	private final static String PATTERN_RULE = "PatternRule";

	private List<Token> lineTokenList;
	private List<Token> multiLineTokenList;
	private Map<String, TextStyle> styleMap;
	private Map<String, List<IRule>> ruleMap;
	private IDocument document = null;
	private int offset = 0;
	private List<List<StyleRange>> styleRangeCache;

	public DocumentStyler(IDocument document) {

		this.document = document;
		System.out.println("linefeed length:" + System.getProperty("line.separator").toCharArray().length);
		this.multiLineTokenList = new ArrayList<Token>();
		ruleMap = new HashMap<String, List<IRule>>();
		styleRangeCache = new ArrayList<List<StyleRange>>();
		this.styleMap = new HashMap<>();
		this.styleMap.put(Constants.T_PETME642YASCII, Constants.TEXTSTYLE_PetMe642Y_ASCII);
		this.styleMap.put(Constants.T_PETME2YASCII, Constants.TEXTSTYLE_PetMe2Y_ASCII);
		this.styleMap.put(Constants.T_C64_BASIC_STRING, Constants.TEXTSTYLE_C64_ASCII);
		this.styleMap.put(Constants.T_Atari_BASIC_STRING, Constants.TEXTSTYLE_ATARI_ASCII);
		this.styleMap.put(Constants.T_COMMENT, Constants.TEXTSTYLE_COMMENT);
		this.styleMap.put(Constants.T_BASIC_COMMAND, Constants.TEXTSTYLE_OPCODE);
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

	public void refreshMultilineComments(String text) {
		multiLineTokenList.clear();

		processTokensById(MULTI_LINE_RULE, text);
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		List<StyleRange> styleRangeList = null;
		int lineOffset = event.lineOffset;
		int lineNo = document.getLineAtOffset(lineOffset);

		try {
			styleRangeList = styleRangeCache.get(lineNo);
		} catch (Exception e) {
		}

		if (styleRangeList == null) {
			styleRangeList = new ArrayList<>();
			System.out.printf("line %d\n", lineNo);
			Color backgroundColor = (document.getCurrentLineIndex() == document.getLineAtOffset(event.lineOffset)
					? Constants.SOURCE_EDITOR_HIGHLIGHTED_BACKGROUND_COLOR
					: Constants.SOURCE_EDITOR_BACKGROUND_COLOR);

			scanLine(ruleMap.get(WORD_RULE), lineOffset, event.lineText.toLowerCase(), styleRangeList, backgroundColor);

			/*
			 * Token token = isInsideTokenRange(multiLineTokenList, event.lineOffset,
			 * event.lineText.length()); if (token == null) {
			 * 
			 * for (String id : ruleMap.keySet()) { if (!id.equals(MULTI_LINE_RULE)) {
			 * processTokensById(id, event.lineText); } } buildStyles(lineTokenList,
			 * styleRangeList, event.lineOffset, backgroundColor); } else // this is the
			 * multiline block {
			 * 
			 * StyleRange styleRange = new StyleRange(styleMap.get(token.getKey()));
			 * styleRange.start = token.getStart() >= event.lineOffset ? token.getStart() :
			 * event.lineOffset; styleRange.length = token.getStart() > event.lineOffset &&
			 * token.getStart() + token.getLength() < event.lineOffset +
			 * event.lineText.length() ? token.getStart() + token.getLength() -
			 * token.getStart() : token.getStart() + token.getLength() - event.lineOffset;
			 * styleRange.background = backgroundColor;
			 * 
			 * styleRangeList.add(styleRange);
			 * 
			 * } int startIndex = event.lineText.indexOf(""); int endIndex =
			 * event.lineText.indexOf(""); if (startIndex > 0 && endIndex > 0) { StyleRange
			 * styleRange = new StyleRange(styleMap.get(Constants.T_PETME642YASCII));
			 * styleRange.start = startIndex + 2; styleRange.length = endIndex -
			 * styleRange.start; styleRangeList.add(styleRange); }
			 */
			styleRangeCache.add(styleRangeList);

		}
		event.styles = styleRangeList.toArray(new StyleRange[styleRangeList.size()]);

	}

	private void scanLine(List<IRule> ruleList, int lineOffset, String text, List<StyleRange> styleRangeList,
			Color backgroundColor) {
		int lo = lineOffset;
		int offset = 0;
		int len = 0;
		boolean hasMatch = false;
		while (offset < text.length()) {
			hasMatch = false;
			for (IRule rule : ruleList) {
				int matchIndex = text.indexOf(rule.getPrefix(), offset);
				if (offset == matchIndex) {
					len = rule.getPrefix().length();
					StyleRange styleRange = new StyleRange(lo + offset, len,
							styleMap.get(rule.getToken().getKey()).foreground, backgroundColor);
					styleRangeList.add(styleRange);
					hasMatch = true;
					break;
				}
			}
			if (hasMatch) {
				offset += len;
			} else {
				offset++;
			}
		}
	}

	private void processTokensById(String id, String text) {
		offset = 0;
		List<IRule> ruleList = ruleMap.get(id);
		if (ruleList != null && ruleList.size() > 0) {
			for (IRule rule : ruleList) {
				findTokens(rule, text);
			}
		}

	}

	private void findTokens(IRule rule, String text) {

		if (text.equals("")) {
			return;
		}
		System.out.printf("try to find[ %s ]\n", rule.getPrefix());
		while (rule.hasMatch(text, offset)) {
			offset = rule.getOffset();
			Token token = rule.getToken();
			Token tokenCopy = Token.copy(token);
			if (rule instanceof MultiLineRule) {
				multiLineTokenList.add(tokenCopy);
				// System.out.println("foundMultiLineMatch:" + token.toString());
			} else {
				// System.out.println("foundSingleLineMatch:" + token.toString());
				lineTokenList.add(tokenCopy);
			}
		}
	}

	public void addRule(IRule rule) {
		if (rule instanceof MultiLineRule) {
			_addRule(MULTI_LINE_RULE, rule);
		} else if (rule instanceof SingleLineRule) {
			_addRule(SINGLE_LINE_RULE, rule);
		} else if (rule instanceof WordRule) {
			_addRule(WORD_RULE, rule);
		} else if (rule instanceof ValueRule) {
			_addRule(PATTERN_RULE, rule);
		}
	}

	private void _addRule(String ruleName, IRule rule) {
		List<IRule> ruleList = ruleMap.get(ruleName);
		if (ruleList == null) {
			ruleList = new ArrayList<IRule>();
			ruleMap.put(ruleName, ruleList);
		}
		ruleList.add(rule);
		Collections.sort(ruleList, new Comparator<IRule>() {
			@Override
			public int compare(IRule o1, IRule o2) {
				return Integer.compare(o1.getPriority(), o2.getPriority());
			}
		});
	}
}
