package de.drazil.nerdsuite.imaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import de.drazil.nerdsuite.model.RunObject;
import de.drazil.nerdsuite.model.RunObject.Mode;
import de.drazil.nerdsuite.model.RunObject.Source;
import de.drazil.nerdsuite.network.TcpHandler;
import de.drazil.nerdsuite.util.NumericConverter;

public class AbstractStreamView {
	protected TcpHandler tcpHandler;
	protected final static int VIC_STREAM_START_COMMAND = 0xff20;
	protected final static int VIC_STREAM_STOP_COMMAND = 0xff30;
	protected final static int SID_STREAM_START_COMMAND = 0xff21;
	protected final static int SID_STREAM_STOP_COMMAND = 0xff31;
	protected final static int DEBUG_STREAM_START_COMMAND = 0xff22;
	protected final static int DEBUG_STREAM_STOP_COMMAND = 0xff32;

	protected static final int VIDEO_STREAM = 1;
	protected static final int DEBUG_STREAM = 2;
	protected static final int AUDIO_STREAM = 4;

	

	protected void reset() {
		if (tcpHandler.openSocket() != -1) {
			tcpHandler.write(
					tcpHandler.buildCommand(NumericConverter.getWord(0xff04), new byte[] { (byte) 0x00, (byte) 0x00 }));
		}
	}

	protected void wait(int delay) {
		if (tcpHandler.openSocket() != -1) {
			tcpHandler
					.write(tcpHandler.buildCommand(NumericConverter.getWord(0xff05), NumericConverter.getWord(delay)));
		}
	}

	protected void readMemory(int address, int length) {
		if (tcpHandler.openSocket() != -1) {

			tcpHandler.write(tcpHandler.buildCommand(NumericConverter.getWord(0xff74),
					NumericConverter.getWord(length + 2), tcpHandler.buildCommand(NumericConverter.getWord(address))));
			try {
				InputStream is = tcpHandler.getInputStream();
				TimeUnit.MILLISECONDS.sleep(500);
				int dataAvailable = 0;
				byte data[] = null;
				is.read();
				while ((dataAvailable = is.available()) > 0) {
					data = new byte[dataAvailable];
					is.read(data, 0, dataAvailable);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void writeMemory(int address, byte[] data) {
		if (tcpHandler.openSocket() != -1) {
			tcpHandler.write(tcpHandler.buildCommand(NumericConverter.getWord(0xff06),
					NumericConverter.getWord(data.length + 2), NumericConverter.getWord(address), data));
		}
	}

	protected void handleObject(RunObject runObject) {
		if (tcpHandler.openSocket() != -1) {
			int cmd_dma = 0xff01;
			int cmd_dma_run = 0xff02;
			int cmd_dma_jump = 0xff09;
			int cmd_dma_mount_img = 0xff0a;
			int cmd_dma_run_img = 0xff0b;
			byte[] command = null;
			if (runObject.getSource() == Source.Program) {
				if (runObject.getMode() == Mode.None && runObject.getStartAdress() == -1) {
					command = tcpHandler.buildCommand(NumericConverter.getWord(cmd_dma),
							NumericConverter.getWord(runObject.getPayload().length));
				} else if (runObject.getMode() == Mode.Run && runObject.getStartAdress() == -1) {
					command = tcpHandler.buildCommand(NumericConverter.getWord(cmd_dma_run),
							NumericConverter.getWord(runObject.getPayload().length));
				} else if (runObject.getMode() == Mode.Run && runObject.getStartAdress() != -1) {
					command = tcpHandler.buildCommand(NumericConverter.getWord(cmd_dma_jump),
							NumericConverter.getWord(runObject.getPayload().length + 4),
							NumericConverter.getWord(runObject.getStartAdress()));
				}
			} else {
				byte l[] = NumericConverter.getLongWord(runObject.getPayload().length);
				byte length[] = new byte[] { l[0], l[1], l[2] };
				if (runObject.getMode() == Mode.Run) {
					command = tcpHandler.buildCommand(NumericConverter.getWord(cmd_dma_run_img), length);
				} else {
					command = tcpHandler.buildCommand(NumericConverter.getWord(cmd_dma_mount_img), length);
				}
			}

			tcpHandler.write(command);
			tcpHandler.write(runObject.getPayload());

		}
	}

	protected void startStreamByCommand(int command, int duration, String clientAdress) {
		byte durationArray[] = NumericConverter.getWord(duration);
		byte clientAdressArray[] = clientAdress.getBytes();
		byte length[] = NumericConverter.getWord(durationArray.length + clientAdressArray.length);
		if (tcpHandler.openSocket() != -1) {
			tcpHandler.write(tcpHandler.buildCommand(NumericConverter.getWord(command), length, durationArray,
					clientAdressArray));
		}
	}

	protected void stopStreamByCommand(int command) {
		if (tcpHandler.openSocket() != -1) {
			tcpHandler.write(tcpHandler.buildCommand(NumericConverter.getWord(command),
					new byte[] { (byte) 0x00, (byte) 0x00 }));
		}

	}

}
