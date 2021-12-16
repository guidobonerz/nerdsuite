package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
import org.eclipse.swt.custom.SashForm;
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
import de.drazil.nerdsuite.network.TcpHandler;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.IHitKeyListener;
import de.drazil.nerdsuite.widget.ImageViewWidget;
import de.drazil.nerdsuite.widget.MemoryViewWidget;
import de.drazil.nerdsuite.widget.PlatformFactory;
import de.drazil.nerdsuite.widget.VirtualKeyboard;
import lombok.Getter;
import lombok.Setter;

public class Ultimate64AppStreamView extends AbstractStreamView implements IHitKeyListener {

	private ImageViewWidget imageViewer;
	private MemoryViewWidget memoryViewer;
	private VirtualKeyboard virtualKeyboard;

	private Thread videoThread;
	private Thread audioThread;

	private VideoStreamReceiver videoStreamReceiver;
	private AudioStreamReceiver audioStreamReceiver;

	private boolean lifeViewMode = false;
	private boolean virtualKeyboardVisible = false;
	private boolean isMute = false;

	private Composite parent;
	private Composite top;
	private Composite bottom;

	private SashForm verticalSash;

	private int streamingMode = VIDEO_STREAM + AUDIO_STREAM;

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

	public void keyPressed(Key key) {
		sendKeyboardSequence(key);
	}

	@Inject
	@Optional
	public void debugStream(@UIEventTopic("U64Debug") BrokerObject brokerObject) {
		lifeViewMode = !lifeViewMode;
		controlStream();
	}

	@Inject
	@Optional
	public void muteStream(@UIEventTopic("U64Mute") BrokerObject brokerObject) {
		isMute = !isMute;
		controlStream();
	}

	private void controlStream() {
		if (lifeViewMode) {
			streamingMode = VIDEO_STREAM + (!isMute ? AUDIO_STREAM : 0);
		} else {
			streamingMode = DEBUG_STREAM + (!isMute ? AUDIO_STREAM : 0);
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
		startStream(VIDEO_STREAM + AUDIO_STREAM);
	}

	private void startStream(int streamingMode) {
		String targetAdress = "10.100.200.205";
		if ((streamingMode & VIDEO_STREAM) == VIDEO_STREAM && !videoStreamReceiver.isRunning()) {
			startStreamByCommand(VIC_STREAM_START_COMMAND, 0, targetAdress);
			imageViewer.drawImage(false);
			videoThread = new Thread(videoStreamReceiver);
			videoThread.start();
			videoStreamReceiver.setRunning(true);
		}
		if ((streamingMode & AUDIO_STREAM) == AUDIO_STREAM && !audioStreamReceiver.isRunning()) {
			startStreamByCommand(SID_STREAM_START_COMMAND, 0, targetAdress);
			audioThread = new Thread(audioStreamReceiver);
			audioThread.start();
			audioStreamReceiver.setRunning(true);
		}

	}

	@Inject
	@Optional
	public void stopStream(@UIEventTopic("StopStream") BrokerObject brokerObject) {
		stopStream(VIDEO_STREAM + AUDIO_STREAM);
	}

	private void stopStream(int streamingMode) {

		if ((streamingMode & VIDEO_STREAM) == VIDEO_STREAM && videoStreamReceiver.isRunning()) {
			videoStreamReceiver.setRunning(false);
			stopStreamByCommand(VIC_STREAM_STOP_COMMAND);
			videoThread = null;
			imageViewer.drawImage(true);
		}
		if ((streamingMode & AUDIO_STREAM) == AUDIO_STREAM && audioStreamReceiver.isRunning()) {
			audioStreamReceiver.setRunning(false);
			stopStreamByCommand(SID_STREAM_STOP_COMMAND);
			audioThread = null;
		}

	}

	@Inject
	@Optional
	public void reset(@UIEventTopic("ResetU64") BrokerObject brokerObject) {
		try {
			stopStream(VIDEO_STREAM + AUDIO_STREAM);
			TimeUnit.MILLISECONDS.sleep(100);
			reset();
			TimeUnit.MILLISECONDS.sleep(100);
			startStream(VIDEO_STREAM + AUDIO_STREAM);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Inject
	@Optional
	public void virtualKeyboard(@UIEventTopic("VirtualKeyboard") BrokerObject brokerObject) {
		virtualKeyboardVisible = !virtualKeyboardVisible;
		virtualKeyboard.getParent().setVisible(virtualKeyboardVisible);
		if (!virtualKeyboardVisible) {
			showWithKeyboard(false);
			verticalSash.layout(true);
		} else {
			showWithKeyboard(true);
			verticalSash.layout(true);
		}
	}

	@Inject
	@Optional
	public void loadAndRunObject(@UIEventTopic("LoadAndRun") BrokerObject brokerObject) {
		try {
			stopStream(VIDEO_STREAM + AUDIO_STREAM);
			TimeUnit.MILLISECONDS.sleep(100);
			RunObject runObject = (RunObject) brokerObject.getTransferObject();
			handleObject(runObject);
			TimeUnit.MILLISECONDS.sleep(2000);
			startStream(VIDEO_STREAM + AUDIO_STREAM);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Inject
	@Optional
	public void sendKeyboardSequence(Key key) {

		int code = key.getCode();
		if (key.getType().equals("KEY") || key.getType().equals("FUNCTION")
				|| (key.getType().equals("COLOR") && key.getOptionState() < 32)) {
			if (code == 3) {

				byte ba[] = new byte[] {};

				ba = tcpHandler.buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						tcpHandler.buildCommand(NumericConverter.getWord(0x0314)), new byte[] { (byte) (0x7b) });
				ba = tcpHandler.buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						tcpHandler.buildCommand(NumericConverter.getWord(0x0091)), new byte[] { (byte) (127) });
				tcpHandler.write(ba);
				ba = tcpHandler.buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						tcpHandler.buildCommand(NumericConverter.getWord(0x0314)), new byte[] { (byte) (0x31) });
				ba = tcpHandler.buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						tcpHandler.buildCommand(NumericConverter.getWord(0x0091)), new byte[] { (byte) (255) });
				tcpHandler.write(ba);
			} else {
				byte codes[] = new byte[] { (byte) (code & 0xff) };
				if (key.getName().equals("RUN")) {
					codes = tcpHandler.buildCommand("RUN".getBytes(), new byte[] { 13 });
				} else if (key.getName().equals("LIST")) {
					codes = tcpHandler.buildCommand("LIST".getBytes(), new byte[] { 13 });
				} else if (key.getName().equals("DIR")) {
					codes = tcpHandler.buildCommand("LOAD\"$\",8".getBytes(), new byte[] { 13 });
				} else if (key.getName().equals("LOAD*")) {
					codes = tcpHandler.buildCommand("LOAD\"*\",8".getBytes(), new byte[] { 13 });
				} else {

				}
				sendKeyboardSequence(codes);
			}
		} else if (key.getType().equals("COLOR"))

		{
			byte ba[] = new byte[] {};

			if ((key.getOptionState() & 32) == 32) {
				ba = tcpHandler.buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						NumericConverter.getWord(0xd020), new byte[] { key.getIndex().byteValue() });
			}
			if ((key.getOptionState() & 64) == 64) {
				ba = tcpHandler.buildCommand(ba, NumericConverter.getWord(0xff06), NumericConverter.getWord(3),
						NumericConverter.getWord(0xd021), new byte[] { key.getIndex().byteValue() });
			}
			tcpHandler.write(ba);
		}
	}

