
package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.BinaryFileReader;
import de.drazil.nerdsuite.widget.ImageRepository;

public class RepositoryView {
	private ImageRepository repository = null;
	private Composite parent = null;
	private byte binaryData[] = null;
	private byte blankData[] = null;

	@Inject
	public RepositoryView() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		getRepository();
	}

	private ImageRepository getRepository() {
		if (repository == null) {
			repository = new ImageRepository(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL) {

				/*
				 * 
				 * @Override protected void setHasTileSelection(int count) {
				 * getStartAnimation().setEnabled(count > 1);
				 * getAnimationTimerDelayScale().setEnabled(count > 1); }
				 * 
				 * @Override protected void showNotification(ImagingServiceDescription type,
				 * ImagingServiceAction mode, String notification, Object data) { if (type ==
				 * ImagingServiceDescription.Animation) {
				 * getStartAnimation().setText(notification); } else {
				 * MessageDialog.openInformation(parent.getShell(), "Information",
				 * notification); } }
				 * 
				 * @Override protected boolean isConfirmed(ImagingServiceDescription type,
				 * ImagingServiceAction mode, int tileCount) { boolean confirmation = false; if
				 * (type == ImagingServiceDescription.Rotate) { confirmation =
				 * MessageDialog.openQuestion(parent.getShell(), "Question",
				 * "Rotating these tile(s) causes data loss, because it is/they are not squarish.\n\nDo you want to rotate anyway?"
				 * ); } if (type == ImagingServiceDescription.All) { confirmation =
				 * MessageDialog.openQuestion(parent.getShell(), "Question",
				 * MessageFormat.format( "Do you really want to process {0} ?", (tileCount > 1)
				 * ? "all selected tiles" : "this tile")); } return confirmation; }
				 * 
				 * @Override protected void setNotification(int offset, int tileSize) {
				 * 
				 * getNotification().setText(MessageFormat.format(
				 * "Offset: ${0} tile:{1} bytes", String.format("%04X", offset), tileSize)); }
				 */
			};
			repository.getConf().setWidgetName("Selector:");
			repository.getConf().setWidth(40);
			repository.getConf().setHeight(25);
			repository.getConf().setWidth(8);
			repository.getConf().setHeight(8);
			repository.getConf().setPixelSize(3);
			repository.getConf().setPixelGridEnabled(false);
			repository.getConf().setTileGridEnabled(true);
			repository.getConf().setTileSubGridEnabled(false);
			repository.getConf().setTileCursorEnabled(true);
			repository.getConf().setSeparatorEnabled(false);
			repository.setSelectedTileOffset(0, 0, false);
			repository.setBitplane(getBlankData());
			repository.setImagePainterFactory(null);
			repository.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
			repository.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
			repository.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
			repository.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());
			repository.setSelectedColor(1);
			repository.recalc();
			// selector.addDrawListener(getPainter());
			// selector.addDrawListener(getPreviewer());

		}
		return repository;

	}

	private byte[] getBlankData() {
		if (blankData == null) {
			blankData = new byte[0x1f40];
			for (int i = 0; i < blankData.length; i++)
				blankData[i] = 32;// (byte) (Math.random() * 80);
		}
		return blankData;
	}

	private byte[] getBinaryData() {
		if (binaryData == null) {

			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL url = bundle.getEntry("fonts/c64_lower.64c");

			try {
				binaryData = BinaryFileReader.readFile(url.openConnection().getInputStream(), 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return binaryData;
	}

}