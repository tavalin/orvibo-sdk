package com.github.tavalin.s20.commands;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.protocol.Message;
import com.github.tavalin.s20.utils.Utils;

public class SubscribeHandler extends AbstractCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(SubscribeHandler.class);
    private int POWER_BYTE_POS = 17;
    private int RESPONSE_LENGTH = 24;

    public SubscribeHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s, PowerState state) {

        final String deviceId = s.getDeviceId();
        final String reverseId = s.getReverseDeviceId();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] reverseIdBytes = Utils.hexStringToByteArray(reverseId);
        final byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };

        // create command payload
        int bufferLength = deviceIdBytes.length + paddingBytes.length + reverseIdBytes.length + paddingBytes.length;
        ByteBuffer bb = ByteBuffer.allocate(bufferLength);
        bb.put(deviceIdBytes);
        bb.put(paddingBytes);
        bb.put(reverseIdBytes);
        bb.put(paddingBytes);
        byte[] payload = bb.array();

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.SUBSCRIBE);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);

        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));
        return message;
    }

    @Override
    public void handleIncoming(Message message) {
        if (isValidResponse(message)) {
            logger.debug("Handling incoming message");
            Socket socket = getSocket(message);
            updatePowerState(socket, message);
        } else {
            logger.warn("Not valid response.");
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected int getStateByte() {
        return POWER_BYTE_POS;
    }

    @Override
    public boolean isValidResponse(Message message) {
        boolean isValid = false;
        byte[] bytes = message.asBytes();
        isValid = bytes.length == RESPONSE_LENGTH;
        return isValid;
    }

}