	@PreDestroy
	public void preDestroy(MPart part) {
		stopStream(VIDEO_STREAM + AUDIO_STREAM);
		tcpHandler.closeSocket();
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		this.parent = parent;

		tcpHandler = new TcpHandler("10.100.200.201", 64);
		List<PlatformColor> colorList = PlatformFactory.getPlatformColors("C64");
		RGB palette[] = new RGB[colorList.size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = colorList.get(i).getColor().getRGB();
		}

		// parent.setLayout(new GridLayout(1, true));
		parent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				sendKeyboardSequence(new byte[] { (byte) e.character });
			}
		});

		verticalSash = new SashForm(parent, SWT.VERTICAL);
		verticalSash.setSashWidth(4);

		top = new Composite(verticalSash, SWT.NONE);
		bottom = new Composite(verticalSash, SWT.NONE);
		top.setLayout(new GridLayout(1, true));
		top.setBackground(Constants.BLACK);
		bottom.setLayout(new GridLayout(1, true));
		bottom.setBackground(Constants.GREY3);

		imageViewer = createImageViewer(top, new PaletteData(palette));
		imageViewer.setLayoutData(new GridData(GridData.CENTER, GridData.FILL_VERTICAL, true, true));
		virtualKeyboard = new VirtualKeyboard(bottom, 0, colorList);
		virtualKeyboard.addHitKeyListener(this);
		virtualKeyboard.setLayoutData(new GridData(GridData.CENTER, GridData.FILL_VERTICAL, true, true));
		showWithKeyboard(false);
		videoStreamReceiver = new VideoStreamReceiver();
		audioStreamReceiver = new AudioStreamReceiver();
		startStream(VIDEO_STREAM + AUDIO_STREAM);

	}

	private void showWithKeyboard(boolean keyboardEnabled) {
		verticalSash.setWeights(1000, keyboardEnabled ? 350 : 0);
	}

	public ImageViewWidget createImageViewer(Composite parent, PaletteData pd) {
		return new ImageViewWidget(parent, SWT.DOUBLE_BUFFERED, pd);
	}

	public MemoryViewWidget createMemoryImageViewer(Composite parent) {
		memoryViewer = new MemoryViewWidget(parent, SWT.DOUBLE_BUFFERED);
		memoryViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return memoryViewer;
	}

	private void sendKeyboardSequence(byte[] data) {
		if (tcpHandler.openSocket() != -1) {
			tcpHandler.write(tcpHandler.buildCommand(NumericConverter.getWord(0xff03),
					NumericConverter.getWord(data.length), data));
		}
	}

	private void showErrorDialog(String message) {
		MessageDialog.openError(parent.getShell(), "Connection error", message);
	}

}
