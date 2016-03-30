package com.github.tavalin.s20.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.protocol.Message;

public class SubcribeHandler extends AbstractCommandHandler {
    
    private final Logger logger = LoggerFactory.getLogger(SubcribeHandler.class);

    public SubcribeHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void handleIncoming(Message m) {
        // TODO Auto-generated method stub
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
