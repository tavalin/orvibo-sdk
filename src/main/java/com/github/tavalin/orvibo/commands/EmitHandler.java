package com.github.tavalin.orvibo.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.protocol.Message;

public class EmitHandler extends AbstractCommandHandler {
    
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(EmitHandler.class);
    private final static int ALLONE_RESPONSE_LENGTH = 25;

	public EmitHandler(OrviboClient client) {
        super(client);
    }

    @Override
    public boolean isValidResponse(Message message) {
        boolean isValid = false;
        byte[] bytes = message.asBytes();
        isValid = bytes.length == ALLONE_RESPONSE_LENGTH;
        return isValid;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void handleInternal(Message message) {
        logger.debug("Handling emitting response");
    }
}
