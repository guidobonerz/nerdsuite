package de.drazil.nerdsuite.imaging;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.IColorProvider;
import de.drazil.nerdsuite.widget.ImagingWidget;

public class BitmapEditor implements IColorProvider {
	private Text offsetField;

	@Inject
	public BitmapEditor() {
		// TODO Your code here
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		// byte binaryData[] = BinaryFileReader.readFile(new
		// File("/Users/drazil/Downloads/dumprambo"));
		byte binaryData[] = BinaryFileReader.readFile(new File("C:\\Users\\drazil\\Downloads\\pic rambo.prg"));
		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
		ImagingWidget painter = new ImagingWidget(parent,
				SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.DOUBLE_BUFFERED);

		painter.setWidth(8);
		painter.setHeight(8);
		painter.setColumns(40);
		painter.setRows(25);
		painter.setPixelSize(3);
		painter.setPixelGridEnabled(false);
		painter.setTileGridEnabled(true);
		painter.setMultiColorEnabled(true);

		painter.setSelectedTileOffset(2);
		painter.setColorProvider(this);

		painter.setContent(binaryData);
		painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		painter.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(4).getColor());
		painter.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(10).getColor());
		painter.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(15).getColor());
		painter.setSelectedColor(3);

		painter.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		painter.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent e) {
				int offset = painter.getSelectedTileOffset();
				offsetField.setText(Integer.toHexString(offset));

			}
		});
		painter.getVerticalBar().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// painter.setOffset(painter.getVerticalBar().getSelection());
				// int offset = painter.getOffset();
				// offsetField.setText(Integer.toHexString(offset));

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		parent.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				painter.setMultiColorEnabled(multicolor.getSelection());

			}
		});
		offsetField = new Text(parent, SWT.NONE);
		Button shiftLeft = new Button(parent, SWT.NONE);
		shiftLeft.setText("Shift Left");
		Button shiftRight = new Button(parent, SWT.NONE);
		shiftRight.setText("Shift Right");
		Button shiftUp = new Button(parent, SWT.NONE);
		shiftUp.setText("Shift Up");
		shiftUp.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int offset = painter.getSelectedTileOffset();
				painter.setSelectedTileOffset(offset + painter.getWidth() / 8);
			}
		});

		Button shiftDown = new Button(parent, SWT.NONE);
		shiftDown.setText("Shift Down");
		shiftDown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int offset = painter.getSelectedTileOffset();
				painter.setSelectedTileOffset(offset - painter.getWidth() / 8);
			}
		});

		Button addColumn = new Button(parent, SWT.NONE);
		addColumn.setText("Add Column");
		addColumn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int columnCount = painter.getColumns();
				painter.setColumns(columnCount + 1);
				parent.layout();
			}
		});
		Button removeColumn = new Button(parent, SWT.NONE);
		removeColumn.setText("Remove Column");
		removeColumn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int columnCount = painter.getColumns();
				if (columnCount > 1) {
					painter.setColumns(columnCount - 1);
					parent.layout();
				}
			}
		});
		Button addRow = new Button(parent, SWT.NONE);
		addRow.setText("Add Row");
		Button removeRow = new Button(parent, SWT.NONE);
		removeRow.setText("Remove Row");

	}

	@Override
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int offset, int index) {
		int colorIndex = 7;

		if ((bitmapByte) == 1) {
			colorIndex = (bitmap[offset + 8000 + index] >> 4) & 0xf;
		} else if ((bitmapByte) == 2) {
			colorIndex = (bitmap[offset + 8000 + index] & 0xf);
		} else if ((bitmapByte) == 3) {
			colorIndex = (bitmap[offset + 9000 + index] & 0xf);
		}

		return InstructionSet.getPlatformData().getColorPalette().get(colorIndex).getColor();
	}
}