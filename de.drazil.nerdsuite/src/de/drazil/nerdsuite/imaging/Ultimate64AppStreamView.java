package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.model.Key;
import de.drazil.nerdsuite.model.PlatformColor;
import de.drazil.nerdsuite.model.RunObject;
import de.drazil.nerdsuite.model.RunObject.Mode;
import de.drazil.nerdsuite.model.RunObject.Source;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.ImageViewWidget;
import de.drazil.nerdsuite.widget.PlatformFactory;
import lombok.Getter;
import lombok.Setter;

public class Ultimate64AppStreamView {

	private ImageViewWidget imageViewer;
	private Socket tcpSocket = null;
	private Thread videoThread;
	private Thread audioThread;
	private VideoStreamReceiver videoStreamReceiver;
	private AudioStreamReceiver audioStreamReceiver;
	private boolean running = false;
	private boolean virtualKeyboardVisible = true;
	private boolean connectionError = false;

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
			String targetAdress = "10.100.200.205";
			startVicStream(0, targetAdress);
			startSidStream(0, targetAdress);
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
		// vk.setVisible(virtualKeyboardVisible);
		parent.pack(true);

	}

	@Inject
	@Optional
	public void loadAndRunObject(@UIEventTopic("LoadAndRun") BrokerObject brokerObject) {
		try {
			stopStream();
			TimeUnit.MILLISECONDS.sleep(100);
			RunObject runObject = (RunObject) brokerObject.getTransferObject();
			handleObject(runObject);
			TimeUnit.MILLISECONDS.sleep(2000);
			startStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Inject
	@Optional
	public void sendKeyboardSequence(@UIEventTopic("KeyboardSequence") BrokerObject brokerObject) {
		Key key = (Key) brokerObject.getTransferObject();
		int code = key.getCode();
		if (key.getType().equals("KEY") || key.getType().equals("FUNCTION")
				|| (key.getType().equals("COLOR") && key.getOptionState() < 32)) {
			if (code == 3) {
				try {
					byte ba[] = new byte[] {};

					ba = buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
							buildCommand(NumericConverter.getWord(0x0314)), new byte[] { (byte) (0x7b) });
					ba = buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
							buildCommand(NumericConverter.getWord(0x0091)), new byte[] { (byte) (127) });
					write(ba);
					ba = buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
							buildCommand(NumericConverter.getWord(0x0314)), new byte[] { (byte) (0x31) });
					ba = buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
							buildCommand(NumericConverter.getWord(0x0091)), new byte[] { (byte) (255) });

					write(ba);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				byte codes[] = new byte[] { (byte) (code & 0xff) };
				if (key.getName().equals("RUN")) {
					codes = buildCommand("RUN".getBytes(), new byte[] { 13 });
				} else if (key.getName().equals("LIST")) {
					codes = buildCommand("LIST".getBytes(), new byte[] { 13 });
				} else if (key.getName().equals("DIR")) {
					codes = buildCommand("LOAD\"$\",8".getBytes(), new byte[] { 13 });
				} else if (key.getName().equals("LOAD*")) {

				} else {

				}
				sendKeyboardSequence(codes);
			}
		} else if (key.getType().equals("COLOR"))

		{
			byte ba[] = new byte[] {};

			if ((key.getOptionState() & 32) == 32) {
				ba = buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						NumericConverter.getWord(0xd020), new byte[] { key.getIndex().byteValue() });
			}
			if ((key.getOptionState() & 64) == 64) {
				ba = buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						NumericConverter.getWord(0xd021), new byte[] { key.getIndex().byteValue() });
			}
			try {
				write(ba);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		parent.setBackground(Constants.BLACK);
		List<PlatformColor> colorList = PlatformFactory.getPlatformColors("C64");
		RGB palette[] = new RGB[colorList.size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = colorList.get(i).getColor().getRGB();
		}
		parent.setLayout(new GridLayout(1, true));
		parent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				sendKeyboardSequence(new byte[] { (byte) e.character });
			}
		});

		imageViewer = createImageViewer(parent, new PaletteData(palette));
		imageViewer.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, true));
		videoStreamReceiver = new VideoStreamReceiver();
		audioStreamReceiver = new AudioStreamReceiver();
		startStream();

	}

	public ImageViewWidget createImageViewer(Composite parent, PaletteData pd) {
		return new ImageViewWidget(parent, SWT.DOUBLE_BUFFERED, pd);
	}

	private void startVicStream(int duration, String target) {
		byte durationArray[] = NumericConverter.getWord(duration);
		byte targetArray[] = target.getBytes();
		byte length[] = NumericConverter.getWord(durationArray.length + targetArray.length);
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff20), length, durationArray, targetArray));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void stopVicStream() {
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff30), new byte[] { (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void startSidStream(int duration, String target) {
		byte durationArray[] = NumericConverter.getWord(duration);
		byte targetArray[] = target.getBytes();
		byte length[] = NumericConverter.getWord(durationArray.length + targetArray.length);
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff21), length, durationArray, targetArray));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopSidStream() {
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff31), new byte[] { (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleObject(RunObject runObject) {
		openSocket();
		int cmd_dma = 0xff01;
		int cmd_dma_run = 0xff02;
		int cmd_dma_jump = 0xff09;
		int cmd_dma_mount_img = 0xff0a;
		int cmd_dma_run_img = 0xff0b;
		byte[] command = null;
		try {
			if (runObject.getSource() == Source.Program) {
				if (runObject.getMode() == Mode.None && runObject.getStartAdress() == -1) {
					command = buildCommand(NumericConverter.getWord(cmd_dma),
							NumericConverter.getWord(runObject.getPayload().length));
				} else if (runObject.getMode() == Mode.Run && runObject.getStartAdress() == -1) {
					command = buildCommand(NumericConverter.getWord(cmd_dma_run),
							NumericConverter.getWord(runObject.getPayload().length));
				} else if (runObject.getMode() == Mode.Run && runObject.getStartAdress() != -1) {
					command = buildCommand(NumericConverter.getWord(cmd_dma_jump),
							NumericConverter.getWord(runObject.getPayload().length + 4),
							NumericConverter.getWord(runObject.getStartAdress()));
				}
			} else {
				byte l[] = NumericConverter.getLongWord(runObject.getPayload().length);
				byte length[] = new byte[] { l[0], l[1], l[2] };
				if (runObject.getMode() == Mode.Run) {
					command = buildCommand(NumericConverter.getWord(cmd_dma_run_img), length);
				} else {
					command = buildCommand(NumericConverter.getWord(cmd_dma_mount_img), length);
				}
			}

			write(command);
			write(runObject.getPayload());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reset() {
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff04), new byte[] { (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void wait(int delay) {
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff05), NumericConverter.getWord(delay)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readMemory(int address, int length) {
		openSocket();
		try {

			write(buildCommand(NumericConverter.getWord(0xff74), NumericConverter.getWord(length + 2),
					buildCommand(NumericConverter.getWord(address))));
			InputStream is = tcpSocket.getInputStream();
			TimeUnit.MILLISECONDS.sleep(500);
			int dataAvailable = 0;
			byte data[] = null;
			is.read();
			while ((dataAvailable = is.available()) > 0) {
				data = new byte[dataAvailable];
				is.read(data, 0, dataAvailable);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void writeMemory(int address, byte[] data) {
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff06), NumericConverter.getWord(data.length + 2),
					NumericConverter.getWord(address), data));
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void sendKeyboardSequence(byte[] data) {
		openSocket();
		try {
			write(buildCommand(NumericConverter.getWord(0xff03), NumericConverter.getWord(data.length), data));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write(byte[] data) throws IOException {
		tcpSocket.getOutputStream().write(data);
	}

	private Socket openSocket() {
		SocketAddress socketAddress = new InetSocketAddress("10.100.200.201", 64);
		try {
			if (tcpSocket == null) {
				tcpSocket = new Socket();
				tcpSocket.connect(socketAddress, 2000);
				connectionError = false;
			}
		} catch (Exception e) {
			connectionError = true;
			showErrorDialog(
					String.format("Connection to Ultimate64@%s could not be established", socketAddress.toString()));
		}
		return tcpSocket;
	}

	private void closeSocket() {
		try {
			if (!connectionError && tcpSocket != null && tcpSocket.isConnected()) {
				tcpSocket.close();
				tcpSocket = null;
				connectionError = false;
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

	private void showErrorDialog(String message) {
		MessageDialog.openError(parent.getShell(), "Connection error", message);
	}
}
