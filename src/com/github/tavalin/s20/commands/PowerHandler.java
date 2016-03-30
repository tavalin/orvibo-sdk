package com.github.tavalin.s20.commands;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.Socket;
import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.protocol.Message;

public class PowerHandler extends AbstractCommandHandler {
    
    private final Logger logger = LoggerFactory.getLogger(PowerHandler.class);
    private int POWER_BYTE_POS = 16; // 

    public PowerHandler(S20Client client) {
        super(client);
    }

    @Override
    public Message createMessage(Socket s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public synchronized void handleIncoming(Message m) {
        byte[] payload = m.getCommandPayload();
        getLogger().debug("Command payload = {}", Message.bb2hex(payload));
        String deviceId = getDeviceId(payload);
        Socket s = getClient().getAllSockets().get(deviceId);
        if (s != null) {
            byte power = payload[POWER_BYTE_POS];
            switch (power) {
            case 0:
                s.powerDidChangeTo(PowerState.OFF);
                break;
            case 1:
                s.powerDidChangeTo(PowerState.ON);
                break;
            default:
                getLogger().warn("Ignoring unexpected data at power byte '{}'", power);
            }
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.putShort(m.getCommand().getCode());
            getLogger().debug(
                    "Tried to process command '{}' but no socket with deviceId '{}' discovered.",
                    Message.bb2hex(buffer.array()), m.getDeviceId());
        }

    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
