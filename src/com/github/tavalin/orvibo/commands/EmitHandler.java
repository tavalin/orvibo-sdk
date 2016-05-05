package com.github.tavalin.orvibo.commands;

import org.slf4j.Logger;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.protocol.Message;

public class EmitHandler extends AbstractCommandHandler {

	public EmitHandler(OrviboClient client) {
        super(client);
        // TODO Auto-generated constructor stub
    }

    public void handle(Message message) {
		// TODO Auto-generated method stub
		
	}
//

    @Override
    public boolean isValidResponse(Message message) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Logger getLogger() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    protected void updatePowerState(Socket socket, Message message) {
        // TODO Auto-generated method stub
        
    }
}
