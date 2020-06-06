package de.drazil.nerdsuite.stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import de.drazil.nerdsuite.disassembler.dialect.KickAssemblerDialect;
import de.drazil.nerdsuite.disassembler.platform.C64Platform;
import de.drazil.nerdsuite.disassembler.platform.IPlatform;
import de.drazil.nerdsuite.util.NumericConverter;

public class U64DebugStream extends Thread {

	private DatagramSocket socket;
	private boolean running;
	private byte[] buf = new byte[1444];
	private IPlatform platform = null;

	public U64DebugStream() throws Exception {
		platform = new C64Platform(new KickAssemblerDialect(), false);
		socket = new DatagramSocket(11002);
	}

	@Execute
	public void execute(MPart part, IEventBroker broker) {
		running = true;

		try {
			while (running) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, address, port);

				int seq = NumericConverter.getWordAsInt(buf, 0);

				for (int i = 4; i < 1444; i += 4) {

					int adr = NumericConverter.getWordAsInt(buf, i);
					int data = NumericConverter.getByteAsInt(buf, i + 2);
					int flags = NumericConverter.getByteAsInt(buf, i + 1);

					// if (adr >= 0xc000 && adr <= 0xc020) {

					System.out.printf("S:%04x A:%04x D:%02x F:%02x  \n", seq, adr, data, flags);
					// }
				}
				/*
				 * if (received.equals("end")) { running = false; continue; }
				 * socket.send(packet);
				 */
			}
		} catch (

		IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();
	}

}
