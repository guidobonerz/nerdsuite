package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.model.PlatformColor;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.IHitKeyListener;
import de.drazil.nerdsuite.widget.ImageViewWidget;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.VirtualKeyboard;
import lombok.Getter;
import lombok.Setter;

public class Ultimate64AppStreamView implements IHitKeyListener {

	private ImageViewWidget imageViewer;
	private Socket tcpSocket = null;
	private Thread videoThread;
	private Thread audioThread;
	private VideoStreamReceiver videoStreamReceiver;
	private AudioStreamReceiver audioStreamReceiver;
	private boolean running = false;
	private boolean virtualKeyboardVisible = true;
	private VirtualKeyboard vk;
	private Composite parent;
	private int controlType;

	public Ultimate64AppStreamView() {

	}

	public class VideoStreamReceiver implements Runnable {

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
				int count = 0;
				while (socket != null && running) {
					DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
					socket.receive(packet);
					InetAddress address = packet.getAddress();
					int port = packet.getPort();
					packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
					int seq = NumericConverter.getWordAsInt(dataBuffer, 0);
					// int frame = NumericConverter.getWordAsInt(dataBuffer, 2);
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
					// System.out.printf("line: %04x\n", line);
					// System.out.printf("seq: %04d\n", seq);
					if ((line & 0x8000) == 0x8000) {

						if (offset == data.length && count == 67) {
							imageViewer.addImageData(data);
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									imageViewer.drawImage(false);
								}
							});
						}
						count = 0;

						offset = 0;
					} else {
						count++;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socket.close();
				socket = null;
			}
		}
	}

	public class AudioStreamReceiver implements Runnable {

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
				socket.close();
				socket = null;
			}
		}
	}

	@Inject

	@Optional
	public void dumpMemory(@UIEventTopic("ReadMemory") BrokerObject brokerObject) {
		readMemory(0x400, 200);
	}

	@Inject
	@Optional
	public void poke(@UIEventTopic("WriteMemory") BrokerObject brokerObject) {
		writeMemory(0x0400, new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
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

			videoThread = new Thread(videoStreamReceiver);
			audioThread = new Thread(audioStreamReceiver);
			videoThread.start();
			audioThread.start();
			videoStreamReceiver.setRunning(true);
			audioStreamReceiver.setRunning(true);
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
			videoStreamReceiver.setRunning(false);
			audioStreamReceiver.setRunning(false);
			stopVicStream();
			stopSidStream();
			videoThread = null;
			audioThread = null;
			imageViewer.drawImage(true);
		}
	}

	@Inject
	@Optional
	public void streamVideo(@UIEventTopic("StreamVideo") BrokerObject brokerObject) {
		System.out.println("Stream Video");
		/*
		 * int snapsPerSecond = 10; int duration = 100; String formatName = ""; String
		 * fileName = ""; String codecName = ""; final Rational framerate =
		 * Rational.make(1, snapsPerSecond);
		 * 
		 * final Muxer muxer = Muxer.make(fileName, null, formatName);
		 * 
		 * final MuxerFormat format = muxer.getFormat(); final Codec codec; if
		 * (codecName != null) { codec = Codec.findEncodingCodecByName(codecName); }
		 * else { codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId()); }
		 * 
		 * Encoder encoder = Encoder.make(codec);
		 * 
		 * encoder.setWidth(320); encoder.setHeight(200);
		 * 
		 * final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
		 * encoder.setPixelFormat(pixelformat); encoder.setTimeBase(framerate);
		 * 
		 * if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
		 * encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
		 * 
		 * encoder.open(null, null);
		 * 
		 * muxer.addNewStream(encoder);
		 * 
		 * muxer.open(null, null);
		 * 
		 * MediaPictureConverter converter = null; final MediaPicture picture =
		 * MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
		 * picture.setTimeBase(framerate);
		 * 
		 * final MediaPacket packet = MediaPacket.make(); for (int i = 0; i < duration /
		 * framerate.getDouble(); i++) {
		 * 
		 * 
		 * 
		 * 
		 * final BufferedImage screen =
		 * convertToType(robot.createScreenCapture(screenbounds),
		 * BufferedImage.TYPE_3BYTE_BGR);
		 * 
		 * if (converter == null) converter =
		 * MediaPictureConverterFactory.createConverter(screen, picture);
		 * converter.toPicture(picture, screen, i);
		 * 
		 * do { encoder.encode(packet, picture); if (packet.isComplete())
		 * muxer.write(packet, false); } while (packet.isComplete());
		 * 
		 * Thread.sleep((long) (1000 * framerate.getDouble())); }
		 * 
		 * do { encoder.encode(packet, null); if (packet.isComplete())
		 * muxer.write(packet, false); } while (packet.isComplete());
		 * 
		 * muxer.close();
		 */
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
	public void virtualKeyboard(@UIEventTopic("VirtualKeyboard") BrokerObject brokerObject) {
		virtualKeyboardVisible = !virtualKeyboardVisible;
		vk.setVisible(virtualKeyboardVisible);
		parent.pack(true);

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
	public void preDestroy(MPart part) {
		stopStream();
		closeSocket();
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		List<PlatformColor> colorList = PlatformFactory.getPlatformColors("C64");
		RGB palette[] = new RGB[colorList.size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = colorList.get(i).getColor().getRGB();
		}
		parent.setLayout(new GridLayout());
		parent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				sendKeyboardSequence(new byte[] { (byte) e.character });
				System.out.println(e.character);
			}
		});
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		imageViewer = createImageViewer(parent, new PaletteData(palette));
		imageViewer.setLayoutData(gd);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		// gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.heightHint = 200;
		vk = new VirtualKeyboard(parent, 0, null);
		vk.addHitKeyListener(this);
		vk.setLayoutData(gd);

		videoStreamReceiver = new VideoStreamReceiver();
		audioStreamReceiver = new AudioStreamReceiver();
		startStream();

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

	private void readMemory(int address, int length) {
		openSocket();
		try {

			tcpSocket.getOutputStream().write(buildCommand(NumericConverter.getWord(0xff74),
					NumericConverter.getWord(length + 2), buildCommand(NumericConverter.getWord(address))));
			InputStream is = tcpSocket.getInputStream();
			TimeUnit.MILLISECONDS.sleep(500);
			int dataAvailable = 0;
			byte data[] = null;
			is.read();
			while ((dataAvailable = is.available()) > 0) {
				data = new byte[dataAvailable];
				is.read(data, 0, dataAvailable);
			}
			int a = 0;
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void writeMemory(int address, byte[] data) {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff06), NumericConverter.getWord(data.length + 2),
							buildCommand(NumericConverter.getWord(address)), buildCommand(data)));
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void sendKeyboardSequence(byte[] data) {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff03), new byte[] { (byte) 0x01, (byte) 0x00 }));
			tcpSocket.getOutputStream().write(data);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket openSocket() {
		try {
			if (tcpSocket == null) {
				tcpSocket = new Socket(InetAddress.getByName("10.100.200.201"), 64);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tcpSocket;
	}

	private void closeSocket() {
		try {
			if (tcpSocket != null && tcpSocket.isConnected()) {
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

	@Override
	public void keyPressed(int controlType, int key) {
		if (key == 0) {
			this.controlType = controlType;
		}
		int k = key;
		if (this.controlType == 2) {
			k += 32;
		} else if (this.controlType == 3) {
			k += 96;
		}
		if (key > 0) {
			System.out.printf("control:%d  key:%d\n", this.controlType, k);
			sendKeyboardSequence(new byte[] { (byte) (k & 0xff) });
		}
	}

}
