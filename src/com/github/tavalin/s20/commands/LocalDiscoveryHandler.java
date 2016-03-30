package com.github.tavalin.s20.commands;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.protocol.Message;
import com.github.tavalin.s20.utils.Utils;

public class LocalDiscoveryHandler extends AbstractCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(LocalDiscoveryHandler.class);
    private int DEVICE_START = 1;

    public LocalDiscoveryHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s) {
        String deviceId = s.getDeviceId();
        
        byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };

        // create command payload
        int bufferLength = deviceIdBytes.length + paddingBytes.length;
        ByteBuffer bb = ByteBuffer.allocate(bufferLength);
        bb.put(deviceIdBytes);
        bb.put(paddingBytes);
        byte[] payload = bb.array();

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.LOCAL_DISCOVERY);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);

        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));

        return message;
    }

    @Override
    public void handleIncoming(Message message) {
        byte[] payload = message.getCommandPayload();
        int pos = payload.length -1;
        //String deviceId = getDeviceId(payload);
        String deviceId = message.getDeviceId();
        Socket s = getClient().getAllSockets().get(deviceId);
        if (s != null) {
            byte power = payload[pos];
            switch (power) {
            case 0:
                s.powerDidChangeTo(PowerState.OFF);
                break;
            case 1:
                s.powerDidChangeTo(PowerState.ON);
                break;
            default:
                getLogger().warn("Ignoring unexpected data at power byte '{}'", power);
            }
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.putShort(message.getCommand().getCode());
            getLogger().debug(
                    "Tried to process command '{}' but no socket with deviceId '{}' discovered.",
                    Message.bb2hex(buffer.array()), message.getDeviceId());
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
    

    
    protected int getDeviceStart(){
        return DEVICE_START;
    }
}
