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
import de.drazil.nerdsuite.model.RunObject;
import de.drazil.nerdsuite.network.TcpHandler;
import de.drazil.nerdsuite.widget.MemoryViewWidget;
import lombok.Getter;
import lombok.Setter;

public class Ultimate64DebugStreamView extends AbstractStreamView {

	private MemoryViewWidget imageViewer;
	private Socket tcpSocket = null;
	private Thread debugThread;
	private DebugStreamReceiver debugStreamReceiver;
	private boolean running = false;
	private int streamingMode = DEBUG_STREAM;// VIDEO_STREAM + AUDIO_STREAM;

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
		startStream(0);

	}

	@Inject
	@Optional
	public void stopStream(@UIEventTopic("StopStream") BrokerObject brokerObject) {
		stopStream();
	}

	private void stopStream() {

		debugStreamReceiver.setRunning(false);
		stopStreamByCommand(DEBUG_STREAM_STOP_COMMAND);
		debugThread = null;
	}

	@Inject
	@Optional
	public void reset(@UIEventTopic("Reset") BrokerObject brokerObject) {
		try {
			stopStream();
			TimeUnit.MILLISECONDS.sleep(100);
			reset();
			TimeUnit.MILLISECONDS.sleep(100);
			startStream(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void startStream(int streamingMode) {
		String targetAdress = "10.100.200.205";
		if (!running) {
			startStreamByCommand(DEBUG_STREAM_START_COMMAND, 0, targetAdress);
			debugThread = new Thread(debugStreamReceiver);
			debugThread.start();
			debugStreamReceiver.setRunning(true);
		}

	}

	private void stopStream(int streamingMode) {

		if ((streamingMode & DEBUG_STREAM) == DEBUG_STREAM && debugStreamReceiver.isRunning()) {
			debugStreamReceiver.setRunning(false);
			stopStreamByCommand(DEBUG_STREAM_STOP_COMMAND);
			debugThread = null;
		}
	}

	@Inject
	@Optional
	public void loadAndRunObject(@UIEventTopic("LoadAndRun") BrokerObject brokerObject) {
		try {
			stopStream(DEBUG_STREAM_STOP_COMMAND);
			TimeUnit.MILLISECONDS.sleep(100);
			RunObject runObject = (RunObject) brokerObject.getTransferObject();
			handleObject(runObject);
			TimeUnit.MILLISECONDS.sleep(2000);
			startStream(DEBUG_STREAM_START_COMMAND);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void preDestroy(MPart part) {
		stopStream();
		tcpHandler.closeSocket();
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout());
		tcpHandler = new TcpHandler("10.100.200.201", 64);
		imageViewer = createImageViewer(parent);
		debugStreamReceiver = new DebugStreamReceiver();
		startStream(DEBUG_STREAM_START_COMMAND);

	}

	public MemoryViewWidget createImageViewer(Composite parent) {
		imageViewer = new MemoryViewWidget(parent, SWT.DOUBLE_BUFFERED);
		imageViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return imageViewer;
	}

}
