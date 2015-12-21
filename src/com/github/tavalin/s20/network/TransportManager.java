package com.github.tavalin.s20.network;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.entities.internal.Message;

public class TransportManager {

	private SocketReader reader;
	private SocketWriter writer;
	private Thread readerThread;
	private Thread writerThread;
	private DatagramSocket udpSocket;
	private static InetSocketAddress broadcastAddress;
	private boolean connected;

	public final static int BROADCAST_PORT = 10000;
	public final static int REMOTE_PORT = 10000;
	public final static int LISTEN_PORT = 10000;

	public TransportManager(S20Client s20Client) throws SocketException {
		udpSocket = new DatagramSocket(TransportManager.LISTEN_PORT);
		udpSocket.setBroadcast(true);
		udpSocket.setReuseAddress(true);
		writer = new SocketWriter(udpSocket);
		reader = new SocketReader(udpSocket, s20Client);
	}

	public boolean isConnected() {
		return connected;
	}

	public void connect() {
		if (!isConnected()) {
			readerThread = new Thread(reader);
			writerThread = new Thread(writer);
			writerThread.start();
			readerThread.start();
			connected = true;
		}

	}

	public void disconnect() {
		try {
			if (isConnected()) {
				writerThread.interrupt();
				readerThread.interrupt();
				writerThread.join();
				readerThread.join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			udpSocket.close();
			connected = false;
		}

	}

	public void addToWriteQueue(Message message) {
		writer.getQueue().add(message);

	}

	/**
	 * Returns the broadcast address that sockets should use.
	 */
	public synchronized static InetSocketAddress getBroadcastAddress() throws SocketException {
		if (broadcastAddress == null) {
			broadcastAddress = new InetSocketAddress(getFirstActiveBroadcast(), BROADCAST_PORT);
		}
		return broadcastAddress;
	}

	// TODO: Maybe unnecessary
	public InetSocketAddress getPeerAddress(InetAddress peerAddress) {
		return new InetSocketAddress(peerAddress, REMOTE_PORT);
	}

	private static InetAddress getFirstActiveBroadcast() throws SocketException {
		NetworkInterface iface = getFirstActiveIPv4Interface();

		if (iface != null) {
			for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
				InetAddress addr = ifaceAddr.getAddress();
				if (addr instanceof Inet4Address) {
					return ifaceAddr.getBroadcast();
				}
			}
		}

		return null;
	}

	private static NetworkInterface getFirstActiveIPv4Interface() throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface iface = networkInterfaces.nextElement();
			if (iface.isUp() && !iface.isLoopback()) {
				for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
					if (ifaceAddr.getAddress() instanceof Inet4Address) {
						return iface;
					}
				}
			}
		}
		return null;
	}

	public static boolean isLocalAddress(InetAddress address) throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface iface = networkInterfaces.nextElement();
			if (iface.isUp()) {
				for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
					if (ifaceAddr.getAddress().equals(address)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
