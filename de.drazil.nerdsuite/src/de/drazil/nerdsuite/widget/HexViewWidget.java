package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.disassembler.HexViewContent;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.Value;

public class HexViewWidget extends Composite {
	private List<InstructionLine> list;
	private byte[] content = null;
	private StyledText adressArea = null;
	private StyledText hexArea = null;
	private StyledText textArea = null;
	private int totalRows = 0;
	private int visibleRows = 0;

	public HexViewWidget(Composite parent, int style) {
		super(parent, style);
		list = new ArrayList<InstructionLine>();
		InstructionLine l1 = new InstructionLine();
		InstructionLine l2 = new InstructionLine();
		InstructionLine l3 = new InstructionLine();
		InstructionLine l4 = new InstructionLine();

		list.add(l1);
		list.add(l2);
		list.add(l3);
		list.add(l4);

		l1.setProgramCounter(new Value(1));
		l2.setProgramCounter(new Value(2));
		l3.setProgramCounter(new Value(3));
		l4.setProgramCounter(new Value(4));
		l1.setUserObject(new String[] { "loop1", "lda #$01" });
		l2.setUserObject(new String[] { "", "inc $d020" });
		l3.setUserObject(new String[] { "", "jmp loop1" });
		l4.setUserObject(new String[] { "", "rts" });
		initialize();
	}

	private static boolean isPrintableCharacter(char c) {
		return c >= 32 && c < 127;
	}

	public void setContent(byte[] content) {
		this.content = content;
		int i = 0;

		StringBuilder sbByte = null;
		StringBuilder sbText = null;
		StringBuilder sbAdress = null;
		sbByte = new StringBuilder();
		sbText = new StringBuilder();
		sbAdress = new StringBuilder();
		while (i < content.length) {
			if (i % 16 == 0) {
				totalRows++;
				sbAdress.append(String.format("%04x:", i));
				sbAdress.append("\n");
			}
			sbByte.append(String.format("%02x ", content[i]));
			// sbText.append(Character.toChars(0xe000 | ((int) content[i]) & 0xff));
			sbText.append(isPrintableCharacter((char) content[i]) ? (char) content[i] : '_');
			i++;
		}

		hexArea.getContent().setText(sbByte.toString());
		textArea.getContent().setText(sbText.toString());
		adressArea.getContent().setText(sbAdress.toString());

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				GC fontGC = new GC(Display.getCurrent());
				int fontHeight = 0;
				try {

					fontGC.setFont(Constants.C64_Pro_Mono_FONT);
					FontMetrics fm = fontGC.getFontMetrics();
					fontHeight = fm.getHeight();
				} finally {
					fontGC.dispose();
				}
				visibleRows = textArea.getClientArea().height / fontHeight;
				getVerticalBar().setMinimum(0);
				getVerticalBar().setMaximum(totalRows);
				getVerticalBar().setSelection(0);
				getVerticalBar().setThumb(visibleRows);
				getVerticalBar().setPageIncrement(visibleRows);
			}
		});
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
		tableViewer.getTable().setLayoutData(gd);
		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		adressArea = new StyledText(this, SWT.READ_ONLY);
		adressArea.setEditable(false);
		adressArea.setEnabled(false);
		adressArea.setFont(Constants.EDITOR_FONT);
		adressArea.setLayoutData(gd);

		// ==================
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = false;
		hexArea = new StyledText(this, SWT.NONE);
		hexArea.setFont(Constants.EDITOR_FONT);
		hexArea.setContent(new HexViewContent(16, 3));
		hexArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				adressArea.setTopIndex(hexArea.getTopIndex());
				textArea.setTopIndex(hexArea.getTopIndex());
				getVerticalBar().setSelection(hexArea.getTopIndex());
				textArea.setSelectionRange(hexArea.getSelectionRange().x / 3, hexArea.getSelectionRange().y / 3);
			}
		});

		hexArea.setLayoutData(gd);

		// ==================

		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		textArea = new StyledText(this, SWT.NONE);
		textArea.setFont(Constants.EDITOR_FONT);
		textArea.setContent(new HexViewContent(16, 1));
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
		/*
		 * TableEditor editor = new TableEditor(tableViewer.getTable());
		 * 
		 * Table table = tableViewer.getTable(); table.setFont(Constants.EDITOR_FONT);
		 * editor.horizontalAlignment = SWT.LEFT; editor.grabHorizontal = true;
		 */

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
		/*
		 * TableViewerColumn tableViewerColumn2 = new TableViewerColumn(tableViewer,
		 * SWT.NONE); ColumnLabelProvider labelProvider2 = new ColumnLabelProvider() {
		 * 
		 * @Override public String getText(Object element) {
		 * 
		 * return String.format("%04x %02x %02x %02x %02x %02x %02x %02x %02x %s", 0, 1,
		 * 2, 3, 4, 5, 6, 7, 8, "abcdefgh"); }
		 * 
		 * @Override public Font getFont(Object element) { FontData fd[] =
		 * Constants.EDITOR_FONT.getFontData(); fd[0].setHeight(12); Font f = new
		 * Font(Display.getDefault(), fd[0]); return f; } };
		 * 
		 * tableViewerColumn2.setLabelProvider(labelProvider2); TableColumn dataLine =
		 * tableViewerColumn2.getColumn(); dataLine.setWidth(300);
		 */

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(list);
		/*
		 * table.addListener(SWT.MouseDown, new Listener() { public void
		 * handleEvent(Event event) { Rectangle clientArea = table.getClientArea();
		 * Point pt = new Point(event.x, event.y); int index = table.getTopIndex();
		 * while (index < table.getItemCount()) { boolean visible = false; final
		 * TableItem item = table.getItem(index); for (int i = 0; i <
		 * table.getColumnCount(); i++) { Rectangle rect = item.getBounds(i); if
		 * (rect.contains(pt)) { final int column = i; Text text = new Text(table,
		 * SWT.NONE);
		 * 
		 * Listener textListener = new Listener() { public void handleEvent(final Event
		 * e) { switch (e.type) { case SWT.FocusOut: item.setText(column,
		 * text.getText()); text.dispose(); break; case SWT.Traverse: switch (e.detail)
		 * { case SWT.TRAVERSE_RETURN: item.setText(column, text.getText()); // FALL
		 * THROUGH case SWT.TRAVERSE_ESCAPE: text.dispose(); e.doit = false; } break; }
		 * } }; text.addListener(SWT.FocusOut, textListener);
		 * text.addListener(SWT.Traverse, textListener); editor.setEditor(text, item,
		 * i);
		 * 
		 * text.setText(item.getText(i)); // text.selectAll(); text.setFocus(); return;
		 * } if (!visible && rect.intersects(clientArea)) { visible = true; } } if
		 * (!visible) return; index++; } } });
		 */

	}
}
