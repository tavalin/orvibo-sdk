package com.github.tavalin.orvibo.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.AbstractCommandHandler;
import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.network.PacketHandler;
import com.github.tavalin.orvibo.protocol.Message;
import com.github.tavalin.orvibo.utils.MessageUtils;

public class InboundMessageTest {

    @Before
    public void setup() throws SocketException {

    }

    @Test
    public void localDiscovery() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x29, 0x71, 0x67, 0x00, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x50, (byte) 0xE1, 0x72, 0x23, (byte) 0xCF, (byte) 0xAC, 0x20,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x49, 0x52, 0x44, 0x30, 0x31, 0x33, (byte) 0xC3, (byte) 0xB4, (byte) 0xE5,
                (byte) 0xDA };
        testHandler(buf);
    }

    @Test
    public void validGlobalDiscovery() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x29, 0x71, 0x61, 0x00, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x50, (byte) 0xE1, 0x72, 0x23, (byte) 0xCF, (byte) 0xAC, 0x20,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x49, 0x52, 0x44, 0x30, 0x31, 0x33, (byte) 0xC3, (byte) 0xB4, (byte) 0xE5,
                (byte) 0xDA };
        testHandler(buf);
    }

    @Test(expected = OrviboException.class)
    public void invalidGlobalDiscovery() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x30, 0x71, 0x61, 0x00, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x50, (byte) 0xE1, 0x72, 0x23, (byte) 0xCF, (byte) 0xAC, 0x20,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x49, 0x52, 0x44, 0x30, 0x31, 0x33, (byte) 0xC3, (byte) 0xB4, (byte) 0xE5,
                (byte) 0xDA, (byte) 0xD8 };
        testHandler(buf);
    }

    @Test
    public void validEmitResponse() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x19, 0x69, 0x63, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1, 0x50,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x01, 0x00, 0x00, 0x00, 0x00, 0x5A, 0x6A };
        testHandler(buf);
    }

    @Test(expected = OrviboException.class)
    public void invalidEmitResponse() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x19, 0x69, 0x63, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1, 0x50,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x01, 0x00, 0x00, 0x00, 0x00, 0x5A, 0x6A, 0x67 };
        testHandler(buf);
    }

    @Test
    public void learnModeConfirm() throws OrviboException {
        byte[] buffer = MessageUtils.hexStringToByteArray("686400186C73ACCF2372E150202020202020010000000000");
        testHandler(buffer);
    }

    @Test
    public void validLearnResponse() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x18, 0x6C, 0x73, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1, 0x50,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00 };
        testHandler(buf);
    }

    @Test
    public void learnIrCodeResponse() throws OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, (byte) 0xBA, 0x6C, 0x73, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x01, 0x00, 0x00, 0x00, 0x00, 0x02, (byte) 0xA0, 0x00, 0x00,
                0x00, 0x00, 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x90, 0x00,
                (byte) 0xC4, 0x22, (byte) 0xB9, 0x11, (byte) 0xFE, 0x01, 0x5E, 0x02, 0x00, 0x02, (byte) 0xC1, 0x06,
                (byte) 0xFF, 0x01, 0x61, 0x02, (byte) 0xFD, 0x01, 0x5E, 0x02, (byte) 0xFF, 0x01, (byte) 0xDA, 0x06,
                (byte) 0xE6, 0x01, 0x5E, 0x02, 0x00, 0x02, (byte) 0xC6, 0x06, (byte) 0xFB, 0x01, (byte) 0xC4, 0x06,
                (byte) 0xFC, 0x01, 0x5E, 0x02, 0x00, 0x02, 0x60, 0x02, (byte) 0xFE, 0x01, (byte) 0xC1, 0x06, 0x00, 0x02,
                (byte) 0xC5, 0x06, (byte) 0xFC, 0x01, 0x5F, 0x02, (byte) 0xFE, 0x01, (byte) 0xC1, 0x06, (byte) 0xFF,
                0x01, (byte) 0xDA, 0x06, (byte) 0xE6, 0x01, 0x5F, 0x02, 0x02, 0x02, (byte) 0xC1, 0x06, (byte) 0xFC,
                0x01, (byte) 0xC2, 0x06, (byte) 0xFF, 0x01, 0x5F, 0x02, (byte) 0xFE, 0x01, (byte) 0xDA, 0x06,
                (byte) 0xE9, 0x01, 0x5B, 0x02, 0x00, 0x02, 0x78, 0x02, (byte) 0xE6, 0x01, (byte) 0xC2, 0x06,
                (byte) 0xFE, 0x01, (byte) 0xC2, 0x06, (byte) 0xFE, 0x01, 0x5D, 0x02, 0x03, 0x02, 0x76, 0x02,
                (byte) 0xE6, 0x01, (byte) 0xDA, 0x06, (byte) 0xE6, 0x01, 0x78, 0x02, (byte) 0xE6, 0x01, (byte) 0xC2,
                0x06, (byte) 0xFF, 0x01, (byte) 0xC4, 0x06, 0x03, 0x02, 0x71, 0x02, (byte) 0xE6, 0x01, 0x77, 0x02,
                (byte) 0xE6, 0x01, 0x04, (byte) 0x9C, (byte) 0xB8, 0x22, 0x18, 0x09, (byte) 0xE4, 0x01, 0x00, 0x00 };
        testHandler(buf);
    }

    @Test
    public void learnIrCodeResponse2() throws OrviboException, SocketException, IOException {
        byte[] buf = MessageUtils.hexStringToByteArray(
                "686400b26c73ACCF2372E1502020202020200000000000029800000000009800000000000000000088001423751138022002330226021e02220237021f02340225021f02210237022002330225021f028b0634027a0637028c061f028c063402790638028b0620028c06340279063702200234027806380220023302790637028d061e0223023602f305b802230236028d061f02220236028c061e0223023602210233027806360221023402770638020000");
        AllOne device = OrviboClient.getInstance().allOneWithDeviceId("ACCF2372E150");
        device.setLearnPath(Files.createTempFile("ircode", ".tmp"));
        testHandler(buf);
    }

    @Test
    public void learnAndEmit() throws IOException, OrviboException {
        byte[] buf = MessageUtils.hexStringToByteArray(
                "686400b26c73ACCF2372E1502020202020200000000000029800000000009800000000000000000088001423751138022002330226021e02220237021f02340225021f02210237022002330225021f028b0634027a0637028c061f028c063402790638028b0620028c06340279063702200234027806380220023302790637028d061e0223023602f305b802230236028d061f02220236028c061e0223023602210233027806360221023402770638020000");
        AllOne device = OrviboClient.getInstance().allOneWithDeviceId("ACCF2372E150");
        device.setLearnPath(Files.createTempFile("ircode", ".tmp"));
        testHandler(buf);

        Message emit = CommandFactory.createEmitCommand(device, device.getLearnPath());

        byte[] learnCode = Arrays.copyOfRange(buf, 26, buf.length);
        byte[] emitCode = Arrays.copyOfRange(emit.asBytes(), 26, emit.asBytes().length);
        assertArrayEquals(learnCode, emitCode);
    }

    @Test
    public void longCommandLength() throws OrviboException {
        // this test should really test that the length bytes equals the actual message length of a known length
        byte[] buf = MessageUtils.hexStringToByteArray(
                "686401526C73ACCF2353041E202020202020010000000002380100000000380100000000000000002801CD008C04D000F302D000F40AD1001505D1001405D1008B04D1007703D000A332CE000504CE00B006CD00F502CD00F502CC000704CB00A105CA00F802C900F702C700FFFF043F0100A8002B04A900B304A8001B03A8001B0BA8003D05A8003D05A800B304B3009603A800CB32A8003A049A001B0BA8005F07A9001903A8002B04A800C405A8001A03A8001803A800FFFF223F0100A8002B04A900B304A9001A03A8001B0BA9003D05A8003D05A900B304A9009F03A900CC32AD002704AD00170BAD005A07AD001503AD002604C500AB05AB001503AE001203AE00FFFF223F0100D0000404D0008B04D000F202D000F30AD0001505D0001505D0008B04D0007803D000A532D0000304D400EF0AD0003707D000F202D0000304D0009C05D000F202D000F002D0000000");
        PacketHandler ph = new PacketHandler(null);
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        ph.packetReceived(dp);
    }

    @After
    public void cleanUp() {

    }

    public void testHandler(byte[] buf) throws OrviboException {
        Message message = new Message(buf);
        AbstractCommandHandler handler = AbstractCommandHandler.getHandler(message.getCommand());
        handler.handle(message);
    }

}
