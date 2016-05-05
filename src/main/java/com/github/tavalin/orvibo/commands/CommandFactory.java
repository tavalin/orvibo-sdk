package com.github.tavalin.orvibo.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.OrviboDevice;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.entities.Types.PowerState;
import com.github.tavalin.orvibo.protocol.Message;
import com.github.tavalin.orvibo.utils.Utils;
import com.google.common.primitives.Bytes;

public class CommandFactory {

    private final static Logger logger = LoggerFactory.getLogger(CommandFactory.class);

    final static byte[] lr = new byte[] { 0x6C, 0x73 };
    final static byte[] ic = new byte[] { 0x69, 0x63 };

    public static Message createGlobalDiscoveryCommand() {
        Message message = new Message();
        message.setCommand(Command.GLOBAL_DISCOVERY);
        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));
        return message;
    }

    public static Message createLocalDiscoveryCommand(OrviboDevice device) {
        final String deviceId = device.getDeviceId();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };

        // create command payload
        byte[] payload = Bytes.concat(deviceIdBytes, paddingBytes);

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.LOCAL_DISCOVERY);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);

        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));

        return message;
    }

    public static Message createSubscribeCommand(OrviboDevice device) {
        final String deviceId = device.getDeviceId();
        final String reverseId = device.getReverseDeviceId();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] reverseIdBytes = Utils.hexStringToByteArray(reverseId);
        final byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };

        // create command payload

        byte[] payload = Bytes.concat(deviceIdBytes, paddingBytes, reverseIdBytes, paddingBytes);

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.SUBSCRIBE);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);

        logger.debug("Constructed message {}", Message.bb2hex(message.asBytes()));
        return message;
    }

    public static Message createPowerCommand(Socket device, PowerState state) {
        final String deviceId = device.getDeviceId();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] paddingBytes = new byte[] { Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };
        final byte[] zeroBytes = new byte[] { Message.ZERO, Message.ZERO, Message.ZERO, Message.ZERO };

        // create command payload
        byte[] payload = Bytes.concat(deviceIdBytes, paddingBytes, zeroBytes, new byte[] { state.getByte() });

        // Construct message object
        Message message = new Message();
        message.setCommand(Command.POWER_REQUEST);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);
        return message;
    }

    public static Message createLearnCommand(AllOne device) {
        
        final String deviceId = device.getDeviceId();
        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] paddingBytes = new byte[]{ Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };
        
       // create command payload
        byte[] payload = Bytes.concat(deviceIdBytes, paddingBytes, new byte[] { 0x01,0x00,0x00,0x00,0x00,0x00});
        
        // Construct message object
        Message message = new Message();
        message.setCommand(Command.LEARN_REQUEST);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);

        return message;
    }

    public static Message createEmitCommand(AllOne device) throws IOException {

        final String deviceId = device.getDeviceId();
        final String file = device.getEmitFilename();

        final byte[] deviceIdBytes = Utils.hexStringToByteArray(deviceId);
        final byte[] paddingBytes = new byte[]{ Message.PADDING, Message.PADDING, Message.PADDING, Message.PADDING,
                Message.PADDING, Message.PADDING };
        final byte[] unknown = new byte[] { 0x65, 0x00, 0x00, 0x00 };
        final byte[] randoms = new byte[] { randomByte(), randomByte() };
        Path path = Paths.get(file);
        byte[] fileBuffer = Files.readAllBytes(path);

        // create command payload
        byte[] payload = Bytes.concat(deviceIdBytes, paddingBytes, unknown, randoms, fileBuffer);
        
        // Construct message object
        Message message = new Message();
        message.setCommand(Command.EMIT_REQUEST);
        message.setDeviceId(deviceId);
        message.setCommandPayload(payload);
        return message;
 
    }

    private static byte randomByte() {
        return (byte) (256 * Math.random());
    }

}
