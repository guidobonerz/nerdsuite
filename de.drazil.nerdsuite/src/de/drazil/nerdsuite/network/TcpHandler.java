package de.drazil.nerdsuite.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import lombok.Data;

public class TcpHandler {

	private Socket tcpSocket = null;
	private String ip;
	private int port;

	@Data
	public static class Response {
		private int status;
		private byte[] result;
	}

	public TcpHandler(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public int openSocket() {
		SocketAddress socketAddress = new InetSocketAddress(ip, port);
		int returnValue = 0;
		try {
			if (tcpSocket == null) {
				tcpSocket = new Socket();
				tcpSocket.connect(socketAddress, 2000);
			}
		} catch (IOException e) {
			e.printStackTrace();
			returnValue = -1;
		}
		return returnValue;
	}

	public InputStream getInputStream() throws IOException {
		return tcpSocket.getInputStream();
	}

	public int closeSocket() {
		int returnValue = 0;
		try {
			if (tcpSocket != null && tcpSocket.isConnected()) {
				tcpSocket.close();
				tcpSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			returnValue = -1;
		}
		return returnValue;
	}

	public Response write(byte[] data) {
		Response response = new Response();
		response.setStatus(0);

		try {
			tcpSocket.getOutputStream().write(data);
			InputStream is = tcpSocket.getInputStream();
			int bytesToRead = is.available();
			byte result[] = new byte[bytesToRead];
			is.read(result);
			response.setResult(result);
			System.out.println(new String(result));
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(-1);
		}
		return response;
	}

	public byte[] buildCommand(byte[]... data) {
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
