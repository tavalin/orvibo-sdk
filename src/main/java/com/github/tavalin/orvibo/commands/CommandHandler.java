package com.github.tavalin.orvibo.commands;

import com.github.tavalin.orvibo.protocol.Message;

public interface CommandHandler {

    public void handle(Message message);
}
 