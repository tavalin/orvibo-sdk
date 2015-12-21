package com.github.tavalin.s20.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.entities.internal.Message;
import com.github.tavalin.s20.entities.internal.MessageType;
import com.github.tavalin.s20.utils.Utils;

public class SocketReader implements Runnable {

	// private Logger logger = LoggerFactory.getLogger(SocketReader.class);
	private DatagramSocket socket;
	private S20Client networkContext;
	private boolean running = false;

	public SocketReader(DatagramSocket udpSocket, S20Client s20Client) {
		if (udpSocket == null) {
			throw new IllegalArgumentException("Socket cannot be null");
		}
		if (s20Client == null) {
			throw new IllegalArgumentException("Client cannot be null");
		}

		this.socket = udpSocket;
		this.networkContext = s20Client;
	}

	@Override
	public void run() {
		running = true;
		while (running && !Thread.currentThread().isInterrupted()) {
			byte[] buffer = new byte[256];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(p);
				if (!TransportManager.isLocalAddress(p.getAddress())) {
				//if (!p.getAddress().equals(InetAddress.getLocalHost())) {
					byte[] data = Arrays.copyOf(p.getData(), p.getLength());
					String message = Utils.toHexString(data);
					// logger.debug(String.format("Received: %s - %s",
					// p.getSocketAddress(), message));

					// order of message config is important
					System.out.println(String.format("%s %s",p.getSocketAddress(), message));
					Message m = new Message(message);
					MessageType type = Message.getMessageTypeFromMessage(m);
					m.setMessageType(type);
					String deviceId = Message.getDeviceIdFromMessage(m);
					m.setDeviceId(deviceId);
					m.setAddress(new InetSocketAddress(p.getAddress(), TransportManager.REMOTE_PORT));
					//System.out.println(String.format("%s %s %s %s",p.getSocketAddress(), type, deviceId, message));
					

					//System.out.println(String.format("Received: %s - %s", p.getSocketAddress(), message));
					networkContext.handleMessage(m);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
	



}
