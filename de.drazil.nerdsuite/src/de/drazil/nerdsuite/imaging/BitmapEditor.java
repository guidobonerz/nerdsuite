package de.drazil.nerdsuite.imaging;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.ImagingWidget;
import de.drazil.nerdsuite.widget.ImagingWidget.WidgetMode;

public class BitmapEditor {

	@Inject
	public BitmapEditor() {
		// TODO Your code here
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
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

		byte binaryData[] = BinaryFileReader.readFile(file, 2);
		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
		ImagingWidget painter = new ImagingWidget(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED);
		painter.setWidgetName("Painter :");
		painter.setWidgetMode(WidgetMode.VIEWER);
		painter.setWidth(8);
		painter.setHeight(8);
		painter.setColumns(40);
		painter.setRows(25);
		painter.setPixelSize(3);
		painter.setPixelGridEnabled(false);
		painter.setTileSubGridEnabled(false);
		painter.setTileGridEnabled(true);
		painter.setColorProvider(new KoalaColorProvider());
		painter.setBitlane(binaryData);
		painter.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(7).getColor());
		painter.setSelectedColor(0);

		painter.recalc();
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button multicolor = new Button(parent, SWT.CHECK);
		multicolor.setSelection(painter.isMultiColorEnabled());
		multicolor.setText("MultiColor");
		multicolor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				painter.setMultiColorEnabled(multicolor.getSelection());
				painter.recalc();
			}
		});

	}

}