package com.github.tavalin.orvibo.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.commands.Command;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.utils.Utils;

public class Message {

    private static final Logger logger = LoggerFactory.getLogger(Message.class);
    
    // byte arrays
    public static final byte[] B_HEADER = new byte[] {0x68, 0x64};
    
    
    // commands
    public static final byte[] B_GLOBAL = new byte[] {0x71, 0x61};
    public static final byte[] B_LOCAL = new byte[] {0x71, 0x67};
    

    public static final byte PADDING = 0x20;
    public static final byte ZERO = 0x00;
    public static final short HEADER = 0x6864;
    public static int MAX_LENGTH = 1024;
    public static int MIN_LENGTH = 6;

    private String deviceId = "";
    private Command command = null;

    private short messageLength = -1;
    private byte[] commandPayload;
    public enum MessageDirection {
        INBOUND,
        OUTBOUND;
    }

    /**
     * Indicates whether the message has had a response.
     */
    private boolean isAcknowledged = false;
    /**
     * Indicates the number of retry attempts left
     */
    public int attempts = 3;

    /**
     * Constructor. Creates a new instance of the Message class.
     */
    public Message() {
        logger.trace("Creating empty message");
        commandPayload = new byte[] {};
    }

    /**
     * Constructor. Creates a new instance of the Message class from a
     * specified buffer.
     * 
     * @param buffer
     *            the buffer to create the SerialMessage from.
     */
    public Message(byte[] buffer) throws OrviboException {
        messageLength = (short) buffer.length;
        if (buffer.length < MIN_LENGTH) {
            isAcknowledged = false;
            String err = String.format("Message is invalid. Actual Length = %d, Minimum Length = %d", messageLength,
                    MIN_LENGTH);
            throw new OrviboException(err);
        }
        command = Command.getCommand(Message.getAsShort(buffer, 4, 5));
        
        commandPayload = Arrays.copyOfRange(buffer, 6, messageLength);
        short header = Message.getAsShort(buffer, 0, 1);
        int expectedLength = Message.getAsShort(buffer, 2, 3);

        if (header == HEADER && messageLength == expectedLength) {
            isAcknowledged = true;
            logger.debug("Message is valid.");
        } else {
            isAcknowledged = false;
            String err = String.format("Message is invalid. Actual Length = %d, Expected Length = %d", messageLength,
                    expectedLength);
            throw new OrviboException(err);
        }
    }

    private static short getAsShort(byte[] buffer, int pos1, int pos2) {
        return (short) (((buffer[pos1] & 0xff) << 8) | (buffer[pos2] & 0xff));
    }

    public boolean isAcknowledged() {
        return isAcknowledged;
    }

    public byte[] asBytes() {
        ByteBuffer bb = ByteBuffer.allocate(MAX_LENGTH);
        bb.putShort(HEADER);
        bb.putShort(getMessageLength());
        bb.putShort(command.getCode());
        bb.put(commandPayload);
        bb.flip();
        return Arrays.copyOf(bb.array(), bb.limit());
    }

    public short getMessageLength() {
        short length = (short) ((messageLength == -1) ? (6 + getCommandPayload().length) : messageLength);
        return length ;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public byte[] getCommandPayload() {
        return commandPayload;
    }

    public void setCommandPayload(byte[] commandPayload) {
        this.commandPayload = commandPayload;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeviceId: ");
        sb.append(getDeviceId());
        sb.append("\t");
        sb.append("Command: ");
        
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(getCommand().getCode());
        sb.append(Utils.toPrettyHexString(buffer.array()));
        sb.append("\t");
        sb.append("Payload: ");
        sb.append(Utils.toPrettyHexString(getCommandPayload()));
        return sb.toString();
    }
    
    public boolean isResponse(Message message) {
        if (getCommand().equals(message.getCommand())) {
            return false;
        }
        if (getMessageDirection().equals(message.getMessageDirection())) {
            return false;
        }
        if (!getDeviceId().equals(message.getDeviceId())) {
            return false;
        }
        return true;
    }

    private Object getMessageDirection() {
        // TODO Auto-generated method stub
        return null;
    }

}
