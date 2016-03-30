package com.github.tavalin.s20.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.protocol.Message;

public class GlobalDiscoveryHandler extends AbstractCommandHandler {
    
    private final Logger logger = LoggerFactory.getLogger(GlobalDiscoveryHandler.class);
    
    private int DEVICE_START = 1;
    
    public GlobalDiscoveryHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s) {

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.LOCAL_DISCOVERY);
        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));
        return message;
    }

    @Override
    public void handleIncoming(Message m) {
        byte[] payload = m.getCommandPayload();
        logger.debug("Command payload = {}", Message.bb2hex(payload));
        String deviceId = getDeviceId(payload);
        logger.debug("Creating socket '{}'", deviceId);
        getClient().socketWithDeviceId(deviceId);
    }
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
    
    protected int getDeviceStart(){
        return DEVICE_START;
    }

}
