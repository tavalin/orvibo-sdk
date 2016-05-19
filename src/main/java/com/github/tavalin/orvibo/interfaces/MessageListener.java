package com.github.tavalin.orvibo.interfaces;

import java.net.InetAddress;

import com.github.tavalin.orvibo.protocol.Message;

public interface MessageListener {
    
    public void messageReceived(InetAddress remote, Message message);

}
