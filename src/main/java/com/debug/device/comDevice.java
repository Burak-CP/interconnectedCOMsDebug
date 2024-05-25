package com.debug.device;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.debug.utils.logger;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class comDevice {

	private SerialPort port;

	private DataInputStream din;
	private DataOutputStream dout;
	private StringBuffer buffer;

	public comDevice(String portname, Integer Baudrate, Integer StopBits, Integer DataBits, String Parity) {
		try {
			SerialPort[] ports = SerialPort.getCommPorts();
			for (int i = 0; i < ports.length; i++) {
				logger.DebugLogger(comDevice.class, ports[i].getSystemPortName() + ": "
						+ ports[i].getDescriptivePortName() + " - " + ports[i].getPortDescription());
			}

			for (int i = 0; i < ports.length; i++) {
				SerialPort tmpport = ports[i];
				if (portname.equalsIgnoreCase(tmpport.getSystemPortName())) {
					buffer = new StringBuffer();
					port = tmpport;
					logger.DebugLogger(comDevice.class, "...PORT SELECTED...");
				}
			}

			boolean opensuccess = port.openPort();

			logger.DebugLogger(comDevice.class, "Opening port " + port.getSystemPortName() + ": "
					+ port.getDescriptivePortName() + " - " + port.getPortDescription());

			port.setBaudRate(Baudrate);
			port.setNumDataBits(DataBits);
			port.setNumStopBits(StopBits);
			port.setParity(parseParity(Parity));
			port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);

			din = new DataInputStream(port.getInputStream());
			dout = new DataOutputStream(port.getOutputStream());

			port.addDataListener(new SerialPortDataListener() {

				@Override
				public void serialEvent(SerialPortEvent event) {
					if (getListeningEvents() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
						String data = read();
						if (data != null) {
							buffer.append(data);
						}
					}
				}

				@Override
				public int getListeningEvents() {
					return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
				}
			});

		} catch (Exception e) {
			logger.ErrorLogger(comDevice.class, e);
		}
	}

	private int parseParity(String parity) {
		int iparity = 0;
		switch (parity) {
		case "NO_PARITY":
			iparity = 0;
			break;
		case "ODD_PARITY":
			iparity = 1;
			break;
		case "EVEN_PARITY":
			iparity = 2;
			break;
		case "MARK_PARITY":
			iparity = 3;
			break;
		case "SPACE_PARITY":
			iparity = 4;
			break;

		default:
			break;
		}
		return iparity;
	}

	public String getData() {
		if (buffer.length() > 0) {
			String s = buffer.toString();
			buffer.delete(0, buffer.length());
			return s;
		}
		return null;
	}

	public void send(String message) {
		byte[] bytedata = message.getBytes();
		try {
			dout.write(bytedata);
		} catch (IOException e) {
			logger.ErrorLogger(comDevice.class, e);
		}
	}

	public String read() {
		String s = "";
		try {
			int availableByte = port.bytesAvailable();
			if (availableByte <= 0) {
				return null;
			}
			byte[] byteBuffer = new byte[availableByte];
			port.readBytes(byteBuffer, availableByte);
			s = new String(byteBuffer, StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.ErrorLogger(comDevice.class, e);
		}
		return s;
	}

	public synchronized void close() {
		try {
			this.port.closePort();
		} catch (Exception e) {
			logger.DebugLogger(comDevice.class, "[IOT] MQTT disconnect error!");
			logger.ErrorLogger(comDevice.class, e);
		}
	}
}
