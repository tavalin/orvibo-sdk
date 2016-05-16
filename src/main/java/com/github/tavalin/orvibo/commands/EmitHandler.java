package com.github.tavalin.orvibo.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.protocol.Message;

public class EmitHandler extends AbstractCommandHandler {
    
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(EmitHandler.class);

	public EmitHandler(OrviboClient client) {
        super(client);
    }

    public void handle(Message message) {
        logger.debug("Handling emitting response");
	}

    @Override
    public boolean isValidResponse(Message message) {
        return true;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }


    @Override
    protected void updatePowerState(Socket socket, Message message) {
        // TODO Auto-generated method stub
        
    }
}
