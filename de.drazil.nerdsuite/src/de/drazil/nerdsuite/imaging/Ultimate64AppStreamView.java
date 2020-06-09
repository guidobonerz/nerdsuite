package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.disassembler.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.disassembler.platform.C64Platform;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.ImageViewWidget;

public class Ultimate64AppStreamView {

	private Composite parent;
	private ImageViewWidget imageViewer;

	public Ultimate64AppStreamView() {

	}

	public class VideoStreamer implements Runnable {

		private byte[] data = new byte[52224];
		private byte[] dataBuffer = new byte[780];
		private boolean running = true;
		private int offset = 0;
		private DatagramSocket socket;

		public VideoStreamer() {

		}

		public synchronized void run() {
			try {
				socket = new DatagramSocket(11000);
				while (running) {
					DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
					socket.receive(packet);
					InetAddress address = packet.getAddress();
					int port = packet.getPort();
					packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
					// int seq = NumericConverter.getWordAsInt(buf, 0);
					int frame = NumericConverter.getWordAsInt(dataBuffer, 2);
					int line = NumericConverter.getWordAsInt(dataBuffer, 4);
					// int pixelPerLine = NumericConverter.getWordAsInt(buf, 6);
					// int linesPerPacket = NumericConverter.getByteAsInt(buf, 8);
					// int bitsPerPixel = NumericConverter.getByteAsInt(buf, 9);
					// int encodingType = NumericConverter.getWordAsInt(buf, 10);

					if (offset < data.length) {
						// System.out.printf(" line: %04x\n", line);
						System.arraycopy(dataBuffer, 12, data, offset, dataBuffer.length - 12);
						offset += (dataBuffer.length - 12);
					}

					if ((line & 0x8000) == 0x8000) {
						// System.out.printf("frame: %04x\n", frame);
						if (offset == data.length) {
							imageViewer.addImageData(data);
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									imageViewer.drawImage();
								}
							});
						}
						offset = 0;
					}

					socket.send(packet);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket.close();
		}
	}

	public class AudioStreamer implements Runnable {

		private byte[] dataBuffer = new byte[770];
		private AudioSystem as = null;
		private boolean running = true;
		private DatagramSocket socket;

		public AudioStreamer() {

		}

		public synchronized void run() {
			try {
				socket = new DatagramSocket(11001);
				AudioFormat af = new AudioFormat(48000, 16, 2, true, false);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
				SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(af, 4096);
				line.start();
				while (running) {
					DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
					socket.receive(packet);
					InetAddress address = packet.getAddress();
					int port = packet.getPort();
					packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
					int seq = NumericConverter.getWordAsInt(dataBuffer, 0);
					line.write(dataBuffer, 2, dataBuffer.length - 2);
					socket.send(packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			socket.close();
		}
	}

	// @Inject
	// @Optional
	// public void startStream(@UIEventTopic("StartStream") BrokerObject
	// brokerObject) {
	public void startStream() {

		Socket tcpSocket = null;

		try {

			byte[] f5 = new byte[] { 0x1b, 0x5b, 0x31, 0x35, 0x7e, 0x1b, 0x5b, 0x42, 0x1b, 0x5b, 0x42, 0x1b, 0x5b, 0x42,
					0x1b, 0x5b, 0x42, 0x1b, 0x5b, 0x42, 0x1b, 0x5b, 0x42, 0x1b, 0x5b, 0x42, 0x1b, 0x5b, 0x42, 0xd,
					0x00 };
			byte[] remoteStartVideo = new byte[] { (byte) 0x20, (byte) 0xff, (byte) 0x02, (byte) 0x00, (byte) 0x00,
					(byte) 0x00 };
			byte[] remoteStartAudio = new byte[] { (byte) 0x21, (byte) 0xff, (byte) 0x02, (byte) 0x00, (byte) 0x00,
					(byte) 0x00 };
			byte[] videoPayload = new byte[remoteStartVideo.length];
			byte[] audioPayload = new byte[remoteStartAudio.length];
			byte[] f5Payload = new byte[f5.length];
			System.arraycopy(remoteStartVideo, 0, videoPayload, 0, remoteStartVideo.length);
			System.arraycopy(remoteStartAudio, 0, audioPayload, 0, remoteStartAudio.length);
			System.arraycopy(f5, 0, f5Payload, 0, f5.length);

			tcpSocket = new Socket(InetAddress.getByName("10.100.200.195"), 64);
			tcpSocket.getOutputStream().write(videoPayload);
			tcpSocket.getOutputStream().write(audioPayload);
			// tcpSocket.getOutputStream().write(f5Payload);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (tcpSocket != null) {
				try {
					tcpSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		new Thread(new VideoStreamer()).start();
		new Thread(new AudioStreamer()).start();
		// parent.getDisplay().timerExec(0, streamer);
	}

	@Inject
	@Optional
	public void stopStream(@UIEventTopic("StopStream") BrokerObject brokerObject) {

	}

	@PreDestroy
	public void preDestroy(MApplication app, MTrimmedWindow window, EModelService modelService, MPart part) {
		if (part.isDirty()) {

		}
	}

	@PostConstruct
	public void postConstruct(Composite parent, MApplication app, MTrimmedWindow window, EMenuService menuService) {
		this.parent = parent;
		IPlatform platform = new C64Platform(new KickAssemblerDialect(), false);
		RGB[] palette = new RGB[platform.getPlatFormData().getColorPalette().size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = platform.getPlatFormData().getColorPalette().get(i).getColor().getRGB();
		}

		parent.setLayout(new GridLayout());
		imageViewer = createImageViewer(parent, new PaletteData(palette));
		startStream();

	}

	public ImageViewWidget createImageViewer(Composite parent, PaletteData pd) {
		imageViewer = new ImageViewWidget(parent, SWT.DOUBLE_BUFFERED, pd);
		imageViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return imageViewer;
	}
}
