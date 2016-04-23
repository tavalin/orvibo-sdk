/*
 * 
 */
package com.github.tavalin.s20;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.commands.Command;
import com.github.tavalin.s20.commands.AbstractCommandHandler;
import com.github.tavalin.s20.network.TransportManager;
import com.github.tavalin.s20.protocol.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class S20Client.
 */

public class S20Client extends OrviboDevice {

    /** The discovery listeners. */
    private final List<SocketDiscoveryListener> discoveryListeners = new ArrayList<SocketDiscoveryListener>();

    /** The instance. */
    private static S20Client instance;

    /** The transport manager. */
    private final TransportManager transportManager;

    /** The all sockets collection. */
    private final Map<String, Socket> allSockets = new HashMap<String, Socket>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(S20Client.class);

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

    /**
     * Gets the all sockets collection.
     *
     * @return the all sockets collection
     */
    public Map<String, Socket> getAllSockets() {
        synchronized (allSockets) {
            return allSockets;
        }
    }

    /**
     * Gets the single instance of S20Client.
     *
     * @return single instance of S20Client
     * @throws SocketException
     *             the socket exception
     */
    public static synchronized S20Client getInstance() throws SocketException {

        if (instance == null) {
            instance = new S20Client();
            logger.debug("New S20Client instance created.");
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
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return transportManager != null && transportManager.isConnected();
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
        final AbstractCommandHandler handler = AbstractCommandHandler.getHandler(Command.GLOBAL_DISCOVERY);
        final Message message = handler.createMessage(null, null);
        sendMessage(message);
    }


    /**
     * Send message.
     *
     * @param message the message to send
     */
    public void sendMessage(final Message message) {
        transportManager.send(message);
        // transportManager.send(message);
        // transportManager.send(message);
        /*
         * transportManager.send(message);
         * transportManager.send(message);
         * transportManager.send(message);
         * transportManager.send(message);
         * transportManager.send(message);
         * transportManager.send(message);
         */
    }

    /**
     * Socket with device id.
     *
     * @param deviceId
     *            the device id
     * @return the socket
     */
    public Socket socketWithDeviceId(final String deviceId) {
        Map<String, Socket> sockets = getAllSockets();
        Socket socket = sockets.get(deviceId);
        if (socket == null) {
            socket = new Socket();
            socket.setDeviceId(deviceId);
            socket.setNetworkContext(this);
            registerSocket(socket);
        } else {
            logger.debug("Socket {} already discovered.", deviceId);
        }
        return socket;
    }

    /**
     * Register socket.
     *
     * @param socket
     *            the socket
     */
    private void registerSocket(final Socket socket) {
        if (socket != null) {
            final String deviceId = socket.getDeviceId();
            final Map<String, Socket> sockets = getAllSockets();
            sockets.put(deviceId, socket);
            logger.debug("Socket '{}' registered", deviceId);
            notifyDiscoveryListeners(socket);
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
    /*
     * public void forwardMessageToDeviceWithDeviceID(MessageOld message, String deviceID) {
     * Socket socket = getAllSocketsCollection().get(deviceID);
     * if (socket == null) {
     * logger.debug("New socket discovered.");
     * socket = socketWithDeviceId(deviceID);
     * notifyDiscoveryListeners(socket);
     * }
     * socket.handleMessage(message);
     * }
     */

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
    public void addSocketDiscoveryListener(final SocketDiscoveryListener listener) {
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
    public void notifyDiscoveryListeners(final Socket socket) {
        logger.debug("Notifying listeners that a socket has been discovered.");
        for (final SocketDiscoveryListener listener : discoveryListeners) {
            listener.socketDiscovered(socket);
        }
    }

}
