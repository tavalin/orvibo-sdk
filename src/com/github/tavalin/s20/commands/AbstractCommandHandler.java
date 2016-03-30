package com.github.tavalin.s20.commands;

import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.protocol.Message;
import com.github.tavalin.s20.utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandHandler.
 */
public abstract class AbstractCommandHandler {

    /** The client. */
    private S20Client client;

    /** The device start. */
    // default position for deviceId data. deviceId is 6 bytes long.
    private static final int DEVICE_START = 0;

    /** The device id length. */
    private static final int DEVICE_ID_LENGTH = 6;

    /** The map. */
    private static Map<Command, AbstractCommandHandler> map = new HashMap<Command, AbstractCommandHandler>();
    
    /**
     * Instantiates a new command handler.
     *
     * @param client the client
     */
    public AbstractCommandHandler(S20Client client) {
        this.client = client;
    }

    static {
        try {
            map.put(Command.GLOBAL_DISCOVERY, new GlobalDiscoveryHandler(S20Client.getInstance()));
            map.put(Command.LOCAL_DISCOVERY, new LocalDiscoveryHandler(S20Client.getInstance()));
            map.put(Command.POWER, new PowerHandler(S20Client.getInstance()));
        } catch (SocketException e) {
            LoggerFactory.getLogger(AbstractCommandHandler.class).error("Could not create command map: {}", e.getMessage());
        }
    }

    /**
     * Gets the handler.
     *
     * @param command the command
     * @return the handler
     */
    // Public methods
    public static AbstractCommandHandler getHandler(final Command command) {
        return map.get(command);
    }

    /**
     * Gets the client.
     *
     * @return the client
     */
    public S20Client getClient() {
        return client;
    }

    /**
     * Gets the device start.
     *
     * @return the device start
     */
    protected int getDeviceStart() {
        return DEVICE_START;
    }

    /**
     * Gets the device end.
     *
     * @return the device end
     */
    protected int getDeviceEnd() {
        return getDeviceStart() + DEVICE_ID_LENGTH;
    }

    /**
     * Gets the device id.
     *
     * @param payload the payload
     * @return the device id
     */
    public String getDeviceId(final byte[] payload) {
        String deviceId = "";
        final int start = getDeviceStart();
        final int end = getDeviceEnd();
        if (end < payload.length) {
            final byte[] deviceIdBytes = Arrays.copyOfRange(payload, start, end);
            deviceId = Utils.toHexString(deviceIdBytes);
            final Logger logger = getLogger();
            logger.debug("Extracted deviceId as '{}'", deviceId);
        }
        return deviceId;
    }

    /**
     * Creates the message.
     *
     * @param socket the socket
     * @return the message
     */
    // Abstract methods
    public abstract Message createMessage(Socket socket);

    /**
     * Handle incoming.
     * 
     * @param message the message
     */
    public abstract void handleIncoming(Message message);

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    protected abstract Logger getLogger();

}