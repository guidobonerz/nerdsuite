package de.drazil.nerdsuite.stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import de.drazil.nerdsuite.disassembler.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.disassembler.platform.C64Platform;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.util.NumericConverter;

public class U64VicStream extends Thread {

	private DatagramSocket socket;
	private boolean running;
	private byte[] buf = new byte[780];
	private IPlatform platform = null;
	private PaletteData pd = null;
	private byte[] data = new byte[104448];
	private ImageLoader loader = null;

	public U64VicStream() throws Exception {
		platform = new C64Platform(new KickAssemblerDialect(), false);
		RGB[] palette = new RGB[platform.getPlatFormData().getColorPalette().size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = platform.getPlatFormData().getColorPalette().get(i).getColor().getRGB();
		}
		pd = new PaletteData(palette);
		socket = new DatagramSocket(11000);
		loader = new ImageLoader();
	}

	@Execute
	public void execute(MPart part, IEventBroker broker) {
		running = true;
		boolean isStarted = false;

		try {
			int pi = 0;
			while (running) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, address, port);

				int seq = NumericConverter.getWordAsInt(buf, 0);
				int frame = NumericConverter.getWordAsInt(buf, 2);
				int line = NumericConverter.getWordAsInt(buf, 4);
				int pixelPerLine = NumericConverter.getWordAsInt(buf, 6);
				int linesPerPacket = NumericConverter.getByteAsInt(buf, 8);
				int bitsPerPixel = NumericConverter.getByteAsInt(buf, 9);
				int encodingType = NumericConverter.getWordAsInt(buf, 10);

				if (pi > 0 && (line & 0x8000) == 0x8000) {
					pi = 0;
					ImageData sourceData = new ImageData(384, 272, 4, pd, 1, data);
					loader.data = new ImageData[] { sourceData };
					loader.save("c:\\Users\\drazil\\testfile_" + System.currentTimeMillis() + ".png", SWT.IMAGE_PNG);
					data = new byte[104448];
					isStarted = false;
				}

				if (!isStarted && (line & 0x8000) == 0x8000) {
					isStarted = true;
				}

				if (isStarted) {
					// System.out.printf("SEQ:%04x FRAME:%04x LINE:%04x PPL:%04x LPP:%02x BPP:%02x
					// ENC:%04x\n", seq, frame, line, pixelPerLine, linesPerPacket, bitsPerPixel,
					// encodingType);

					for (int i = 12, c = 0; i < 780; i++, c++) {
						byte hc = (byte) ((buf[i] >> 4) & 0x0f);
						byte lc = (byte) (buf[i] & 0x0f);
						data[pi] = hc;
						data[pi + 1] = lc;
						pi += 2;
						// System.out.printf("line:%02d c:%02d BYTE:%04x HC:%02x LC:%02x\n", c%192, c,
						// i, hc, lc);
					}
					// System.out.printf("---------------------------------------\n");

				}

				/*
				 * if (received.equals("end")) { running = false; continue; }
				 */
				socket.send(packet);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
	}

}
