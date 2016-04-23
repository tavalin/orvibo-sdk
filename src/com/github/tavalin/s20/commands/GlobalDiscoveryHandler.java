package com.github.tavalin.s20.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.protocol.Message;

public class GlobalDiscoveryHandler extends AbstractCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalDiscoveryHandler.class);

    private int DEVICE_START = 1;
    private int POWER_STATE_POS = 35;
    private int RESPONSE_LENGTH = 42;

    public GlobalDiscoveryHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s, PowerState state) {

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.GLOBAL_DISCOVERY);
        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));
        return message;
    }

    @Override
    public void handleIncoming(Message message) {
        if (isValidResponse(message)) {
            byte[] payload = message.getCommandPayload();
            logger.debug("Command payload = {}", Message.bb2hex(payload));
            String deviceId = getDeviceId(payload);
            logger.debug("Creating socket '{}'", deviceId);
            S20Client client = getClient();
            Socket socket = client.socketWithDeviceId(deviceId);
            updatePowerState(socket, message);
        } else {
            logger.warn("Not valid response.");
        }
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
        return POWER_STATE_POS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.tavalin.s20.commands.AbstractCommandHandler#isValidResponse(com.github.tavalin.s20.protocol.Message)
     */
    @Override
    public boolean isValidResponse(Message message) {
        boolean isValid = false;
        byte[] bytes = message.asBytes();
        isValid = bytes.length == RESPONSE_LENGTH;
        return isValid;
    }

}
