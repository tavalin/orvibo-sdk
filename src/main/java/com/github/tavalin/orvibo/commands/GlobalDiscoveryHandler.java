package com.github.tavalin.orvibo.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.protocol.Message;

public class GlobalDiscoveryHandler extends AbstractCommandHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(GlobalDiscoveryHandler.class);
    private final static int DEVICE_START = 1;
    private final static int DEVICE_END = 7;
    private final static int ALLONE_RESPONSE_LENGTH = 41;
    private final static int SOCKET_RESPONSE_LENGTH = 42;
    
    public GlobalDiscoveryHandler(OrviboClient client) {
        super(client);
    }

    public void handle(Message message) {
        if(isValidResponse(message)) {
            createDevice(message);
        } else {
            handleInvalidResponse(message);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
    
    protected int getDeviceStart(){
        return DEVICE_START;
    }
    
    protected int getDeviceEnd() {
        return DEVICE_END;
    }

    @Override
    public boolean isValidResponse(Message message) {
        boolean isValid = false;
        byte[] bytes = message.asBytes();
        isValid = (bytes.length == SOCKET_RESPONSE_LENGTH || bytes.length == ALLONE_RESPONSE_LENGTH) ;
        return isValid;
    }

}
