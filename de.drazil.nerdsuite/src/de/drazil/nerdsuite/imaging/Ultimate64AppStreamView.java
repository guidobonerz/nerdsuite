package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

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
import lombok.Getter;
import lombok.Setter;

public class Ultimate64AppStreamView {

	private Composite parent;
	private ImageViewWidget imageViewer;
	private Socket tcpSocket = null;
	private Thread videoThread;
	private Thread audioThread;
	private VideoStreamer videoStreamer;
	private AudioStreamer audioStreamer;
	private boolean running = false;

	public Ultimate64AppStreamView() {

	}

	public class VideoStreamer implements Runnable {

		private byte[] data = new byte[52224];
		private byte[] dataBuffer = new byte[780];
		@Setter
		@Getter
		private boolean running = false;

		private int offset = 0;
		private DatagramSocket socket;

		public synchronized void run() {
			try {
				socket = new DatagramSocket(11000);
				// socket.setSoTimeout(1000);
				while (socket != null && running) {
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
									imageViewer.drawImage(false);
								}
							});
						}
						offset = 0;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("video socket closed");
				socket.close();
				socket = null;
			}
		}
	}

	public class AudioStreamer implements Runnable {

		private byte[] dataBuffer = new byte[770];
		private DatagramSocket socket;
		@Setter
		@Getter
		private boolean running = false;

		public synchronized void run() {
			try {
				socket = new DatagramSocket(11001);
				// socket.setSoTimeout(1000);
				AudioFormat af = new AudioFormat(48000, 16, 2, true, false);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
				SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(af, 4096);
				line.start();
				while (socket != null && running) {
					DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
					socket.receive(packet);
					InetAddress address = packet.getAddress();
					int port = packet.getPort();
					packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
					int seq = NumericConverter.getWordAsInt(dataBuffer, 0);
					line.write(dataBuffer, 2, dataBuffer.length - 2);

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("audio socket closed");
				socket.close();
				socket = null;
			}
		}
	}

	@Inject
	@Optional
	public void startStream(@UIEventTopic("StartStream") BrokerObject brokerObject) {
		startStream();
	}

	private void startStream() {
		if (!running) {
			running = true;
			startVicStream();
			startSidStream();
			imageViewer.drawImage(false);

			videoThread = new Thread(videoStreamer);
			audioThread = new Thread(audioStreamer);
			videoThread.start();
			audioThread.start();
			videoStreamer.setRunning(true);
			audioStreamer.setRunning(true);
		}
	}

	@Inject
	@Optional
	public void stopStream(@UIEventTopic("StopStream") BrokerObject brokerObject) {
		stopStream();
	}

	private void stopStream() {
		if (running) {
			running = false;
			videoStreamer.setRunning(false);
			audioStreamer.setRunning(false);
			stopVicStream();
			stopSidStream();
			videoThread = null;
			audioThread = null;
			imageViewer.drawImage(true);
		}
	}

	@Inject
	@Optional
	public void reset(@UIEventTopic("Reset") BrokerObject brokerObject) {
		try {
			stopStream();
			TimeUnit.MILLISECONDS.sleep(100);
			reset();
			TimeUnit.MILLISECONDS.sleep(100);
			startStream();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Inject
	@Optional
	public void loadAndRunProgram(@UIEventTopic("LoadAndRun") BrokerObject brokerObject) {
		try {
			stopStream();
			TimeUnit.MILLISECONDS.sleep(100);
			byte[] data = (byte[]) brokerObject.getTransferObject();
			loadCode(data, true);
			TimeUnit.MILLISECONDS.sleep(100);
			startStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		videoStreamer = new VideoStreamer();
		audioStreamer = new AudioStreamer();
	}

	public ImageViewWidget createImageViewer(Composite parent, PaletteData pd) {
		imageViewer = new ImageViewWidget(parent, SWT.DOUBLE_BUFFERED, pd);
		imageViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return imageViewer;
	}

	private void startVicStream() {
		openSocket();
		try {
			tcpSocket.getOutputStream().write(buildCommand(NumericConverter.getWord(0xff20),
					new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void stopVicStream() {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff30), new byte[] { (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void startSidStream() {
		openSocket();
		try {
			tcpSocket.getOutputStream().write(buildCommand(NumericConverter.getWord(0xff21),
					new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopSidStream() {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff31), new byte[] { (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadCode(byte[] data) {
		loadCode(data, true, -1);
	}

	private void loadCode(byte[] data, boolean run) {
		loadCode(data, run, -1);
	}

	private void loadCode(byte[] data, boolean run, int adress) {
		openSocket();
		int cmd_dma = 0xff01;
		int cmd_dma_run = 0xff02;
		int cmd_dma_jump = 0xff09;
		byte[] command = null;
		if (!run && adress == -1) {
			command = buildCommand(NumericConverter.getWord(cmd_dma), NumericConverter.getWord(data.length));
		} else if (run && adress == -1) {
			command = buildCommand(NumericConverter.getWord(cmd_dma_run), NumericConverter.getWord(data.length));
		} else if (run && adress != -1) {
			command = buildCommand(NumericConverter.getWord(cmd_dma_jump), NumericConverter.getWord(data.length + 4),
					NumericConverter.getWord(1), NumericConverter.getWord(adress));
		}
		try {
			tcpSocket.getOutputStream().write(command);
			tcpSocket.getOutputStream().write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reset() {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff04), new byte[] { (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket openSocket() {
		try {
			if (tcpSocket == null) {
				tcpSocket = new Socket(InetAddress.getByName("10.100.200.195"), 64);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tcpSocket;
	}

	private void closeSocket() {
		try {
			if (tcpSocket != null && tcpSocket.isConnected()) {
				// tcpSocket.getOutputStream().write(Mes);
				tcpSocket.close();
				tcpSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] buildCommand(byte[]... data) {
		int targetLength = 0;
		for (byte[] source : data) {
			targetLength += source.length;
		}
		byte[] target = new byte[targetLength];
		int targetPos = 0;
		for (byte[] source : data) {
			System.arraycopy(source, 0, target, targetPos, source.length);
			targetPos += source.length;
		}
		return target;
	}
}
