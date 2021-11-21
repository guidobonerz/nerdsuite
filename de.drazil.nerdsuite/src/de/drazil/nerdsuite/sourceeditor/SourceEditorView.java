package de.drazil.nerdsuite.sourceeditor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.basic.SourceRepositoryService;
import de.drazil.nerdsuite.configuration.Initializer;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.util.C64Font;
import de.drazil.nerdsuite.util.IFont;
import de.drazil.nerdsuite.widget.PlatformFactory;

public class SourceEditorView implements IDocument {

	enum WordBounds {
		Begin, End
	};

	private StyledText styledText = null;
	private DocumentStyler documentStyler;
	private Project project;
	private String owner;
	private SourceRepositoryService srs;
	@Inject
	private MPart part;
	int x = 0xe000;

	public SourceEditorView() {

		/*
		 * for (AssemblerDirective directive : InstructionSet.getDirectiveList()) {
		 * documentStyler.addRule(new WordRule(directive.getId(), new
		 * Token(Constants.T_DIRECTIVE))); } for (CpuInstruction instruction :
		 * InstructionSet.getCpuInstructionList("")) { documentStyler.addRule(new
		 * WordRule(instruction.getId(), new
		 * Token(CPU_6510.getInstructionTokenKey(instruction.isIllegal(),
		 * !instruction.isStable())))); }
		 */
		// --------------------- erstmal nicht
		// documentStyler.addRule(new ValueRule("!", ":", new Token("LABEL")));
		// scanner.addRule(new SingleLineRule("!", new Token("LABEL")));
		// scanner.addRule(new SingleLineRule(":", new Token("MACRO")));
	}

	private DocumentStyler getBasicStyler(BasicInstructions basicInstructions, int version) {
		documentStyler = new DocumentStyler(this);
		documentStyler.addRule(new MultiLineRule(basicInstructions.getBlockComment()[0],
				basicInstructions.getBlockComment()[1], new Token(Constants.T_COMMENT_BLOCK)));
		documentStyler.addRule(new SingleLineRule(basicInstructions.getSingleLineComment(), Marker.EOL,
				new Token(Constants.T_COMMENT)));
		documentStyler.addRule(new SingleLineRule("//", Marker.EOL, new Token(Constants.T_COMMENT)));
		documentStyler.addRule(new SingleLineRule(basicInstructions.getStringQuote(),
				basicInstructions.getStringQuote(), new Token(Constants.T_C64_BASIC_STRING)));
		// documentStyler.addRule(new SingleLineRule("", ":", new
		// Token(Constants.T_LABEL)));
		// documentStyler.addRule(new ValueRule("#", "d", 5, new
		// Token(Constants.T_DECIMAL)));
		// documentStyler.addRule(new ValueRule("$", "h", 4, new
		// Token(Constants.T_ADRESS)));
		// documentStyler.addRule(new ValueRule("#%", "b", 8, new
		// Token(Constants.T_BINARY)));
		// documentStyler.addRule(new ValueRule("#$", "h", 2, new
		// Token(Constants.T_HEXADECIMAL)));

		List<BasicInstruction> list = basicInstructions.getBasicInstructionList().stream()
				.filter(e -> e.getMinVersion() < version).collect(Collectors.toList());

		for (BasicInstruction bi : basicInstructions.getBasicInstructionList()) {
			if (!bi.isComment()) {
				documentStyler.addRule(new WordRule(bi, new Token(Constants.T_COMMAND)));
			}
		}

		return documentStyler;
	}

	@Inject
	@Optional
	public void manageSave(@UIEventTopic("Save") BrokerObject brokerObject) {
		if (brokerObject.getOwner().equalsIgnoreCase(owner)) {
			save();
		}
	}

	private void save() {
		System.out.println("save source");
		srs.setContent(styledText.getText());
		updateWorkspace(false);
		LocalDateTime ldt = LocalDateTime.now();
		Date d = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		project.setChangedOn(d);
		srs.save(project);
		part.setDirty(false);
	}

	@Persist
	private void close() {
		save();
	}

