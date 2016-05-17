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
            byte[] data = Arrays.copyOfRange(in, ALLONE_RESPONSE_LENGTH, in.length);
            AllOne allone = (AllOne) device;
            allone.saveLearnedData(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
