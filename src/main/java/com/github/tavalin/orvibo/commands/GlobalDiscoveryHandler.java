package com.github.tavalin.orvibo.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.entities.Types.PowerState;
import com.github.tavalin.orvibo.protocol.Message;

public class GlobalDiscoveryHandler extends AbstractCommandHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(GlobalDiscoveryHandler.class);
    private int DEVICE_START = 1;
    private int DEVICE_END = 7;
    
    public GlobalDiscoveryHandler(OrviboClient client) {
        super(client);
    }

    public void handle(Message message) {
        createDevice(message);
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
        // TODO Auto-generated method stub
        return true;
    }

    protected void updatePowerState(Socket socket, Message message) {
        PowerState state = getPowerState(message);
        socket.powerDidChangeTo(state);
    }




}
