package com.github.tavalin.orvibo.commands;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.protocol.Message;

public class LearnHandler extends AbstractCommandHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(LearnHandler.class);

    public LearnHandler(OrviboClient client) {
        super(client);
    }

    @Override
    public boolean isValidResponse(Message message) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void handle(Message message) {
        if (isValidResponse(message)) {

            try {
                logger.debug("Handling learning response");
                byte[] payload = message.getCommandPayload();
                String deviceId = getDeviceId(payload);
                OrviboDevice device = getDevice(deviceId);
                if (device == null) {
                    return;
                }
                int irStart = 26;

                byte[] in = message.asBytes();
                if (in.length > irStart) {
                    byte[] data = Arrays.copyOfRange(in, irStart, in.length);
                    AllOne allone = (AllOne) device;
                    allone.saveLearnedData(data);
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            } 
        }

    }

    @Override
    protected Logger getLogger() {
        // TODO Auto-generated method stub
        return logger;
    }

    @Override
    protected void updatePowerState(Socket socket, Message message) {
        // TODO Auto-generated method stub

    }

}
