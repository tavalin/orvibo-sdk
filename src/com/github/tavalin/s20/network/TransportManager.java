package com.github.tavalin.s20.network;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.entities.internal.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class TransportManager.
 */
public class TransportManager {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(TransportManager.class);
	
	/** The reader. */
	private SocketReader reader;
	
	/** The writer. */
	private SocketWriter writer;
	
	/** The reader thread. */
	private Thread readerThread;
	
	/** The writer thread. */
	private Thread writerThread;
	
	/** The udp socket. */
	private DatagramSocket udpSocket;
	
	/** The broadcast address. */
	private static InetSocketAddress broadcastAddress;
	
	/** The connected. */
	private boolean connected;

	/** The Constant BROADCAST_PORT. */
	public final static int BROADCAST_PORT = 10000;
	
	/** The Constant REMOTE_PORT. */
	public final static int REMOTE_PORT = 10000;
	
	/** The Constant LISTEN_PORT. */
	public final static int LISTEN_PORT = 10000;

	/**
	 * Instantiates a new transport manager.
	 *
	 * @param s20Client the s20 client
	 * @throws SocketException the socket exception
	 */
	public TransportManager(S20Client s20Client) throws SocketException {
		udpSocket = new DatagramSocket(TransportManager.LISTEN_PORT);
		udpSocket.setBroadcast(true);
		udpSocket.setReuseAddress(true);
		writer = new SocketWriter(udpSocket);
		reader = new SocketReader(udpSocket, s20Client);
	}

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Connect.
	 */
	public void connect() {
		if (!isConnected()) {
			readerThread = new Thread(reader);
			writerThread = new Thread(writer);
			writerThread.start();
			readerThread.start();
			connected = true;
		}

	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {
		try {
			if (isConnected()) {
				writerThread.interrupt();
				readerThread.interrupt();
				writerThread.join(5000);
				readerThread.join(5000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			udpSocket.close();
			connected = false;
		}

	}

	/**
	 * Adds the to write queue.
	 *
	 * @param message the message
	 */
	public void addToWriteQueue(Message message) {
		writer.getQueue().add(message);

	}

	/**
	 * Returns the broadcast address that sockets should use.
	 *
	 * @return the broadcast address
	 * @throws SocketException the socket exception
	 */
	public synchronized static InetSocketAddress getBroadcastAddress() throws SocketException {
		if (broadcastAddress == null) {
			broadcastAddress = new InetSocketAddress(getFirstActiveBroadcast(), BROADCAST_PORT);
		}
		return broadcastAddress;
	}

	/**
	 * Gets the peer address.
	 *
	 * @param peerAddress the peer address
	 * @return the peer address
	 */
	// TODO: Maybe unnecessary
	public InetSocketAddress getPeerAddress(InetAddress peerAddress) {
		return new InetSocketAddress(peerAddress, REMOTE_PORT);
	}

	/**
	 * Gets the first active broadcast.
	 *
	 * @return the first active broadcast
	 * @throws SocketException the socket exception
	 */
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

	/**
	 * Gets the first active i pv4 interface.
	 *
	 * @return the first active i pv4 interface
	 * @throws SocketException the socket exception
	 */
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
		logger.debug("Unable to retrieve active network interface.");
		return null;
	}

	/**
	 * Checks if is local address.
	 *
	 * @param address the address
	 * @return true, if is local address
	 * @throws SocketException the socket exception
	 */
	public static boolean isLocalAddress(InetAddress address) throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface iface = networkInterfaces.nextElement();
			if (iface.isUp()) {
				for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
					if (ifaceAddr.getAddress().equals(address)) {
						logger.debug("Address is local address.");
						return true;
					}
				}
			}
		}
		logger.debug("Address is not local address.");
		return false;
	}

}
