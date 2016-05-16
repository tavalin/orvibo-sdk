package com.github.tavalin.orvibo.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.DeviceType;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.protocol.Message;

public class SubscribeHandler extends AbstractCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(SubscribeHandler.class);
    private final static int ALLONE_RESPONSE_LENGTH = 23;
    private final static int SOCKET_RESPONSE_LENGTH = 24;

    public SubscribeHandler(OrviboClient client) {
        super(client);
    }


    @Override
    public void handle(Message message) {
        if (isValidResponse(message)) {
            logger.debug("Handling incoming message");
            byte[] payload = message.getCommandPayload();
            String deviceId = getDeviceId(payload);
            OrviboDevice device = getDevice(deviceId);
            if (device == null) {
                createDevice(message);
            } else if (device.getDeviceType() == DeviceType.SOCKET) {
                handleSocket((Socket)device,message);
            } else if (device.getDeviceType() == DeviceType.ALLONE) {
                handleAllOne((AllOne) device,message);
            } else {
                logger.warn("Unknown device type");
            }
        } else {
            handleInvalidResponse(message);
        }
    }
    
    private void handleAllOne(AllOne allOne, Message message) {
        // nothing to do as far as I can tell
    }

    private void handleSocket(Socket socket, Message message) {
        updatePowerState(socket, message);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isValidResponse(Message message) {
        boolean isValid = false;
        byte[] bytes = message.asBytes();
        isValid = (bytes.length == SOCKET_RESPONSE_LENGTH || bytes.length == ALLONE_RESPONSE_LENGTH) ;
        return isValid;
    }


}
