package com.github.tavalin.orvibo.messages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import com.github.tavalin.orvibo.devices.DeviceType;
import com.github.tavalin.orvibo.devices.PowerState;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.messages.request.GlobalDiscoveryRequest;
import com.github.tavalin.orvibo.messages.request.LocalDiscoveryRequest;
import com.github.tavalin.orvibo.messages.response.GlobalDiscoveryResponse;
import com.github.tavalin.orvibo.messages.response.LocalDiscoveryResponse;
import com.github.tavalin.orvibo.network.mina.Command;
import com.google.common.primitives.Bytes;

public class MessageUtils {

    // private Logger logger = LoggerFactory.getLogger(MessageUtils.class);

    public static final int MAGIC = 0x6864;
    private static final byte[] SOC = new byte[] { 0x53, 0x4F, 0x43 };
    private static final byte[] IRD = new byte[] { 0x49, 0x52, 0x44 };

    public static OrviboMessage createMessage(Command cmd, byte[] data, int length) throws OrviboException {
        
        String deviceId;
        DeviceType deviceType;
        PowerState powerState;
        
        switch (cmd) {
            case GLOBAL_DISCOVERY:
                GlobalDiscoveryResponse global = new GlobalDiscoveryResponse();
                deviceId = getDeviceId(cmd, data, length);
                deviceType = getDeviceType(cmd, data, length);
                powerState = getPowerState(cmd, data, length);
                global.setDeviceId(deviceId);
                global.setDeviceType(deviceType);
                global.setPowerState(powerState);
                return global;
            case LOCAL_DISCOVERY:
                LocalDiscoveryResponse message = new LocalDiscoveryResponse();
                deviceId = getDeviceId(cmd, data, length);
                powerState = getPowerState(cmd, data, length);
                message.setDeviceId(deviceId);
                message.setPowerState(powerState);
                return message;
        
            default:
        }
        /*
         * if (Command.GLOBAL_DISCOVERY.equals(cmd)) {
         * GlobalDiscoveryResponse message = new GlobalDiscoveryResponse();
         * String deviceId = getDeviceId(cmd, data, length);
         * DeviceType deviceType = getDeviceType(cmd, data, length);
         * PowerState powerState = getPowerState(cmd, data, length);
         * message.setDeviceId(deviceId);
         * message.setDeviceType(deviceType);
         * message.setPowerState(powerState);
         * return message;
         * } else if (Command.LOCAL_DISCOVERY.equals(cmd)) {
         * LocalDiscoveryResponse message = new LocalDiscoveryResponse();
         * String deviceId = getDeviceId(cmd, data, length);
         * PowerState powerState = getPowerState(cmd, data, length);
         * message.setDeviceId(deviceId);
         * message.setPowerState(powerState);
         * return message;
         * }
         * return null;
         */
        return null;
    }

    private static String getDeviceId(Command cmd, byte[] data, int length) {
        byte[] deviceIdBytes;
        String str = "";
        switch (cmd) {
            case GLOBAL_DISCOVERY:
            case LOCAL_DISCOVERY:
                deviceIdBytes = Arrays.copyOfRange(data, 7, 13);
                str = MessageUtils.toHexString(deviceIdBytes);
            default:
        }
        return str;
    }

    private static PowerState getPowerState(Command cmd, byte[] data, int length) throws OrviboException {
        switch (cmd) {
            case GLOBAL_DISCOVERY:
            case LOCAL_DISCOVERY:
                if (length == 42) {
                    byte b = data[length - 1];
                    return b == 1 ? PowerState.ON : PowerState.OFF;
                } else if (length == 41) {
                    return PowerState.UNKNOWN;
                } else {
                    throw new OrviboException("invalid length");
                }
            default:
                return PowerState.UNKNOWN;
        }
    }

    /**
     * Gets the device type.
     *
     * @param cmd the cmd
     * @param data the data
     * @param length the length
     * @return the device type
     */
    private static DeviceType getDeviceType(Command cmd, byte[] data, int length) {
        switch (cmd) {
            case GLOBAL_DISCOVERY:
                if (Bytes.indexOf(data, SOC) >= 0) {
                    return DeviceType.SOCKET;
                } else if (Bytes.indexOf(data, IRD) >= 0) {
                    return DeviceType.ALLONE;
                }
            default:
                return DeviceType.UNKNOWN;
        }
    }

    public static byte[] createBytes(OrviboMessage message) {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        if (message instanceof GlobalDiscoveryRequest) {
            buf.putShort((short) MAGIC);
            buf.putShort((short) 6);
            buf.putShort(Command.GLOBAL_DISCOVERY.getCode());
            buf.flip();
            // return new byte[] { 0x68, 0x64, 0x00, 0x06, 0x71, 0x61 };
            return Arrays.copyOfRange(buf.array(), 0, buf.limit());
        } else if (message instanceof LocalDiscoveryRequest) {
            // 68 64 00 12 71 67 ac cf 23 24 19 c0 20 20 20 20 20 20
            buf.putShort((short) MAGIC);
            buf.putShort((short) 18);
            buf.putShort(Command.LOCAL_DISCOVERY.getCode());
            String deviceId = message.getDeviceId();
            buf.put(hexStringToByteArray(deviceId));
            buf.putShort((short) 0x2020);
            buf.putShort((short) 0x2020);
            buf.putShort((short) 0x2020);
            buf.flip();
            return Arrays.copyOfRange(buf.array(), 0, buf.limit());
        }
        return null;
    }

    ///

    /**
     * Converts a hex string to a byte array. Found at
     * http://stackoverflow.com/questions/140131/convert-a-string-representation
     * -of-a-hex-dump-to-a-byte-array-using-java
     * 
     * @param s
     *            hex string to convert
     * @return byte array of converted string
     */
    public static byte[] hexStringToByteArray(String s) {
        s = s.toUpperCase();
        return DatatypeConverter.parseHexBinary(s);
    }

    /*
     * public static byte[] calculateMessageLenth(String magickey, String lengthPadding, String cmd) {
     * byte[] b = hexStringToByteArray(magickey + lengthPadding + cmd);
     * int len = b.length;
     * return hexStringToByteArray(String.format("%04X", len));
     * }
     */

    public static String toPrettyHexString(byte[] array) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            result.append(String.format("%02X ", array[i]));
        }
        return result.toString();
    }

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array).toUpperCase();

    }
}
