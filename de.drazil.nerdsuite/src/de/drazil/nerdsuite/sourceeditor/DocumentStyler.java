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
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.StyleRangeCacheEntry;
import de.drazil.nerdsuite.util.SwtUtil;

public class DocumentStyler implements LineStyleListener {
	private final static String MULTI_LINE_RULE = "MultiLineRule";
	private final static String SINGLE_LINE_RULE = "SingleLineRule";
	private final static String WORD_RULE = "WordRule";
	private final static String PATTERN_RULE = "PatternRule";

	private List<Token> multiLineTokenList;
	private List<StyleRangeCacheEntry> styleRangeCache;
	private Map<String, TextStyle> styleMap;
	private Map<String, List<IRule>> ruleMap;
	private IDocument document = null;
	private static Color[] braceDepthColors = new Color[] { new Color(Display.getCurrent(), SwtUtil.toRGB("#C0392B")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#F5B7B1")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#9B59B6")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#1ABC9C")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#3498DB")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#AAB7B8")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#E74C3C")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#F1C40F")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#F39C12")),
			new Color(Display.getCurrent(), SwtUtil.toRGB("#129CF3")) };

	public DocumentStyler(IDocument document) {

		this.document = document;
		System.out.println("linefeed length:" + System.getProperty("line.separator").toCharArray().length);
		this.multiLineTokenList = new ArrayList<Token>();
		ruleMap = new HashMap<String, List<IRule>>();
		styleRangeCache = new ArrayList<StyleRangeCacheEntry>();
		this.styleMap = new HashMap<>();
		this.styleMap.put(Constants.T_PETME642YASCII, Constants.TEXTSTYLE_PetMe642Y_ASCII);
		this.styleMap.put(Constants.T_PETME2YASCII, Constants.TEXTSTYLE_PetMe2Y_ASCII);
		this.styleMap.put(Constants.T_C64_BASIC_STRING, Constants.TEXTSTYLE_C64_ASCII);
		this.styleMap.put(Constants.T_Atari_BASIC_STRING, Constants.TEXTSTYLE_ATARI_ASCII);
		this.styleMap.put(Constants.T_COMMENT, Constants.TEXTSTYLE_COMMENT);
		this.styleMap.put(Constants.T_COMMENT_BLOCK, Constants.TEXTSTYLE_ILLEGAL_OPCODE);
		this.styleMap.put(Constants.T_COMMAND, Constants.TEXTSTYLE_COMMAND);
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

		// processTokensById(MULTI_LINE_RULE, text);
	}

	public void cleanupLines(int lineNo) {
		while (styleRangeCache.size() > lineNo) {
			styleRangeCache.remove(styleRangeCache.size() - 1);
		}
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		StyleRangeCacheEntry styleRangeCacheEntry = null;
		int lineOffset = event.lineOffset;
		int lineNo = document.getLineAtOffset(lineOffset);

		if (lineNo < styleRangeCache.size()) {
			styleRangeCacheEntry = styleRangeCache.get(lineNo);
		}

		if (styleRangeCacheEntry == null) {
			styleRangeCacheEntry = new StyleRangeCacheEntry();
			List<StyleRange> styleRangeList = new ArrayList<StyleRange>();
			styleRangeCacheEntry.setStyleRangeList(styleRangeList);
			styleRangeCacheEntry.setLineIndex(lineNo);
			styleRangeCacheEntry.setLineOffset(lineOffset);
			// parseText(ruleMap.get(MULTI_LINE_RULE), lineOffset, document.getText(),
			// styleRangeList, null);
			parseText(ruleMap.get(SINGLE_LINE_RULE), lineOffset, event.lineText.toLowerCase(), styleRangeList, null);
			parseText(ruleMap.get(WORD_RULE), lineOffset, event.lineText.toLowerCase(), styleRangeList, null);
			parseBraces(lineOffset, event.lineText.toLowerCase(), styleRangeList, null);

			styleRangeList.sort(new Comparator<StyleRange>() {
				@Override
				public int compare(StyleRange o1, StyleRange o2) {
					return Integer.compare(o1.start, o2.start);
				}
			});
			if (lineNo < styleRangeCache.size()) {
				styleRangeCache.add(lineNo, styleRangeCacheEntry);
			} else {
				styleRangeCache.add(styleRangeCacheEntry);
			}
		}
		event.styles = styleRangeCacheEntry.getStyleRangeList()
				.toArray(new StyleRange[styleRangeCacheEntry.getStyleRangeList().size()]);

	}

	private void parseBraces(int lineOffset, String text, List<StyleRange> styleRangeList, Color backgroundColor) {
		int openBracesCount = 0;
		int closedBracesCount = 0;
		int braceDepth = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '(') {
				if (!isInExistingStyleRange(lineOffset, i, 1, styleRangeList)) {
					StyleRange styleRange = new StyleRange(lineOffset + i, 1, braceDepthColors[braceDepth], null);
					styleRangeList.add(styleRange);
					openBracesCount++;
					braceDepth++;
					System.out.println(braceDepth);
				}
			}
			if (text.charAt(i) == ')') {
				if (!isInExistingStyleRange(lineOffset, i, 1, styleRangeList)) {
					braceDepth--;
					StyleRange styleRange = new StyleRange(lineOffset + i, 1, braceDepthColors[braceDepth], null);
					styleRangeList.add(styleRange);
					closedBracesCount++;

				}
			}
			// System.out.printf("%s braces\n", (openBracesCount + closedBracesCount) % 2 ==
			// 0 ? "balanced" : "unbalanced");
		}
	}

	private void parseText(List<IRule> ruleList, int lineOffset, String text, List<StyleRange> styleRangeList,
			Color backgroundColor) {
		if (ruleList == null) {
			return;
		}

		int lo = lineOffset;
		int offset = 0;
		int len = 0;
		boolean hasMatch = false;

		while (offset < text.length()) {
			hasMatch = false;
			for (IRule rule : ruleList) {

				DocumentPartition partition = rule.hasMatch(text, offset);
				if (partition != null) {
					if (!isInExistingStyleRange(lo, partition.getOffset(), partition.getLen(), styleRangeList)) {
						len = partition.getLen();
						offset = partition.getOffset();

						Color c = null;
						if (rule.getTokenControl() == 0) {
							c = Constants.COMMAND_COLOR;
						} else if (rule.getTokenControl() == 1) {
							c = Constants.FUNCTION_COLOR;
						} else if (rule.getTokenControl() == 2) {
							c = Constants.OPERATOR_COLOR;
						} else if (rule.getTokenControl() == 3) {
							c = Constants.CONSTANT_COLOR;
						} else {
							c = styleMap.get(rule.getToken().getKey()).foreground;
						}
						StyleRange styleRange = new StyleRange(lo + partition.getOffset(), len, c, null);
						styleRange.font = styleMap.get(rule.getToken().getKey()).font;
						styleRangeList.add(styleRange);
						hasMatch = true;
					}

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

	private boolean isInExistingStyleRange(int offset, int po, int plen, List<StyleRange> styleRangeList) {
		for (StyleRange sr : styleRangeList) {
			if (offset + po >= sr.start && offset + po + plen <= sr.start + sr.length) {
				return true;
			}
		}
		return false;
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
