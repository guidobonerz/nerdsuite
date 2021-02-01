package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.disassembler.HexViewContent;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.Value;

public class HexViewWidget extends Composite {

	private byte[] content = null;
	private StyledText addressArea = null;
	private StyledText hexArea = null;
	private StyledText textArea = null;
	private Button autoDiscover;
	private Button startAnalyse;
	private Button startAddress;
	private Button code;
	private Button binary;
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
	private TableViewer tableViewer;
	private boolean addressChecked = false;
	private Value pc;
	private Stack<InstructionLine> jumpStack;

	public HexViewWidget(Composite parent, int style, IPlatform platform) {
		super(parent, style);
		this.platform = platform;
		rangeList = new ArrayList<Range>();
		jumpStack = new Stack<InstructionLine>();
		initialize();
	}

	private static boolean isPrintableCharacter(char c) {
		return c >= 32 && c < 127;
	}

	public Composite getDisassemblyView() {
		return tableViewer.getTable();
	}

	public Composite getBinaryView() {
		return hexArea;
	}

	public void jumpToAddress() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		InstructionLine line = (InstructionLine) selection.getFirstElement();
		InstructionLine refLine = platform.getCPU().findInstructionLineByProgrammCounter(line.getReferenceValue());
		if (refLine == null) {
			MessageBox message = new MessageBox(getParent().getShell(), SWT.OK | SWT.ICON_WARNING);
			message.setMessage(String.format("0x%04x is currently unreachable!", line.getReferenceValue().getValue()));
			message.setText("Target unreachable");
			message.open();
		} else {
			selectInstruction(refLine);
			jumpStack.push(line);
		}
	}

	public void returnToOrigin() {
		if (!jumpStack.isEmpty()) {
			selectInstruction(jumpStack.pop());
		} else {
			MessageBox message = new MessageBox(getParent().getShell(), SWT.OK | SWT.ICON_INFORMATION);
			message.setMessage("Base origin already reached!");
			message.setText("Base origin reached");
			message.open();
		}
	}

	private void selectInstruction(InstructionLine line) {
		int index = platform.getCPU().getIndexOf(line);
		tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(index)), true);
		tableViewer.getTable().showSelection(); // setTopIndex(index);
	}

	public void setLabel(String name) {

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
				addressArea.redraw();
				hexArea.redraw();
				textArea.redraw();

				pc = platform.checkAdress(content, 0);
				if (pc.getValue() > 0) {
					MessageBox message = new MessageBox(getParent().getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
					message.setMessage(String.format("Found potential StartAddress 0x%04x\nApply it?", pc.getValue()));
					message.setText("StartAddress found");
					if (message.open() == SWT.YES) {
						startAddress.setSelection(true);
						platform.setProgrammCounter(new Value(pc.getValue()));
						prepareContent();
					}
				}
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
		addressArea.getContent().setText(sbAdress.toString());
		hexArea.redraw();
		textArea.redraw();
	}

	private void updateArea(int offset) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				addressArea.setTopIndex(offset);
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
		gd.grabExcessHorizontalSpace = true;
		gd.verticalSpan = 2;
		gd.widthHint = 400;
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(tableViewer.getTable()) });
		tableViewer.setColumnProperties(new String[] { "ADDRESS", "LABEL", "DISASM" });
		/*
		 * tableViewer.setCellModifier(new ICellModifier() {
		 * 
		 * @Override public void modify(Object element, String property, Object value) {
		 * if (element instanceof Item) { element = ((Item) element).getData(); }
		 * ((InstructionLine) element).setLabelName((String) value);
		 * tableViewer.refresh(); }
		 * 
		 * @Override public Object getValue(Object element, String property) { return
		 * ((InstructionLine) element).getLabelName(); }
		 * 
		 * @Override public boolean canModify(Object element, String property) { return
		 * "LABEL".equals(property); } });
		 */
		TableViewerColumn tableViewerColumnAddress = new TableViewerColumn(tableViewer, SWT.NONE);
		TableViewerColumn tableViewerColumnLabel = new TableViewerColumn(tableViewer, SWT.NONE);
		TableViewerColumn tableViewerColumnCode = new TableViewerColumn(tableViewer, SWT.NONE);
		ColumnLabelProvider labelProviderAddress = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				InstructionLine il = (InstructionLine) element;
				Object[] userObject = (Object[]) il.getUserObject();
				String s = String.format("%s", userObject[0]);
				return s;
			}

			@Override
			public Font getFont(Object element) {
				return Constants.EDITOR_FONT;
			}
		};
		ColumnLabelProvider labelProviderLabel = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				InstructionLine il = (InstructionLine) element;
				Object[] userObject = (Object[]) il.getUserObject();
				String s = String.format("%s", userObject[1]);
				return s;
			}

			@Override
			public Font getFont(Object element) {
				return Constants.EDITOR_FONT;
			}
		};
		ColumnLabelProvider labelProviderCode = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				InstructionLine il = (InstructionLine) element;
				Object[] userObject = (Object[]) il.getUserObject();
				String s = String.format("%-13s %s %-15s %s", userObject[2], userObject[3], userObject[4],
						userObject[5]);
				return s;
			}

			@Override
			public Font getFont(Object element) {
				return Constants.EDITOR_FONT;
			}
		};
		tableViewerColumnAddress.setLabelProvider(labelProviderAddress);
		tableViewerColumnLabel.setLabelProvider(labelProviderLabel);
		tableViewerColumnCode.setLabelProvider(labelProviderCode);
		TableColumn addressLine = tableViewerColumnAddress.getColumn();
		addressLine.setWidth(50);
		TableColumn labelLine = tableViewerColumnLabel.getColumn();
		labelLine.setWidth(200);
		TableColumn codeLine = tableViewerColumnCode.getColumn();
		codeLine.setWidth(500);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.getTable().setLayoutData(gd);

		final TableEditor editor = new TableEditor(tableViewer.getTable());
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		tableViewer.getTable().addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = tableViewer.getTable().getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = tableViewer.getTable().getTopIndex();
				while (index < tableViewer.getTable().getItemCount()) {
					boolean visible = false;
					final TableItem item = tableViewer.getTable().getItem(index);
					Rectangle rect = item.getBounds(1);
					if (rect.contains(pt)) {
						final int column = 1;
						final Text text = new Text(tableViewer.getTable(), SWT.NONE);
						Listener textListener = new Listener() {
							public void handleEvent(final Event e) {
								switch (e.type) {
								case SWT.FocusOut:
									item.setText(column, text.getText());
									text.dispose();
									break;
								case SWT.Traverse:
									switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										item.setText(column, text.getText());
										// FALL THROUGH
									case SWT.TRAVERSE_ESCAPE:
										text.dispose();
										e.doit = false;
									}
									break;
								}
							}
						};
						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);
						editor.setEditor(text, item, 1);
						text.setText(item.getText(1));
						text.selectAll();
						text.setFocus();
						return;
					}
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
					if (!visible)
						return;
					index++;
				}
			}
		});

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

		binary = new Button(group, SWT.RADIO);
		binary.setText("Binary");
		binary.setBackground(Constants.BINARY_COLOR);
		binary.setForeground(Constants.WHITE);
		binary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRangeType = RangeType.Binary;
				hexArea.setSelectionForeground(Constants.WHITE);
				textArea.setSelectionForeground(Constants.WHITE);
				hexArea.setSelectionBackground(Constants.BINARY_COLOR);
				textArea.setSelectionBackground(Constants.BINARY_COLOR);
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
			}
		});

		c.setLayoutData(gd);

		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		addressArea = new StyledText(this, SWT.READ_ONLY);
		addressArea.setEditable(false);
		addressArea.setEnabled(false);
		addressArea.setContent(new HexViewContent(5));
		addressArea.setFont(Constants.EDITOR_FONT);
		addressArea.setLayoutData(gd);

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
				if (e.button == 1) {
					selectStart = false;
					selStart = hexArea.getSelectionRange().x / 3;
					selLength = hexArea.getSelectionRange().y / 3;
					textArea.setSelectionRange(selStart, selLength);
					if (selLength > 0) {
						platform.getCPU().clear();
						handleDataRange(selStart, selLength, selectedRangeType);
						hexArea.redraw();
						textArea.redraw();
						platform.setProgrammCounter(new Value(pc.getValue()));
						platform.parseBinary(content, new Range(selStart + contentOffset, selLength, RangeType.Code));
						tableViewer.setInput(platform.getCPU().getInstructionLineList());
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					selectStart = true;
				}
			}
		});
		hexArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addressArea.setTopIndex(hexArea.getTopIndex());
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
				addressArea.setTopIndex(textArea.getTopIndex());
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
			case Binary:
				bgc = Constants.BINARY_COLOR;
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
