package com.github.tavalin.orvibo.commands;

import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.entities.Types.PowerState;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.protocol.Message;
import com.github.tavalin.orvibo.utils.Utils;
import com.google.common.primitives.Bytes;


// TODO: Auto-generated Javadoc
/**
 * The Class CommandHandler.
 */
public abstract class AbstractCommandHandler  {

    /** The client. */
    private OrviboClient client;

    /** The device start. */
    // default position for deviceId data. deviceId is 6 bytes long.
    private static final int DEVICE_START = 0;
    private static final int DEVICE_END = 6;


    /** The device id length. */
    //private static final int DEVICE_ID_LENGTH = 6;

    /** The map. */
    private static Map<Command, AbstractCommandHandler> map = new HashMap<Command, AbstractCommandHandler>();
    
    private static final byte[] SOC = new byte[] { 0x53, 0x4F, 0x43 };
    private static final byte[] IRD = new byte[] { 0x49, 0x52, 0x44 };

    /**
     * Instantiates a new command handler.
     *
     * @param client the client
     */
    public AbstractCommandHandler(OrviboClient client) {
        setClient(client);
    }

    static {
        
        try {
            map.put(Command.GLOBAL_DISCOVERY, new GlobalDiscoveryHandler(OrviboClient.getInstance()));
            map.put(Command.LOCAL_DISCOVERY, new LocalDiscoveryHandler(OrviboClient.getInstance()));
            map.put(Command.POWER, new PowerHandler(OrviboClient.getInstance()));
            map.put(Command.SUBSCRIBE, new SubscribeHandler(OrviboClient.getInstance()));
            map.put(Command.LEARN, new LearnHandler(OrviboClient.getInstance()));
            map.put(Command.EMIT, new EmitHandler(OrviboClient.getInstance()));
        } catch (SocketException e) {
            LoggerFactory.getLogger(AbstractCommandHandler.class).error("Could not create command map: {}",
                    e.getMessage());
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
    public OrviboClient getClient() {
        return client;
    }
    

    public void setClient(OrviboClient client) {
        this.client = client;
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
        return DEVICE_END;
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
    
    protected OrviboDevice getDevice(Message message) {
        byte[] payload = message.getCommandPayload();
        String deviceId = getDeviceId(payload);
        return getDevice(deviceId);
    }
    
    protected OrviboDevice getDevice(String deviceId) {
        OrviboClient client = getClient();
        Map<String,OrviboDevice> devices = client.getAllDevices();
        return devices.get(deviceId);
    }

    protected void createDevice(Message message) {
        byte[] payload = message.getCommandPayload();
        if (Bytes.indexOf(payload, SOC) > 0) {
            createSocket(message);
        } else if (Bytes.indexOf(payload, IRD) > 0) {
            createAllOne(message);
        } else {
            getLogger().warn("Unknown device type");
        }
    }
    

    private void createSocket(Message message) {
        byte[] payload = message.getCommandPayload();
        String deviceId = getDeviceId(payload);
        getLogger().debug("Creating socket '{}'", deviceId);
        OrviboClient client = getClient();
        Socket socket = client.socketWithDeviceId(deviceId);
        updatePowerState(socket, message);
    }

    private void createAllOne(Message message) {
        byte[] payload = message.getCommandPayload();
        String deviceId = getDeviceId(payload);
        getLogger().debug("Creating AllOne '{}'", deviceId);
        OrviboClient client = getClient();
        client.allOneWithDeviceId(deviceId);
    }
   
    /**
     * Handle incoming.
     * 
     * @param message the message
     * @throws OrviboException 
     */
    public void handle(Message message) throws OrviboException {
        if(isValidResponse(message)) {
            handleInternal(message);
        } else {
            handleInvalidResponse(message);
        }
    }
    
    public abstract boolean isValidResponse(Message message);
    
    protected abstract void handleInternal(Message message);

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    protected abstract Logger getLogger();
    
    protected void updatePowerState(Socket socket, Message message) {
        PowerState state = getPowerState(message);
        socket.powerDidChangeTo(state);
    }
    
    protected PowerState getPowerState(Message message) {
        PowerState state = null;
        byte[] payload = message.getCommandPayload();
        int pos = payload.length -1;
        if (payload[pos] == PowerState.ON.getByte()) {
            state = PowerState.ON;
        } else if (payload[pos] == PowerState.OFF.getByte()) {
            state = PowerState.OFF; 
        }
        return state;
    }
    
    protected void handleInvalidResponse(Message message) throws OrviboException {
        //Logger logger = getLogger();
        //logger.warn("Not valid response: " + Message.bb2hex(message.asBytes()));
        throw new OrviboException("Not valid response: " + Message.bb2hex(message.asBytes()));
    }
    
    


    






    

}