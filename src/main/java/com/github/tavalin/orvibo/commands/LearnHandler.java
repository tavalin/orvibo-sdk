package com.github.tavalin.orvibo.commands;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.protocol.Message;

public class LearnHandler extends AbstractCommandHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(LearnHandler.class);
    private final static int ALLONE_RESPONSE_LENGTH = 24;
    private final static int ALLONE_COMMAND_START = 26;

    public LearnHandler(OrviboClient client) {
        super(client);
    }

    @Override
    public boolean isValidResponse(Message message) {
        boolean isValid = false;
        byte[] bytes = message.asBytes();
        isValid = bytes.length >= ALLONE_RESPONSE_LENGTH;
        return isValid;
    }

    @Override
    protected void handleInternal(Message message) {
        try {
            logger.debug("Handling learning response");
            byte[] payload = message.getCommandPayload();
            String deviceId = getDeviceId(payload);
            OrviboDevice device = getDevice(deviceId);
            if (device == null) {
                return;
            }
            byte[] in = message.asBytes();
            if (in.length >= ALLONE_COMMAND_START) {
                byte[] data = Arrays.copyOfRange(in, ALLONE_COMMAND_START, in.length);
                AllOne allone = (AllOne) device;
                allone.saveLearnedData(data);
                //System.out.println("Memorizzato comando lungo "+in.length);
                allone.setStatus(AllOne.IDLE);
            }else {
                AllOne allone = (AllOne) device;
                allone.setStatus(AllOne.LEARNING);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
