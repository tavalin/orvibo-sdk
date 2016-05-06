package com.github.tavalin.orvibo.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.entities.Types.PowerState;
import com.github.tavalin.orvibo.protocol.Message;

public class OutboundMessageTest {

    private Socket socket;
    private AllOne allone;

    @Before
    public void setupMock() {
        socket = mock(Socket.class);
        allone = mock(AllOne.class);

        when(socket.getDeviceId()).thenReturn("AABBCCDDEEFF");
        when(socket.getReverseDeviceId()).thenReturn("FFEEDDCCBBAA");
        when(allone.getDeviceId()).thenReturn("FFAABBCCDDEE");
        when(allone.getReverseDeviceId()).thenReturn("EEDDCCBBAAFF");
    }

    @Test
    public void globalDiscovery() {
        Message message = CommandFactory.createGlobalDiscoveryCommand();
        byte[] expected = new byte[] { 0x68, 0x64, 0x00, 0x06, 0x71, 0x61 };
        assertArrayEquals(expected, message.asBytes());
    }

    @Test
    public void localDiscovery() {
        Message socketDiscovery = CommandFactory.createLocalDiscoveryCommand(socket);
        Message alloneDiscovery = CommandFactory.createLocalDiscoveryCommand(allone);

        byte[] socketExpected = new byte[] { 0x68, 0x64, 0x00, 0x12, 0x71, 0x67, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] alloneExpected = new byte[] { 0x68, 0x64, 0x00, 0x12, 0x71, 0x67, (byte) 0xFF, (byte) 0xAA, (byte) 0xBB,
                (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };

        assertArrayEquals(socketExpected, socketDiscovery.asBytes());
        assertArrayEquals(alloneExpected, alloneDiscovery.asBytes());
    }

    @Test
    public void subscribe() {
        Message socketDiscovery = CommandFactory.createSubscribeCommand(socket);
        Message alloneDiscovery = CommandFactory.createSubscribeCommand(allone);
        byte[] socketExpected = new byte[] { 0x68, 0x64, 0x00, 0x1e, 0x63, 0x6c, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xFF, (byte) 0xEE,
                (byte) 0xDD, (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] alloneExpected = new byte[] { 0x68, 0x64, 0x00, 0x1e, 0x63, 0x6c, (byte) 0xFF, (byte) 0xAA, (byte) 0xBB,
                (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xEE, (byte) 0xDD,
                (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };

        assertArrayEquals(socketExpected, socketDiscovery.asBytes());
        assertArrayEquals(alloneExpected, alloneDiscovery.asBytes());
    }

    @Test
    public void powerState() {
        Message off = CommandFactory.createPowerCommand(socket, PowerState.OFF);
        Message on = CommandFactory.createPowerCommand(socket, PowerState.ON);

        byte[] offExpected = new byte[] { 0x68, 0x64, 0x00, 0x17, 0x64, 0x63, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x00 };
        byte[] onExpected = new byte[] { 0x68, 0x64, 0x00, 0x17, 0x64, 0x63, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x01 };

        assertArrayEquals(offExpected, off.asBytes());
        assertArrayEquals(onExpected, on.asBytes());
    }

    @Test
    public void learn() {
        Message learn = CommandFactory.createLearnCommand(allone);
        byte[] learnExpected = new byte[] { 0x68, 0x64, 0x00, 0x18, 0x6c, 0x73, (byte) 0xFF, (byte) 0xAA, (byte) 0xBB,
                (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x01, 0x00, 0x00, 0x00, 0x00,
                0x00 };
        assertArrayEquals(learnExpected, learn.asBytes());
    }

    @Test
    public void emit() {
        String testFile = "src/test/resources/test.ir";
        when(allone.getEmitFilename()).thenReturn(testFile);
        try {
            Message emit = CommandFactory.createEmitCommand(allone);

            byte[] expectedEmit = new byte[] { 0x68, 0x64, 0x00, 0x1D, 0x69, 0x63, (byte) 0xFF, (byte) 0xAA,
                    (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x65, 0x00,
                    0x00, 0x00, 0x73, 0x00, 0x00, 0x03, 0x41, 0x42, 0x43 };

            byte[] startEmit = Arrays.copyOfRange(emit.asBytes(), 0, 22);
            byte[] startExpectedEmit = Arrays.copyOfRange(expectedEmit, 0, 22);

            byte[] endEmit = Arrays.copyOfRange(emit.asBytes(), 24, emit.asBytes().length);
            byte[] endExpectedEmit = Arrays.copyOfRange(expectedEmit, 24, expectedEmit.length);
            
            assertArrayEquals(startExpectedEmit, startEmit);
            assertArrayEquals(endExpectedEmit, endEmit);
        } catch (IOException e) {
            fail("Could not open test file " + testFile);
        }
    }
}
