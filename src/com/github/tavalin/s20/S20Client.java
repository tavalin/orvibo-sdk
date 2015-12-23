/*
 * 
 */
package com.github.tavalin.s20;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.entities.DeviceMapping;
import com.github.tavalin.s20.entities.internal.Message;
import com.github.tavalin.s20.entities.internal.MessageType;
import com.github.tavalin.s20.network.RoutingTable;
import com.github.tavalin.s20.network.TransportManager;
import com.github.tavalin.s20.socket.Socket;

// TODO: Auto-generated Javadoc
/**
 * The Class S20Client.
 */
public class S20Client {

	/**
	 * The listener interface for receiving socketDiscovery events. The class
	 * that is interested in processing a socketDiscovery event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's <code>addSocketDiscoveryListener
	 * <code> method. When the socketDiscovery event occurs, that object's
	 * appropriate method is invoked.
	 *
	 * @see SocketDiscoveryEvent
	 */
	public interface SocketDiscoveryListener {

		/**
		 * Socket discovered.
		 *
		 * @param socket
		 *            the socket
		 */
		public void socketDiscovered(Socket socket);
	}

	/** The discovery listeners. */
	private ArrayList<SocketDiscoveryListener> discoveryListeners = new ArrayList<SocketDiscoveryListener>();

	/** The instance. */
	private static S20Client instance;

	/** The transport manager. */
	private TransportManager transportManager;

	/** The routing table. */
	private RoutingTable routingTable;

	/** The all sockets collection. */
	private Map<String, Socket> allSocketsCollection = new HashMap<String, Socket>();

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(S20Client.class);

	/**
	 * Gets the all sockets collection.
	 *
	 * @return the all sockets collection
	 */
	public synchronized Map<String, Socket> getAllSocketsCollection() {
		return allSocketsCollection;
	}

	/**
	 * Gets the single instance of S20Client.
	 *
	 * @return single instance of S20Client
	 * @throws SocketException
	 *             the socket exception
	 */
	public static S20Client getInstance() throws SocketException {
		if (instance == null) {
			logger.debug("New S20Client instance created.");
			instance = new S20Client();
		}
		return instance;
	}

	/**
	 * Instantiates a new s20 client.
	 *
	 * @throws SocketException
	 *             the socket exception
	 */
	public S20Client() throws SocketException {
		transportManager = new TransportManager(this);
		routingTable = new RoutingTable();
	}

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return (transportManager != null && transportManager.isConnected());
	}

	/**
	 * Connect.
	 */
	public void connect() {
		if (transportManager != null && !transportManager.isConnected()) {
			logger.debug("Starting connection threads.");
			transportManager.connect();
			logger.debug("Connection complete.");
		}
	}

	/**
	 * Global discovery.
	 */
	public void globalDiscovery() {

		String command = "686400067161";
		Message message = new Message(command);
		message.setMessageType(MessageType.DISCOVERY_REQUEST);
		try {
			logger.debug("Sending global discovery message.");
			sendMessage(message);
		} catch (SocketException e) {
			logger.error("Ooops. Couldn't send message to socket for some reason: " + e.getMessage());
		}
	}

	/**
	 * Send message.
	 *
	 * @param message
	 *            the message
	 * @throws SocketException
	 *             the socket exception
	 */
	public void sendMessage(Message message) throws SocketException {

		DeviceMapping mapping = routingTable.getDeviceMappingForDeviceID(message.getDeviceId());
		if (mapping == null || message.getDeviceId() == null) {
			logger.debug("No routing table entry found, sending message as broadcast.");
			message.setAddress(TransportManager.getBroadcastAddress());
		} else {
			message.setAddress(mapping.getAddress());
		}
		transportManager.addToWriteQueue(message);
	}

	/**
	 * Handle message.
	 *
	 * @param message
	 *            the message
	 */
	public void handleMessage(Message message) {
		routingTable.updateMappingsFromMessage(message);
		forwardMessageToDeviceWithDeviceID(message, message.getDeviceId());
	}

	/**
	 * Socket with device id.
	 *
	 * @param deviceID
	 *            the device id
	 * @return the socket
	 */
	public Socket socketWithDeviceID(String deviceID) {
		Socket socket = new Socket();
		socket.setDeviceID(deviceID);
		socket.setNetworkContext(this);
		registerSocket(socket);
		return socket;
	}

	/**
	 * Register socket.
	 *
	 * @param socket
	 *            the socket
	 */
	private void registerSocket(Socket socket) {
		if (socket != null) {
			String deviceID = socket.getDeviceId();
			getAllSocketsCollection().put(deviceID, socket);
		}
	}

	/**
	 * Forward message to device with device id.
	 *
	 * @param message
	 *            the message
	 * @param deviceID
	 *            the device id
	 */
	public void forwardMessageToDeviceWithDeviceID(Message message, String deviceID) {
		Socket socket = getAllSocketsCollection().get(deviceID);
		if (socket == null) {
			logger.debug("New socket discovered.");
			socket = socketWithDeviceID(deviceID);

			notifyDiscoveryListeners(socket);
		}
		socket.handleMessage(message);
	}

	/**
	 * Disconnect.
	 */
	public void disconnect() {
		if (transportManager != null && transportManager.isConnected()) {
			logger.debug("Starting disconnection.");
			transportManager.disconnect();
			logger.debug("Disconnection complete.");
		}
	}

	/**
	 * Adds the socket discovery listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void addSocketDiscoveryListener(SocketDiscoveryListener listener) {
		if (!discoveryListeners.contains(listener)) {
			discoveryListeners.add(listener);
		}
	}

	/**
	 * Removes the all socket discovery listeners.
	 */
	public void removeAllSocketDiscoveryListeners() {
		discoveryListeners.clear();
	}

	/**
	 * Removes the socket discovery listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void removeSocketDiscoveryListener(SocketDiscoveryListener listener) {
		discoveryListeners.remove(listener);
	}

	/**
	 * Notify discovery listeners.
	 *
	 * @param socket
	 *            the socket
	 */
	public void notifyDiscoveryListeners(Socket socket) {
		logger.debug("Notifying listeners that a socket has been discovered.");
		for (SocketDiscoveryListener aListener : discoveryListeners) {
			aListener.socketDiscovered(socket);
		}
	}

}