	/**
	 * Create contents of the view part.
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent, MApplication app, MTrimmedWindow window, EMenuService menuService) {

		Map<String, Object> pm = (Map<String, Object>) part.getObject();
		project = (Project) pm.get("project");
		owner = (String) pm.get("repositoryOwner");

		srs = ServiceFactory.getService(project.getId(), SourceRepositoryService.class);
		BasicInstructions basicInstructions = PlatformFactory.getBasicInstructions(srs.getMetadata().getPlatform());

		int version = 20;
		String variant = srs.getMetadata().getVariant();
		if (variant.equals("V20")) {
			version = 20;
		} else if (variant.equals("V35")) {
			version = 35;
		} else if (variant.equals("V70")) {
			version = 70;
		} else {
			version = 0;
		}

		part.setDirty(false);
		part.getTransientData().put(Constants.OWNER, owner);
		part.setTooltip("basic Source File");

		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
		styledText = new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setText(srs.getContent() == null ? "" : srs.getContent());
		styledText.setBackground(Constants.SOURCE_EDITOR_BACKGROUND_COLOR);
		styledText.setForeground(Constants.SOURCE_EDITOR_FOREGROUND_COLOR);
		styledText.setFont(Constants.RobotoMonoBold_FONT);
		styledText.addLineStyleListener(getBasicStyler(basicInstructions, version));
		styledText.addLineBackgroundListener(new LineBackgroundListener() {
			@Override
			public void lineGetBackground(LineBackgroundEvent event) {
				event.lineBackground = (styledText.getLineAtOffset(styledText.getCaretOffset()) == styledText
						.getLineAtOffset(event.lineOffset) ? Constants.SOURCE_EDITOR_HIGHLIGHTED_BACKGROUND_COLOR
								: Constants.SOURCE_EDITOR_BACKGROUND_COLOR);
			}
		});

		styledText.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				event.gc.setForeground(Constants.SOURCE_EDITOR_HIGHLIGHTED_FOREGROUND_COLOR);
				int line = styledText.getOffsetAtLine(styledText.getLineAtOffset(styledText.getCaretOffset()));
				Point topLeft = styledText.getLocationAtOffset(line);
				event.gc.drawRectangle(topLeft.x - 1, topLeft.y, styledText.getBounds().width,
						styledText.getLineHeight());

			}
		});

		styledText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN
				// ||
				// e.keyCode == SWT.PAGE_DOWN || e.keyCode == SWT.PAGE_UP)
				// {
				// styledText.redraw();
				// }

			}
		});

		/*
		 * styledText.addExtendedModifyListener(new ExtendedModifyListener() {
		 * 
		 * @Override public void modifyText(ExtendedModifyEvent event) {
		 * System.out.println("Ext Modify Text"); // TODO Auto-generated method stub }
		 * });
		 */

		styledText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				System.out.println("modify");
				// documentStyler.refreshMultilineComments(styledText.getText());
				styledText.redraw();
			}
		});
/*
		Button button = new Button(parent, SWT.NONE);
		button.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				IFont font = new C64Font();
				// int cursorPos = styledText.getCaretOffset();
				styledText.insert(new String(Character.toChars(x)));
				x++;
			}
		});
*/
		/*
		 * FontData[] fD = styledText.getFont().getFontData(); fD[0].setHeight(12);
		 * styledText.setFont(new Font(parent.getDisplay(), fD[0]));
		 */
		/*
		 * styledText.getVerticalBar().addListener(SWT.Selection, new Listener() { int
		 * lastIndex = styledText.getTopIndex();
		 * 
		 * public void handleEvent(Event e) { int index = styledText.getTopIndex(); if
		 * (index != lastIndex) { lastIndex = index; styledText.redraw(); } } });
		 */
	}

	@Override
	public String getText() {
		return this.styledText.getText();
	}

	@Override
	public int getCurrentCharOffset() {
		return this.styledText.getCaretOffset();
	}

	@Override
	public int getFirstVisibleLineIndex() {
		return this.styledText.getTopIndex();
	}

	@Override
	public int getFirstVisibleLineOffset() {

		return this.styledText.getOffsetAtLine(getFirstVisibleLineIndex());
	}

	@Override
	public int getLineOffsetAtlineIndex(int lineIndex) {
		return this.styledText.getOffsetAtLine(lineIndex);
	}

	@Override
	public int getLineCount() {
		return this.styledText.getLineCount();
	}

	@Override
	public int getVisibleLineCount() {
		int vlc = styledText.getClientArea().height / styledText.getLineHeight();
		return vlc > getLineCount() ? getLineCount() : vlc;
	}

	@Override
	public String getLineAtIndex(int index) {
		return this.styledText.getLine(index);
	}

	@Override
	public int getLineAtOffset(int offset) {
		return this.styledText.getLineAtOffset(offset);
	}

	@Override
	public int getCurrentLineIndex() {
		return this.styledText.getLineAtOffset(this.styledText.getCaretOffset());
	}

	@Override
	public int getCharOffsetAtCurrentLine() {
		return getCharOffsetAtLine(getCurrentLineIndex());
	}

	@Override
	public int getCharOffsetAtLine(int lineIndex) {
		return styledText.getOffsetAtLine(lineIndex);
	}

	@Override
	public void addOrReplaceStyleRanges(int start, int length, StyleRange[] styleRanges) {
		styledText.replaceStyleRanges(start, length, styleRanges);
	}

	@Override
	public void setStyleRanges(StyleRange[] styleRanges) {
		styledText.setStyleRanges(styleRanges);
	}

	@Override
	public void redraw() {
		styledText.redraw();
	}

	/*
	 * private Bounds getWordBounds(String text, int caretOffset) { int startOffset
	 * = caretOffset; int endOffset = caretOffset; int start = 0; int end = 0;
	 * Bounds bounds = new Bounds(); char c;
	 * 
	 * boolean whiteSpace = false; while (startOffset > 0) {
	 * 
	 * if (startOffset < text.length() && (whiteSpace =
	 * Character.isWhitespace(text.charAt(startOffset)))) { if (whiteSpace) {
	 * startOffset += 1; } break; } startOffset--; } start = startOffset;
	 * 
	 * whiteSpace = false; while (endOffset < text.length()) { if (endOffset > 0 &&
	 * (whiteSpace = Character.isWhitespace(text.charAt(endOffset)))) { if
	 * (whiteSpace) { endOffset -= 1; } break; } endOffset++; } end = endOffset;
	 * 
	 * bounds.setStart(start); bounds.setEnd(end); return bounds; }
	 */
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO Set the focus to control
	}

	private void updateWorkspace(boolean addProject) {
		Initializer.getConfiguration().updateWorkspace(project, addProject, false);
	}
}
