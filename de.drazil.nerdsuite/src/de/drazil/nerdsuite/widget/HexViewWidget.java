package de.drazil.nerdsuite.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.jface.resource.ImageRegistry;
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
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import de.drazil.nerdsuite.cpu.decode.HexViewStyledTextContent;
import de.drazil.nerdsuite.cpu.decode.InstructionLine;
import de.drazil.nerdsuite.cpu.platform.IPlatform;
import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.BinaryFileHandler;

public class HexViewWidget extends Composite implements IContentProvider {

	private byte[] content = null;
	private StyledText addressArea = null;
	private StyledText hexArea = null;
	private StyledText textArea = null;
	private Button autoDiscover;
	// private Button startDecode;
	private Button startAddress;
	private Button code;
	private Button binary;
	// private Button undefined;
	private int contentOffset = 0;

	private int visibleRows = 0;
	private IPlatform platform;
	private List<DisassemblingRange> rangeList;
	private DisassemblingRange selectedRange = null;

	private boolean enableSelect = false;
	private boolean dragStarted = false;
	private int selectStartOffset = 0;

	private TableViewer tableViewer;

	private Value pc;
	private Stack<InstructionLine> jumpStack;

	private static StyleRange[] hexStyleRangeList = new StyleRange[32];
	static {
		for (int i = 0; i < 32; i += 2) {
			hexStyleRangeList[i] = new StyleRange(0, 2, Constants.HEXVIEW_ODD_COLUMN_FG_COLOR,
					Constants.HEXVIEW_ODD_COLUMN_BG_COLOR);
			hexStyleRangeList[i + 1] = new StyleRange(0, 2, Constants.HEXVIEW_EVEN_COLUMN_FG_COLOR,
					Constants.HEXVIEW_EVEN_COLUMN_BG_COLOR);
		}
	}

	public HexViewWidget(Composite parent, int style, IPlatform platform) {
		super(parent, style);
		this.platform = platform;
		rangeList = new ArrayList<DisassemblingRange>();
		jumpStack = new Stack<InstructionLine>();
		selectedRange = new DisassemblingRange(0, 0, RangeType.Binary);
		initialize();
		
	}

	private static boolean isPrintableCharacter(char c) {
		return c >= 32 && c < 127;
	}

	public byte[] getContentArray() {
		return content;
	}

	public byte getContentAtOffset(int index) {
		return content[contentOffset + index];
	}

	public int getContentOffset() {
		return contentOffset;
	}

