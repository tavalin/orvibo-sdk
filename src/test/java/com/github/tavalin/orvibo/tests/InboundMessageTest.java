package com.github.tavalin.orvibo.tests;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tavalin.orvibo.commands.AbstractCommandHandler;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.protocol.InvalidMessageException;
import com.github.tavalin.orvibo.protocol.Message;

public class InboundMessageTest {


    @Before
    public void setup() throws SocketException {

    }

    @Test
    public void localDiscovery() throws UnknownHostException, InvalidMessageException, OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x29, 0x71, 0x67, 0x00, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x50, (byte) 0xE1, 0x72, 0x23, (byte) 0xCF, (byte) 0xAC, 0x20,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x49, 0x52, 0x44, 0x30, 0x31, 0x33, (byte) 0xC3, (byte) 0xB4, (byte) 0xE5,
                (byte) 0xDA };
        Message message = new Message(buf);
        AbstractCommandHandler handler = AbstractCommandHandler.getHandler(message.getCommand());
        handler.handle(message);
    }
    
    @Test
    public void validGlobalDiscovery() throws UnknownHostException, InvalidMessageException, OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x29, 0x71, 0x61, 0x00, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x50, (byte) 0xE1, 0x72, 0x23, (byte) 0xCF, (byte) 0xAC, 0x20,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x49, 0x52, 0x44, 0x30, 0x31, 0x33, (byte) 0xC3, (byte) 0xB4, (byte) 0xE5,
                (byte) 0xDA };
        Message message = new Message(buf);
        AbstractCommandHandler handler = AbstractCommandHandler.getHandler(message.getCommand());
        handler.handle(message);
    }
    
    @Test(expected=OrviboException.class)
    public void invalidGlobalDiscovery() throws UnknownHostException, InvalidMessageException, OrviboException {
        byte[] buf = { 0x68, 0x64, 0x00, 0x30, 0x71, 0x61, 0x00, (byte) 0xAC, (byte) 0xCF, 0x23, 0x72, (byte) 0xE1,
                0x50, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x50, (byte) 0xE1, 0x72, 0x23, (byte) 0xCF, (byte) 0xAC, 0x20,
                0x20, 0x20, 0x20, 0x20, 0x20, 0x49, 0x52, 0x44, 0x30, 0x31, 0x33, (byte) 0xC3, (byte) 0xB4, (byte) 0xE5, (byte)0xDA, (byte)0xD8 };
        Message message = new Message(buf);
        AbstractCommandHandler handler = AbstractCommandHandler.getHandler(message.getCommand());
        handler.handle(message);
    }

    @After
    public void cleanUp() {

    }



}
