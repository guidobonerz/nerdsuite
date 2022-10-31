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
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.Value;

public class HexViewWidget extends Composite implements DragDetectListener {

    private byte[] content = null;
    private StyledText addressArea = null;
    private StyledText hexArea = null;
    private StyledText textArea = null;
    private Button autoDiscover;
    private Button startDecode;
    private Button startAddress;
    private Button code;
    private Button binary;
    private Button undefined;

    private int visibleRows = 0;
    private IPlatform platform;
    private List<DisassemblingRange> rangeList;
    private DisassemblingRange selectedRange = null;

    private int contentOffset = 0;
    private boolean selectStart = false;
    private int cursorPos = 0;
    private RangeType selectedRangeType = RangeType.Code;
    private boolean wasShifted = false;
    private TableViewer tableViewer;
    private boolean addressChecked = false;
    private Value pc;
    private Stack<InstructionLine> jumpStack;

    public HexViewWidget(Composite parent, int style, IPlatform platform) {
        super(parent, style);
        this.platform = platform;
        rangeList = new ArrayList<DisassemblingRange>();
        jumpStack = new Stack<InstructionLine>();
        initialize();
        addDragDetectListener(this);
    }

    @Override
    public void dragDetected(DragDetectEvent e) {
        System.out.println("drad detected");

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

    public void setBinaryContent(byte[] binaryContent) {

        content = binaryContent;
        if (binaryContent == null) {
            content = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0x80, (byte) 100, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0 };
        }
        selectedRange = new DisassemblingRange(0, content.length, RangeType.Unspecified);
        rangeList = new ArrayList<DisassemblingRange>();
        rangeList.add(selectedRange);
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
                if (pc != null && pc.getValue() > 0) {
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

    private void disassemble(int start, int length, RangeType rangeType) {

        if (length > 0) {
            platform.getCPU().clear();
            handleDataRange(start, length, rangeType);
            hexArea.redraw();
            textArea.redraw();
            if (pc != null) {
                platform.setProgrammCounter(new Value(pc.getValue()));
                platform.parseBinary(content,
                        new DisassemblingRange(start + contentOffset, length, RangeType.Code));
                tableViewer.setInput(platform.getCPU().getInstructionLineList());
            }
        }
    }

    private void handleDataRange(int start, int length, RangeType rangeType) {
        List<DisassemblingRange> result = findRanges(start, length);
        for (DisassemblingRange range : result) {
            splitRange(range, rangeType, start, length);
        }
        DisassemblingRange lastRange = null;
        for (int i = 0; i < rangeList.size(); i++) {
            DisassemblingRange r = rangeList.get(i);
            if (lastRange != null && lastRange.getRangeType() == r.getRangeType()) {
                int l = lastRange.getLen() + r.getLen();
                lastRange.setLen(l);
                rangeList.remove(i);
            }
            lastRange = r;
        }
        if (rangeList.isEmpty()) {
            rangeList.add(new DisassemblingRange(0, content.length, RangeType.Unspecified));
        }
    }

    private List<DisassemblingRange> findRanges(int start, int length) {

        List<DisassemblingRange> resultList = rangeList.stream()
                .filter(r -> start <= r.getOffset() && start + length >= r.getOffset() + r.getLen() /* OVER ALL */)
                .collect(Collectors.toList());
        for (DisassemblingRange r : resultList) {
            rangeList.remove(r);
        }

        resultList = rangeList.stream()
                .filter(r -> start >= r.getOffset() && start + length <= r.getOffset() + r.getLen() /* IN */
                        || start > r.getOffset() && start < r.getOffset() + r.getLen()
                                && start + length >= r.getOffset() + r.getLen() /* OVERLAP START */
                ).collect(Collectors.toList());
        Collections.sort(resultList, new Comparator<DisassemblingRange>() {
            @Override
            public int compare(DisassemblingRange o1, DisassemblingRange o2) {
                return Integer.compare(o1.getOffset(), o2.getOffset());
            }
        });

        return resultList;
    }

    private void splitRange(DisassemblingRange r, RangeType rangeType, int start, int length) {
        int rangeIndex = rangeList.indexOf(r);
        int oldStart = r.getOffset();
        int oldEnd = r.getLen();
        if (start >= r.getOffset() && start + length <= r.getOffset() + r.getLen()
                && r.getRangeType() != rangeType) /* IN */ {
            r.setLen(start - oldStart);
            DisassemblingRange newRange1 = new DisassemblingRange(start, length, rangeType);
            DisassemblingRange newRange2 = new DisassemblingRange(start + length, oldStart + oldEnd - (start + length),
                    r.getRangeType());
            rangeList.add(rangeIndex + 1, newRange1);
            rangeList.add(rangeIndex + 2, newRange2);
        } else if (start > r.getOffset() && start < r.getOffset() + r.getLen()
                && start + length >= r.getOffset() + r.getLen()) /* OVERLAP START */ {
            r.setLen(start - oldStart);
            DisassemblingRange nextRange = rangeList.get(rangeIndex + 1);
            nextRange.setOffset(start + length);
            nextRange.setLen(nextRange.getOffset() + nextRange.getLen() - ((start + length)));
            rangeList.add(rangeIndex + 1, new DisassemblingRange(start, length, rangeType));
        }
    }

    private void shiftRange(int offset) {
        for (DisassemblingRange r : rangeList) {
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
            sbByte.append(String.format("%02x", content[b + contentOffset]));
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
        // gd.widthHint = 400;
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
                String s = "";
                if (userObject != null) {
                    s = String.format("%s", userObject[0]);
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
                    s = String.format("%s", userObject[1]);
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
                    s = String.format("%-13s %s %-15s %s", userObject[2], userObject[3], userObject[4],
                            userObject[5]);
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
        gd.grabExcessHorizontalSpace = false;
        // gd.minimumWidth = 450;
        gd.horizontalSpan = 3;

        /*
         * RowLayout layout = new RowLayout(); rowLayout.wrap = false; rowLayout.pack =
         * false; rowLayout.justify = true; rowLayout.marginLeft = 5;
         * rowLayout.marginTop = 5; rowLayout.marginRight = 5; rowLayout.marginBottom =
         * 5; rowLayout.spacing = 0;
         */
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
                prepareContent();
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

        startDecode = new Button(group, SWT.PUSH);
        startDecode.setFont(Constants.FontAwesome5ProSolid);
        startDecode.setText("\ue059");

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
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessVerticalSpace = true;
        gd.grabExcessHorizontalSpace = false;
        hexArea = new StyledText(this, SWT.NONE);
        hexArea.setFont(Constants.EDITOR_FONT);
        hexArea.setSelectionBackground(Constants.CODE_COLOR);
        hexArea.setContent(new HexViewContent(32));
        hexArea.addLineStyleListener(new LineStyleListener() {
            @Override
            public void lineGetStyle(LineStyleEvent event) {
                event.styles = getStyleRanges(2, event);
            }
        });

        hexArea.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                if (selectStart) {
                    int start = cursorPos;
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
                    hexArea.setSelectionRange(start, length);
                    textArea.setSelectionRange(start >> 1, length >> 1);
                    selectedRange.setOffset(start >> 1);
                    selectedRange.setLen(length >> 1);
                }
            }
        });
        hexArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (e.button == 1) {
                    selectStart = false;
                    disassemble(selectedRange.getOffset(), selectedRange.getLen(), selectedRangeType);
                }
            }

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    selectStart = true;
                    cursorPos = hexArea.getCaretOffset();
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
                event.styles = getStyleRanges(1, event);
            }

        });
        textArea.setLayoutData(gd);

        textArea.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                if (selectStart) {
                    int start = cursorPos;
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
        textArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (e.button == 1) {
                    selectStart = false;
                    disassemble(selectedRange.getOffset(), selectedRange.getLen(), selectedRangeType);
                }
            }

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    selectStart = true;
                    cursorPos = textArea.getCaretOffset();
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
            bgc = Constants.BINARY_COLOR;
            int lineLength = event.lineText.length();
            for (int x = event.lineOffset; x < event.lineOffset + lineLength; x += 2) {
                styleRange = new StyleRange(x, 2, fgc, x % 4 == 0 ? Constants.LIGHT_BLUE : Constants.WHITE);
                list.add(styleRange);
            }
        }
        for (DisassemblingRange range : rangeList) {
            switch (range.getRangeType()) {
                case Code:
                    bgc = Constants.CODE_COLOR;
                    styleRange = new StyleRange(range.getOffset() * width, range.getLen() *
                            width, fgc, bgc);
                    list.add(styleRange);
                    break;
                case Binary:
                    bgc = Constants.CODE_COLOR;
                    styleRange = new StyleRange(range.getOffset() * width, range.getLen() *
                            width, fgc, bgc);
                    list.add(styleRange);
                    break;
                default:
                    // bgc = Constants.WHITE;
                    // styleRange = new StyleRange(range.getOffset() * width, range.getLen() *
                    // width, fgc, bgc);
                    // list.add(styleRange);
                    break;
            }

        }
        return list.toArray(new StyleRange[list.size()]);
    }
}
