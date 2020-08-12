
package de.drazil.nerdsuite.disassembler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.Value;

public class DisassemblerView {

	private List<InstructionLine> list;

	@Inject
	public DisassemblerView() {
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

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		ColumnLabelProvider labelProvider = new ColumnLabelProvider() {
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
		tableViewerColumn.setLabelProvider(labelProvider);
		TableColumn line = tableViewerColumn.getColumn();
		line.setWidth(300);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(list);

	}

	@PreDestroy
	public void preDestroy() {

	}

	@Focus
	public void onFocus() {

	}

	@Persist
	public void save() {

	}

}