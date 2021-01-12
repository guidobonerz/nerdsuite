package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.disassembler.HexViewContent;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.disassembler.platform.C64Platform;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;

public class HexViewWidget extends Composite {

	private byte[] content = null;
	private StyledText adressArea = null;
	private StyledText hexArea = null;
	private StyledText textArea = null;
	private Button startAddress;
	private Button code;
	private Button data;
	private Button undefined;
	private int visibleRows = 0;
	private IPlatform platform;
	private List<Range> rangeList;
	private int selStart;
	private int selLength;
	private int contentOffset = 0;
	private boolean selectStart = false;
	private RangeType selectedRangeType = RangeType.Code;
	private boolean wasShifted = false;

	public HexViewWidget(Composite parent, int style) {
		super(parent, style);
		platform = new C64Platform(new KickAssemblerDialect(), false);
		rangeList = new ArrayList<Range>();
		initialize();
	}

	private static boolean isPrintableCharacter(char c) {
		return c >= 32 && c < 127;
	}

	public void setContent(byte[] content) {
		this.content = content;
		rangeList = new ArrayList<Range>();
		rangeList.add(new Range(0, content.length, RangeType.Unspecified));
		prepareContent();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				GC fontGC = new GC(Display.getCurrent());
				int fontHeight = 0;
				try {

					fontGC.setFont(Constants.EDITOR_FONT);
					FontMetrics fm = fontGC.getFontMetrics();
					fontHeight = fm.getHeight();
				} finally {
					fontGC.dispose();
				}
				visibleRows = getClientArea().height / fontHeight;
				getVerticalBar().setMinimum(0);
				getVerticalBar().setMaximum(hexArea.getLineCount());
				getVerticalBar().setSelection(0);
				getVerticalBar().setThumb(visibleRows);
				getVerticalBar().setPageIncrement(visibleRows);
				adressArea.redraw();
				hexArea.redraw();
				textArea.redraw();
			}
		});
	}

	private void handleDataRange(int start, int length, RangeType rangeType) {
		List<Range> result = findRanges(start, length);
		for (Range range : result) {
			splitRange(range, rangeType, start, length);
		}
		Range lastRange = null;
		for (int i = 0; i < rangeList.size(); i++) {
			Range r = rangeList.get(i);
			if (lastRange != null && lastRange.getRangeType() == r.getRangeType()) {
				int l = lastRange.getLen() + r.getLen();
				lastRange.setLen(l);
				rangeList.remove(i);
			}
			lastRange = r;
		}
		if (rangeList.isEmpty()) {
			rangeList.add(new Range(0, content.length, RangeType.Unspecified));
		}
	}

	private List<Range> findRanges(int start, int length) {

		List<Range> resultList = rangeList.stream()
				.filter(r -> start <= r.getOffset() && start + length >= r.getOffset() + r.getLen() /* OVER ALL */)
				.collect(Collectors.toList());
		for (Range r : resultList) {
			rangeList.remove(r);
		}

		resultList = rangeList.stream()
				.filter(r -> start >= r.getOffset() && start + length <= r.getOffset() + r.getLen() /* IN */
						|| start > r.getOffset() && start < r.getOffset() + r.getLen()
								&& start + length >= r.getOffset() + r.getLen() /* OVERLAP START */
				).collect(Collectors.toList());
		Collections.sort(resultList, new Comparator<Range>() {
			@Override
			public int compare(Range o1, Range o2) {
				return Integer.compare(o1.getOffset(), o2.getOffset());
			}
		});

		return resultList;
	}

	private void splitRange(Range r, RangeType rangeType, int start, int length) {
		int rangeIndex = rangeList.indexOf(r);
		int oldStart = r.getOffset();
		int oldEnd = r.getLen();
		if (start >= r.getOffset() && start + length <= r.getOffset() + r.getLen()
				&& r.getRangeType() != rangeType) /* IN */ {
			r.setLen(start - oldStart);
			Range newRange1 = new Range(start, length, rangeType);
			Range newRange2 = new Range(start + length, oldStart + oldEnd - (start + length), r.getRangeType());
			rangeList.add(rangeIndex + 1, newRange1);
			rangeList.add(rangeIndex + 2, newRange2);
		} else if (start > r.getOffset() && start < r.getOffset() + r.getLen()
				&& start + length >= r.getOffset() + r.getLen()) /* OVERLAP START */ {
			r.setLen(start - oldStart);
			Range nextRange = rangeList.get(rangeIndex + 1);
			nextRange.setOffset(start + length);
			nextRange.setLen(nextRange.getOffset() + nextRange.getLen() - ((start + length)));
			rangeList.add(rangeIndex + 1, new Range(start, length, rangeType));
		}
	}

	private void shiftRange(int offset) {
		for (Range r : rangeList) {
			int start = r.getOffset() + offset;
			r.setOffset(start);
		}
	}

	private void prepareContent() {
		int b = 0;
		int memoryOffset = 0;
		platform.setIgnoreStartAddressBytes(true);
		if (startAddress.getSelection()) {
			platform.setIgnoreStartAddressBytes(false);
			contentOffset = 2;
			memoryOffset = platform.getCPU().getWord(content, 0);
			wasShifted = true;
			shiftRange(-2);
		} else {
			shiftRange(wasShifted ? 2 : 0);
			wasShifted = false;
			contentOffset = 0;
		}

		StringBuilder sbByte = null;
		StringBuilder sbText = null;
		StringBuilder sbAdress = null;
		sbByte = new StringBuilder();
		sbText = new StringBuilder();
		sbAdress = new StringBuilder();

		while (b + contentOffset < content.length) {
			if (b % 16 == 0) {
				sbAdress.append(String.format("%04x:", memoryOffset + b));
			}
			sbByte.append(String.format("%02x ", content[b + contentOffset]));
			sbText.append(
					isPrintableCharacter((char) content[b + contentOffset]) ? (char) content[b + contentOffset] : '_');
			b++;
		}

		hexArea.getContent().setText(sbByte.toString());
		textArea.getContent().setText(sbText.toString());
		adressArea.getContent().setText(sbAdress.toString());
	}

	private void updateArea(int offset) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				adressArea.setTopIndex(offset);
				hexArea.setTopIndex(offset);
				textArea.setTopIndex(offset);
			}
		});
	}

	private void initialize() {
		getVerticalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.doit = false;
				int offset = getVerticalBar().getSelection();
				updateArea(offset);
			}
		});

		GridLayout layout = new GridLayout(4, false);
		setLayout(layout);
		GridData gd = null;
		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		gd.verticalSpan = 2;
		TableViewer tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);

		TableViewerColumn tableViewerColumn1 = new TableViewerColumn(tableViewer, SWT.NONE);
		ColumnLabelProvider labelProvider1 = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				InstructionLine il = (InstructionLine) element;
				String[] userObject = (String[]) il.getUserObject();
				return String.format("%04x> %-10s %s", il.getProgramCounter().getValue(), userObject[0], userObject[1]);
			}

			@Override
			public Font getFont(Object element) {
				return Constants.EDITOR_FONT;
			}
		};
		tableViewerColumn1.setLabelProvider(labelProvider1);
		TableColumn codeLine = tableViewerColumn1.getColumn();
		codeLine.setWidth(300);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.getTable().setLayoutData(gd);

		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 3;

		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = true;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 0;

		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(rowLayout);

		Group group = new Group(c, SWT.NONE);
		group.setLayout(new RowLayout(SWT.HORIZONTAL));

		code = new Button(group, SWT.RADIO);
		code.setBackground(Constants.CODE_COLOR);
		code.setForeground(Constants.WHITE);
		code.setText("Code");
		code.setSelection(true);
		code.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRangeType = RangeType.Code;
				hexArea.setSelectionForeground(Constants.WHITE);
				textArea.setSelectionForeground(Constants.WHITE);
				hexArea.setSelectionBackground(Constants.CODE_COLOR);
				textArea.setSelectionBackground(Constants.CODE_COLOR);
			}
		});

		data = new Button(group, SWT.RADIO);
		data.setText("Data");
		data.setBackground(Constants.DATA_COLOR);
		data.setForeground(Constants.WHITE);
		data.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRangeType = RangeType.Data;
				hexArea.setSelectionForeground(Constants.WHITE);
				textArea.setSelectionForeground(Constants.WHITE);
				hexArea.setSelectionBackground(Constants.DATA_COLOR);
				textArea.setSelectionBackground(Constants.DATA_COLOR);
			}
		});

		undefined = new Button(group, SWT.RADIO);
		undefined.setText("Undefined");
		undefined.setBackground(Constants.WHITE);
		undefined.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRangeType = RangeType.Unspecified;
				hexArea.setSelectionForeground(Constants.BLACK);
				textArea.setSelectionForeground(Constants.BLACK);
				hexArea.setSelectionBackground(Constants.WHITE);
				textArea.setSelectionBackground(Constants.WHITE);
			}
		});

		startAddress = new Button(c, SWT.CHECK);
		startAddress.setText("First two bytes represent StartAddress");
		startAddress.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				prepareContent();
				hexArea.redraw();
				textArea.redraw();
			}
		});

		c.setLayoutData(gd);

		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		adressArea = new StyledText(this, SWT.READ_ONLY);
		adressArea.setEditable(false);
		adressArea.setEnabled(false);
		adressArea.setContent(new HexViewContent(5));
		adressArea.setFont(Constants.EDITOR_FONT);
		adressArea.setLayoutData(gd);

		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		hexArea = new StyledText(this, SWT.NONE);
		hexArea.setFont(Constants.EDITOR_FONT);
		hexArea.setSelectionBackground(Constants.CODE_COLOR);
		hexArea.setContent(new HexViewContent(48));
		hexArea.addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				event.styles = getStyleRanges(3);
			}
		});
		hexArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				selectStart = false;
				selStart = hexArea.getSelectionRange().x / 3;
				selLength = hexArea.getSelectionRange().y / 3;
				textArea.setSelectionRange(selStart, selLength);
				if (selLength > 0) {
					handleDataRange(selStart, selLength, selectedRangeType);
					hexArea.redraw();
					textArea.redraw();
					platform.parseBinary(content, new Range(selStart, selLength, RangeType.Code));
					tableViewer.setInput(platform.getCPU().getInstructionLineList());
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				selectStart = true;
			}
		});
		hexArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				adressArea.setTopIndex(hexArea.getTopIndex());
				textArea.setTopIndex(hexArea.getTopIndex());
				getVerticalBar().setSelection(hexArea.getTopIndex());
			}
		});

		hexArea.setLayoutData(gd);

		// ==================

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		textArea = new StyledText(this, SWT.NONE);
		textArea.setFont(Constants.EDITOR_FONT);
		textArea.setSelectionBackground(Constants.CODE_COLOR);
		textArea.setContent(new HexViewContent(16));
		textArea.addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				event.styles = getStyleRanges(1);
			}
		});
		textArea.setLayoutData(gd);

		textArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				adressArea.setTopIndex(textArea.getTopIndex());
				hexArea.setTopIndex(textArea.getTopIndex());
				getVerticalBar().setSelection(textArea.getTopIndex());
				hexArea.setSelectionRange(textArea.getSelectionRange().x * 3, textArea.getSelectionRange().y * 3);
			}
		});

	}

	private StyleRange[] getStyleRanges(int width) {
		List<StyleRange> list = new ArrayList<StyleRange>();
		for (Range range : rangeList) {

			Color fgc = Constants.BLACK;
			Color bgc = null;
			switch (range.getRangeType()) {
			case Code:
				bgc = Constants.CODE_COLOR;
				break;
			case Data:
				bgc = Constants.DATA_COLOR;
				break;
			default:
				bgc = Constants.WHITE;
				break;
			}

			StyleRange styleRange = new StyleRange(range.getOffset() * width, range.getLen() * width, fgc, bgc);
			list.add(styleRange);
		}
		return list.toArray(new StyleRange[list.size()]);
	}
}
