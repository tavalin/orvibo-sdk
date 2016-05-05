package com.github.tavalin.orvibo.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.devices.AllOne;
import com.github.tavalin.orvibo.devices.Socket;
import com.github.tavalin.orvibo.protocol.Message;



public class MessageTest {
    
    private Socket socket;
    private AllOne allone;
    
    @Before
    public void setupMock() {
        socket = mock(Socket.class);
        allone = mock(AllOne.class);
                
        when(socket.getDeviceId()).thenReturn("AABBCCDDEEFF");
        when(allone.getDeviceId()).thenReturn("FFAABBCCDDEE");
    }

    @Test
    public void globalDiscovery() {

        Message message = CommandFactory.createGlobalDiscoveryCommand();
        byte[] bytes = message.asBytes();
        byte[] header = Arrays.copyOfRange(bytes, 0, 2);
        byte[] len = Arrays.copyOfRange(bytes, 2, 4);
        byte[] cmd = Arrays.copyOfRange(bytes, 4, 6);
        
        assertArrayEquals(Message.B_HEADER, header);
        assertArrayEquals(Message.B_GLOBAL, cmd);
        assertSame(Message.MIN_LENGTH, bytes.length);
        assertSame(bytes.length, new BigInteger(len).intValue());
    }

    @Test
    public void test2() {
        System.out.println(socket.getDeviceId());
        System.out.println(allone.getDeviceId());
    }

}
