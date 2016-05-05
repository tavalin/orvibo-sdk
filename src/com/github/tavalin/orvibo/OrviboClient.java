/*
 * 
 */
package com.github.tavalin.orvibo;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.network.TransportManager;
import com.github.tavalin.orvibo.protocol.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class S20Client.
 */

public class OrviboClient {

    /** The discovery listeners. */
    private final List<OrviboDiscoveryListener> discoveryListeners = new ArrayList<OrviboDiscoveryListener>();

    /** The instance. */
    private static OrviboClient instance;

    /** The transport manager. */
    private final TransportManager transportManager;

    /** The all devices collection. */
    private final Map<String, OrviboDevice> allDevices = new HashMap<String, OrviboDevice>();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(OrviboClient.class);

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
    public interface OrviboDiscoveryListener {

        /**
         * Socket discovered.
         *
         * @param device
         *            the discovered device
         */
        public void deviceDiscovered(OrviboDevice device);
    }

    public Map<String, OrviboDevice> getAllDevices() {
        synchronized (allDevices) {
            return allDevices;
        }
    }

    /**
     * Gets the single instance of S20Client.
     *
     * @return single instance of S20Client
     * @throws SocketException
     *             the socket exception
     */
    public static synchronized OrviboClient getInstance() throws SocketException {

        if (instance == null) {
            instance = new OrviboClient();
            logger.debug("New OrviboClient instance created.");
        }
        return instance;

    }

    /**
     * Instantiates a new s20 client.
     *
     * @throws SocketException
     *             the socket exception
     */
    public OrviboClient() throws SocketException {
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
        // final AbstractCommandHandler handler = AbstractCommandHandler.getHandler(Command.GLOBAL_DISCOVERY);
        // final Message message = handler.createMessage(null, null);
        final Message message = CommandFactory.createGlobalDiscoveryCommand();
        sendMessage(message);
    }

    /**
     * Send message.
     *
     * @param message the message to send
     */
    public void sendMessage(final Message message) {
        transportManager.send(message);
    }

    /**
     * Socket with device id.
     *
     * @param deviceId
     *            the device id
     * @return the socket
     */
    public Socket socketWithDeviceId(final String deviceId) {
        Map<String, OrviboDevice> allDevices = getAllDevices();
        OrviboDevice socket = allDevices.get(deviceId);
        if (socket == null) {
            socket = new Socket();
            socket.setDeviceId(deviceId);
            socket.setNetworkContext(this);
            registerDevice(socket);
        } else {
            logger.debug("Device {} already discovered.", deviceId);
        }
        return (Socket) socket;
    }

    public AllOne allOneWithDeviceId(final String deviceId) {
        Map<String, OrviboDevice> allDevices = getAllDevices();
        OrviboDevice allOne = allDevices.get(deviceId);
        if (allOne == null) {
            allOne = new AllOne();
            allOne.setDeviceId(deviceId);
            allOne.setNetworkContext(this);
            registerDevice(allOne);
        } else {
            logger.debug("Device {} already discovered.", deviceId);
        }
        return (AllOne) allOne;
    }

    private void registerDevice(final OrviboDevice device) {
        if (device != null) {
            final String deviceId = device.getDeviceId();
            final Map<String, OrviboDevice> devices = getAllDevices();
            devices.put(deviceId, device);
            logger.debug("Device '{}' registered", deviceId);
            notifyDiscoveryListeners(device);
        }
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
    public void addDeviceDiscoveryListener(final OrviboDiscoveryListener listener) {
        if (!discoveryListeners.contains(listener)) {
            discoveryListeners.add(listener);
        }
    }

    /**
     * Removes the all socket discovery listeners.
     */
    public void removeAllDeviceDiscoveryListeners() {
        discoveryListeners.clear();
    }

    /**
     * Removes the socket discovery listener.
     *
     * @param listener
     *            the listener
     */
    public void removeDeviceDiscoveryListener(OrviboDiscoveryListener listener) {
        discoveryListeners.remove(listener);
    }

    /**
     * Notify discovery listeners.
     * 
     * @param device the socket
     */
    public void notifyDiscoveryListeners(final OrviboDevice device) {
        logger.debug("Notifying listeners that a device has been discovered.");
        for (final OrviboDiscoveryListener listener : discoveryListeners) {
            listener.deviceDiscovered(device);
        }
    }

}
