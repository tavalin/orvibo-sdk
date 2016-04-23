package com.github.tavalin.s20.commands;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.protocol.Message;
import com.github.tavalin.s20.utils.Utils;

public class PowerHandler extends AbstractCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(PowerHandler.class);
    private int POWER_BYTE_POS = 16; //
    private int RESPONSE_LENGTH = 23;

    public PowerHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket socket, PowerState state) {

        final String deviceId = socket.getDeviceId();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };
        final byte[] zeroBytes = new byte[] { Message.ZERO, Message.ZERO, Message.ZERO, Message.ZERO };

        // create command payload
        int bufferLength = deviceIdBytes.length + paddingBytes.length + zeroBytes.length + 1;
        ByteBuffer bb = ByteBuffer.allocate(bufferLength);
        bb.put(deviceIdBytes);
        bb.put(paddingBytes);
        bb.put(zeroBytes);
        bb.put(state.getByte());
        byte[] payload = bb.array();

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.POWER_REQUEST);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);
        return message;
    }

    @Override
    public synchronized void handleIncoming(Message message) {
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
