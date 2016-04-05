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
    private int DEVICE_START = 0;
    private int POWER_BYTE_POS = 35;

    public LocalDiscoveryHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s, PowerState state) {
        final String deviceId = s.getDeviceId();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
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
        logger.debug("Handling incoming message");
        Socket socket = getSocket(message);
        updatePowerState(socket, message);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    protected int getDeviceStart() {
        return DEVICE_START;
    }

    @Override
    protected int getStateByte() {
        return POWER_BYTE_POS;
    }
}
