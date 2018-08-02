package de.drazil.nerdsuite.viewer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.util.C64Font;
import de.drazil.nerdsuite.util.IFont;

public class BinaryViewer {
	private Table table;

	public BinaryViewer() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {

		// byte binaryData[] = BinaryFileReader.readFile(new
		// File("/Users/drazil/Downloads/mx25l4006e-pollin120916.bin"));
		Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
		URL url = bundle.getEntry("/images/picrambo.prg");
		File file = null;
		try {
			file = new File(FileLocator.resolve(url).toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte binaryData[] = BinaryFileReader.readFile(file);

		IFont font = new C64Font();
		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new BinaryContentProvider());
		TableLabelProvider labelProvider = new TableLabelProvider(font);
		tableViewer.setLabelProvider(labelProvider);

		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setBounds(0, 0, 18, 81);
		table.setFont(font.getFont());

		/*
		 * final TableEditor editor = new TableEditor(table);
		 * editor.horizontalAlignment = SWT.LEFT; editor.grabHorizontal = true;
		 * editor.minimumWidth = 30; table.addSelectionListener(new
		 * SelectionAdapter() { public void widgetSelected(SelectionEvent e) {
		 * // Clean up any previous editor control Control oldEditor =
		 * editor.getEditor(); if (oldEditor != null) oldEditor.dispose();
		 * 
		 * // Identify the selected row TableItem item = (TableItem) e.item; if
		 * (item == null) return;
		 * 
		 * // The control that will be the editor must be a child of the Table
		 * Text newEditor = new Text(table, SWT.NONE);
		 * newEditor.setText(item.getText(1)); newEditor.addModifyListener(new
		 * ModifyListener() { public void modifyText(ModifyEvent e) { Text text
		 * = (Text) editor.getEditor(); editor.getItem().setText(1,
		 * text.getText()); } }); newEditor.selectAll(); newEditor.setFocus();
		 * editor.setEditor(newEditor, item, 1); } });
		 */
		int height = font.getFont().getFontData()[0].getHeight();

		TableColumn counterColumn = new TableColumn(table, SWT.NONE);
		counterColumn.setWidth(height * 7);
		counterColumn.setText("#");

		for (int i = 0; i < 16; i++) {
			TableColumn dataColumn = new TableColumn(table, SWT.NONE);
			dataColumn.setWidth(height * 4);
			dataColumn.setText(String.format("%02x", i));
		}

		TableColumn asciiColumn = new TableColumn(table, SWT.NONE);

		asciiColumn.setWidth(height * 20);
		asciiColumn.setText("ASCII");

		tableViewer.setInput(new BinaryTableModel(binaryData, 0, 16));
	}

	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO Set the focus to control
	}
}