	public int getContentLength() {
		return content.length - contentOffset;
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

	public void clearAllRanges() {
		rangeList.clear();
		rangeList.add(new DisassemblingRange(0, content.length, RangeType.Binary));
		// disassemble();
		addressArea.redraw();
		hexArea.redraw();
		textArea.redraw();
	}

	public void setRangeTag() {

	}

	private void selectInstruction(InstructionLine line) {
		int index = platform.getCPU().getIndexOf(line);
		tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(index)), true);
		tableViewer.getTable().showSelection(); // setTopIndex(index);
	}

	public void setLabel(String name) {

	}

	public void setBinaryContent(byte[] binaryContent) {
		content = binaryContent;
		rangeList = new ArrayList<DisassemblingRange>();
		rangeList.add(new DisassemblingRange(0, content.length, RangeType.Binary));
		prepareContent();
		// disassemble(selectedRange);
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
				if (pc != null && pc.getValue() > 0) {
					MessageBox message = new MessageBox(getParent().getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
					message.setMessage(String.format("Found potential StartAddress 0x%04x\nApply it?", pc.getValue()));
					message.setText("StartAddress found");
					if (message.open() == SWT.YES) {
						startAddress.setSelection(true);
						platform.setProgrammCounter(new Value(pc.getValue()));
					}
				}
				prepareContent();
			}
		});
	}

	private void disassemble() {
		if (selectedRange.getLen() > 0) {
			platform.getCPU().clear();
			handleSelection(selectedRange.getOffset(), selectedRange.getLen(), selectedRange.getRangeType());
			Console.println("> ----------------------------------------------");
			Console.printf("> range count :%d\n", rangeList.size());
			for (int i = 0; i < rangeList.size(); i++) {
				int offset = pc.getValue() + rangeList.get(i).getOffset();
				Console.printf("> %04d : %04x - %04x %s\n", i, offset, offset + rangeList.get(i).getLen() - 1,
						rangeList.get(i).getRangeType().toString());
			}

			hexArea.redraw();
			textArea.redraw();

			if (pc != null) {
				platform.setProgrammCounter(new Value(pc.getValue()));
				// platform.parseBinary(this, rangeList);
				tableViewer.setInput(platform.getCPU().getInstructionLineList());
			}

		}
	}

	private void handleSelection(int start, int length, RangeType rangeType) {
		DisassemblingRange range = rangeList.stream()
				.filter(r -> start >= r.getOffset() && start + length <= r.getOffset() + r.getLen()).findFirst()
				.orElse(null);
		if (range != null) {
			if (rangeType != range.getRangeType()) {
				range.setDirty(true);
				if (start == range.getOffset() && length == range.getLen()) {
					range.setRangeType(rangeType);
					Console.println("> simply change rangetype.");
				} else {
					int oldStart = range.getOffset();
					int oldLength = range.getLen();
					int rangeIndex = rangeList.indexOf(range);
					int croppedLength = start - range.getOffset();
					int indexOffset = 1;
					if (croppedLength == 0) {
						indexOffset = 0;
						rangeList.remove(rangeIndex);
					}
					range.setLen(croppedLength);
					range.setDirty(true);
					rangeList.add(rangeIndex + (indexOffset++), new DisassemblingRange(start, length, rangeType));
					rangeList.add(rangeIndex + (indexOffset++), new DisassemblingRange(start + length,
							(oldStart + oldLength) - (start + length), range.getRangeType()));
				}
			} else {
				Console.println("> skip setting new range due to equality.");
			}
		} else {

			List<DisassemblingRange> ranges = rangeList.stream()
					.filter(r -> start <= r.getOffset() + r.getLen() && start + length > r.getOffset())
					.collect(Collectors.toList());

			while (ranges.size() > 2) {
				DisassemblingRange dr = ranges.get(1);
				ranges.remove(dr);
				rangeList.remove(dr);
			}

			DisassemblingRange topRange = ranges.get(0);
			DisassemblingRange bottomRange = ranges.get(ranges.size() - 1);
			topRange.setDirty(true);
			bottomRange.setDirty(true);
			if (topRange.getRangeType() == bottomRange.getRangeType() && topRange.getRangeType() == rangeType) {
				int newLength = (bottomRange.getOffset() + bottomRange.getLen()) - topRange.getOffset();
				rangeList.remove(bottomRange);
				topRange.setLen(newLength);
			} else if (topRange.getRangeType() == rangeType && topRange.getRangeType() != bottomRange.getRangeType()) {
				bottomRange.setLen((bottomRange.getOffset() + bottomRange.getLen()) - (start + length));
				bottomRange.setOffset(start + length);
				topRange.setLen((start + length) - topRange.getOffset());
			} else if (bottomRange.getRangeType() == rangeType
					&& topRange.getRangeType() != bottomRange.getRangeType()) {
				topRange.setLen(start - topRange.getOffset());
				bottomRange.setLen((bottomRange.getOffset() + bottomRange.getLen()) - start);
				bottomRange.setOffset(start);
			} else if (topRange.getRangeType() == bottomRange.getRangeType() && topRange.getRangeType() != rangeType) {
				topRange.setLen(start - topRange.getOffset());
				bottomRange.setLen((bottomRange.getOffset() + bottomRange.getLen()) - (start + length));
				bottomRange.setOffset(start + length);
				int insertIndex = rangeList.indexOf(topRange) + 1;
				rangeList.add(insertIndex, new DisassemblingRange(start, length, rangeType));
				if (topRange.getLen() == 0) {
					rangeList.remove(topRange);
				}
			} else {
				Console.println("> should not happen.");
			}
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
		} else {
			contentOffset = 0;
		}

		StringBuilder sbByte = null;
		StringBuilder sbText = null;
		StringBuilder sbAdress = null;
		sbByte = new StringBuilder();
		sbText = new StringBuilder();
		sbAdress = new StringBuilder();

		while (b < getContentLength()) {
			if (b % 16 == 0) {
				sbAdress.append(String.format("%04x:", memoryOffset + b));
			}
			byte c = getContentAtOffset(b);
			sbByte.append(String.format("%02x", c));
			sbText.append(isPrintableCharacter((char) c) ? (char) c : '_');
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
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(tableViewer.getTable()) });
		tableViewer.setColumnProperties(new String[] { "ADDRESS", "LABEL", "DISASSEMBLY" });
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
				String s = "";
				if (userObject != null) {
					s = String.format("%s", userObject[1]);
				}
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
				String s = "";
				if (userObject != null) {
					s = String.format("%s", userObject[2]);
				}
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
				String s = "";
				if (userObject != null) {
					if (userObject[0].equals(RangeType.Code.toString())) {
						s = String.format("%-13s %s %-15s %s", userObject[3], userObject[4], userObject[5],
								userObject[6]);
					} else if (userObject[0].equals(RangeType.Binary.toString())) {
						s = String.format("%s", userObject[3]);
					}
				}
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
						final Text text = new Text(tableViewer.getTable(), SWT.NONE);
						text.setFont(Constants.C64_Pro_Mono_FONT);

						Listener textListener = new Listener() {
							public void handleEvent(final Event e) {
								switch (e.type) {
								case SWT.FocusOut:
									item.setText(1, text.getText());
									text.dispose();
									break;
								case SWT.Traverse:
									switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										item.setText(1, text.getText());
										((InstructionLine) item.getData()).setLabelName(text.getText());
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

		Transfer[] types = new Transfer[] { FileTransfer.getInstance() };

		tableViewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, types, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				String fileName = ((String[]) event.data)[0].toString();
				byte[] content = null;
				try {
					content = BinaryFileHandler.readFile(new File(fileName), 0);
					setBinaryContent(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = 3;

		GridLayout layout2 = new GridLayout(4, false);

		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(layout2);

		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.BEGINNING;
		gd2.horizontalSpan = 1;
		gd2.grabExcessHorizontalSpace = false;
		autoDiscover = new Button(c, SWT.CHECK);
		autoDiscover.setText("AutoDiscover Mode");
		autoDiscover.setLayoutData(gd2);

		gd2 = new GridData();
		gd2.horizontalAlignment = GridData.BEGINNING;
		gd2.horizontalSpan = 1;
		gd2.grabExcessHorizontalSpace = false;
		startAddress = new Button(c, SWT.CHECK);
		startAddress.setText("First two bytes represent StartAddress");
		startAddress.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// prepareContent();
			}
		});

		gd2 = new GridData();
		gd2.horizontalAlignment = GridData.BEGINNING;
		gd2.horizontalSpan = 4;
		Group group = new Group(c, SWT.NONE);
		group.setLayout(new RowLayout(SWT.HORIZONTAL));
		group.setLayoutData(gd2);

		code = new Button(group, SWT.RADIO);
		code.setBackground(Constants.CODE_COLOR);
		code.setForeground(Constants.WHITE);
		code.setText("Code");
		code.setSelection(true);
		code.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRange.setRangeType(RangeType.Code);
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
				selectedRange.setRangeType(RangeType.Binary);
				hexArea.setSelectionForeground(Constants.WHITE);
				textArea.setSelectionForeground(Constants.WHITE);
				hexArea.setSelectionBackground(Constants.BINARY_COLOR);
				textArea.setSelectionBackground(Constants.BINARY_COLOR);
			}
		});
		/*
		 * undefined = new Button(group, SWT.RADIO); undefined.setText("Undefined");
		 * undefined.setBackground(Constants.WHITE); undefined.addSelectionListener(new
		 * SelectionAdapter() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent e) {
		 * selectedRange.setRangeType(RangeType.Unspecified);
		 * hexArea.setSelectionForeground(Constants.BLACK);
		 * textArea.setSelectionForeground(Constants.BLACK);
		 * hexArea.setSelectionBackground(Constants.WHITE);
		 * textArea.setSelectionBackground(Constants.WHITE); } });
		 */
		/*
		 * startDecode = new Button(group, SWT.PUSH);
		 * startDecode.setFont(Constants.FontAwesome5ProSolid);
		 * startDecode.setText("\ue059");
		 */
		selectedRange.setRangeType(code.getSelection() ? RangeType.Code
				: binary.getSelection() ? RangeType.Binary : RangeType.Unspecified);

		c.setLayoutData(gd);

		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.widthHint = 40;
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = false;
		addressArea = new StyledText(this, SWT.READ_ONLY);
		addressArea.setEditable(false);
		addressArea.setEnabled(false);
		addressArea.setContent(new HexViewStyledTextContent(5));
		addressArea.setFont(Constants.EDITOR_FONT);
		addressArea.setLayoutData(gd);

		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalAlignment = GridData.FILL;
		gd.widthHint = (int) (6.5f * 32);
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = false;
		hexArea = new StyledText(this, SWT.NONE);
		hexArea.setFont(Constants.EDITOR_FONT);
		hexArea.invokeAction(ST.TOGGLE_OVERWRITE);
		hexArea.setSelectionBackground(Constants.CODE_COLOR);
		hexArea.setContent(new HexViewStyledTextContent(32));
		hexArea.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				int h = getBounds().height;
				GC gc = e.gc;
				gc.setAlpha(50);
				gc.setBackground(Constants.LIGHT_RED);
				for (int i = 13; i < 208; i += 26) {
					gc.fillRectangle(i, 0, 13, h);
				}

			}
		});
		hexArea.addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				event.styles = getStyleRanges(2, event);
			}
		});
		hexArea.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					enableSelect = true;
					dragStarted = false;
					selectStartOffset = hexArea.getCaretOffset();
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					if (enableSelect && dragStarted) {
						disassemble();
					}
					enableSelect = false;
					dragStarted = false;
				}
				textArea.setSelection(0, 0);
				hexArea.setSelection(0, 0);
			}
		});

		hexArea.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if (enableSelect) {
					dragStarted = true;
					int start = selectStartOffset;
					int end = hexArea.getCaretOffset();

					if (start > end) {
						int x = end;
						end = start;
						start = x;
					}

					if (start % 2 != 0) {
						start--;
					}

					if (end % 2 != 0) {
						end++;
					}
					int length = end - start;
					int sstart = start >> 1;
					int slength = length >> 1;
					hexArea.setSelectionRange(start, length);
					textArea.setSelectionRange(sstart, slength);
					selectedRange.setOffset(sstart);
					selectedRange.setLen(slength);
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
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalAlignment = GridData.FILL;
		gd.widthHint = (int) (6.5f * 16);
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = false;
		textArea = new StyledText(this, SWT.NONE);
		textArea.setFont(Constants.EDITOR_FONT);
		textArea.setSelectionBackground(Constants.CODE_COLOR);
		textArea.setContent(new HexViewStyledTextContent(16));
		textArea.addLineStyleListener(new LineStyleListener() {

			@Override
			public void lineGetStyle(LineStyleEvent event) {
				event.styles = getStyleRanges(1, event);
			}

		});
		textArea.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				/*
				 * int h = getParent().getBounds().height; GC gc = e.gc; gc.setAlpha(50);
				 * gc.setBackground(Constants.LIGHT_RED); for (int i = 13; i < 208; i += 26) {
				 * gc.fillRectangle(i, 0, 13, h); // gc.drawLine(i, 0, i, h); }
				 */
			}
		});
		textArea.setLayoutData(gd);
		textArea.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					enableSelect = true;
					dragStarted = false;
					selectStartOffset = textArea.getCaretOffset();
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					if (enableSelect && dragStarted) {
						disassemble();
					}
					enableSelect = false;
					dragStarted = false;
					textArea.setSelection(0, 0);
					hexArea.setSelection(0, 0);
				}
			}
		});

		textArea.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if (enableSelect) {
					dragStarted = true;
					int start = selectStartOffset;
					int end = textArea.getCaretOffset();

					if (start > end) {
						int x = end;
						end = start;
						start = x;
					}
					int length = end - start;
					hexArea.setSelectionRange(start << 1, length << 1);
					textArea.setSelectionRange(start, length);
					selectedRange.setOffset(start);
					selectedRange.setLen(length);
				}
			}
		});

		textArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addressArea.setTopIndex(textArea.getTopIndex());
				textArea.setTopIndex(textArea.getTopIndex());
				getVerticalBar().setSelection(textArea.getTopIndex());
			}
		});

	}

	private StyleRange[] getStyleRanges(int width, LineStyleEvent event) {
		List<StyleRange> list = new ArrayList<StyleRange>();
		StyleRange styleRange = null;
		Color fgc = Constants.BLACK;
		Color bgc = null;

		if (width == 2) {
			int lineLength = event.lineText.length();
			for (int x = event.lineOffset; x < event.lineOffset + lineLength; x += 2) {
				StyleRange sr = hexStyleRangeList[(x & 0b11111) >> 1];
				sr.start = x;
				list.add(sr);
			}
		}

		if (width == 2) {
			for (DisassemblingRange range : rangeList) {
				switch (range.getRangeType()) {
				case Code:
					bgc = Constants.CODE_COLOR;
					break;
				case Binary:
					bgc = Constants.WHITE;
					break;
				default:
					bgc = Constants.WHITE;
					break;

				}
				styleRange = new StyleRange(range.getOffset() * width, range.getLen() * width, fgc, bgc);
				list.add(styleRange);

			}
		}
		return list.toArray(new StyleRange[list.size()]);
	}
}
