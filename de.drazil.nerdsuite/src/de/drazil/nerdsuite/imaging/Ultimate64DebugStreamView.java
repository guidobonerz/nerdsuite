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

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.handler.BrokerObject;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.MemoryViewWidget;
import lombok.Getter;
import lombok.Setter;

public class Ultimate64DebugStreamView {

	private MemoryViewWidget imageViewer;
	private Socket tcpSocket = null;
	private Thread debugThread;
	private DebugStreamReceiver debugStreamReceiver;
	private boolean running = false;

	public Ultimate64DebugStreamView() {

	}

	public class DebugStreamReceiver implements Runnable {

		private byte[] buf = new byte[1444];
		private int[] mem = new int[0x10000];
		@Setter
		@Getter
		private boolean running = false;
		private DatagramSocket socket;

		public synchronized void run() {
			try {
				socket = new DatagramSocket(11002);
				while (socket != null && running) {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
					InetAddress address = packet.getAddress();
					int port = packet.getPort();
					packet = new DatagramPacket(buf, buf.length, address, port);

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {

							for (int i = 4; i < 1444; i += 4) {
								int adr = ((int) ((buf[i + 1] << 8) | (buf[i + 0] & 0xff)) & 0xffff);
								int data = ((int) (buf[i + 2] & 0xff));
								int flags = ((int) (buf[i + 3] & 0xff));

								if (mem[adr] != data) {

									mem[adr] = data;
									imageViewer.setByte(adr, data, (flags & 1) == 1);
								}
							}
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
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
			startDebugStream();

			debugThread = new Thread(debugStreamReceiver);
			debugThread.start();
			debugStreamReceiver.setRunning(true);
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
			debugStreamReceiver.setRunning(false);
			stopDebugStream();
			debugThread = null;
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
	public void virtualKeyboard(@UIEventTopic("VirtualKeyboard") BrokerObject brokerObject) {
		sendKeyboardSequence("A\n".getBytes());
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
		parent.setLayout(new GridLayout());
		imageViewer = createImageViewer(parent);
		debugStreamReceiver = new DebugStreamReceiver();
		startStream();

	}

	public MemoryViewWidget createImageViewer(Composite parent) {
		imageViewer = new MemoryViewWidget(parent, SWT.DOUBLE_BUFFERED);
		imageViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return imageViewer;
	}

	private void startDebugStream() {
		openSocket();
		try {
			tcpSocket.getOutputStream().write(buildCommand(NumericConverter.getWord(0xff22),
					new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void stopDebugStream() {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff32), new byte[] { (byte) 0x00, (byte) 0x00 }));
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

	private void sendKeyboardSequence(byte[] data) {
		openSocket();
		try {
			tcpSocket.getOutputStream()
					.write(buildCommand(NumericConverter.getWord(0xff03), new byte[] { (byte) 0x00, (byte) 0x00 }));
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
}
