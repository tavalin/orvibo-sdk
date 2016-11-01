package com.github.tavalin.orvibo.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.PowerState;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.messages.MessageUtils;
import com.github.tavalin.orvibo.messages.request.EmitRequest;
import com.github.tavalin.orvibo.messages.request.GlobalDiscoveryRequest;
import com.github.tavalin.orvibo.messages.request.LearnRequest;
import com.github.tavalin.orvibo.messages.request.LocalDiscoveryRequest;
import com.github.tavalin.orvibo.messages.request.PowerStatusRequest;
import com.github.tavalin.orvibo.messages.request.SubscriptionRequest;

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
        GlobalDiscoveryRequest request = new GlobalDiscoveryRequest();
        byte[] actual = MessageUtils.createBytes(request);
        byte[] expected = new byte[] { 0x68, 0x64, 0x00, 0x06, 0x71, 0x61 };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void localDiscovery() {
    	
    	LocalDiscoveryRequest socketDiscovery = new LocalDiscoveryRequest(socket.getDeviceId());
    	LocalDiscoveryRequest alloneDiscovery = new LocalDiscoveryRequest(allone.getDeviceId());
    	
        byte[] socketExpected = new byte[] { 0x68, 0x64, 0x00, 0x12, 0x71, 0x67, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] alloneExpected = new byte[] { 0x68, 0x64, 0x00, 0x12, 0x71, 0x67, (byte) 0xFF, (byte) 0xAA, (byte) 0xBB,
                (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        
        assertArrayEquals(socketExpected, MessageUtils.createBytes(socketDiscovery));
        assertArrayEquals(alloneExpected, MessageUtils.createBytes(alloneDiscovery));
    }

    @Test
    public void subscribe() {
    	
    	SubscriptionRequest socketSubcribe = new SubscriptionRequest(socket.getDeviceId());
    	SubscriptionRequest alloneSubcribe = new SubscriptionRequest(allone.getDeviceId());
    	
    	byte[] socketExpected = new byte[] { 0x68, 0x64, 0x00, 0x1e, 0x63, 0x6c, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xFF, (byte) 0xEE,
                (byte) 0xDD, (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
        byte[] alloneExpected = new byte[] { 0x68, 0x64, 0x00, 0x1e, 0x63, 0x6c, (byte) 0xFF, (byte) 0xAA, (byte) 0xBB,
                (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, (byte) 0xEE, (byte) 0xDD,
                (byte) 0xCC, (byte) 0xBB, (byte) 0xAA, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20 };
    	
        assertArrayEquals(socketExpected, MessageUtils.createBytes(socketSubcribe));
        assertArrayEquals(alloneExpected, MessageUtils.createBytes(alloneSubcribe));
    }

    @Test
    public void powerState() {
    	
    	PowerStatusRequest off = new PowerStatusRequest(socket.getDeviceId(), PowerState.OFF);
    	PowerStatusRequest on = new PowerStatusRequest(socket.getDeviceId(), PowerState.ON);
    	
        byte[] offExpected = new byte[] { 0x68, 0x64, 0x00, 0x17, 0x64, 0x63, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x00 };
        byte[] onExpected = new byte[] { 0x68, 0x64, 0x00, 0x17, 0x64, 0x63, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC,
                (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x01 };
    	
        assertArrayEquals(offExpected, MessageUtils.createBytes(off));
        assertArrayEquals(onExpected, MessageUtils.createBytes(on));
    }

    @Test
    public void learn() {
    	LearnRequest learn = new LearnRequest(allone.getDeviceId());
    	 byte[] learnExpected = new byte[] { 0x68, 0x64, 0x00, 0x18, 0x6c, 0x73, (byte) 0xFF, (byte) 0xAA, (byte) 0xBB,
                 (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x01, 0x00, 0x00, 0x00, 0x00,
                 0x00 };
    	 assertArrayEquals(learnExpected, MessageUtils.createBytes(learn));
    }

    @Test
    public void emit() {
        
        Path testFile = Paths.get("src/test/resources/test.ir");
        try {
            EmitRequest emit = new EmitRequest(allone.getDeviceId(), testFile); //TODO: fix

            byte[] expectedEmit = new byte[] { 0x68, 0x64, 0x00, 0x1D, 0x69, 0x63, (byte) 0xFF, (byte) 0xAA,
                    (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x65, 0x00,
                    0x00, 0x00, 0x73, 0x00, 0x00, 0x03, 0x41, 0x42, 0x43 };

            byte[] startEmit = Arrays.copyOfRange(MessageUtils.createBytes(emit), 0, 22);
            byte[] startExpectedEmit = Arrays.copyOfRange(expectedEmit, 0, 22);

            byte[] endEmit = Arrays.copyOfRange(MessageUtils.createBytes(emit), 24, MessageUtils.createBytes(emit).length);
            byte[] endExpectedEmit = Arrays.copyOfRange(expectedEmit, 24, expectedEmit.length);

            assertArrayEquals(startExpectedEmit, startEmit);
            assertArrayEquals(endExpectedEmit, endEmit);
        } catch (Exception e) {
            fail("Could not open test file " + testFile.getFileName());
        }
        
    }
}
