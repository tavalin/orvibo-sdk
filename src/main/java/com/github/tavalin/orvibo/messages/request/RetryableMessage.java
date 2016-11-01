package com.github.tavalin.orvibo.messages.request;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.messages.OrviboMessage;

public class RetryableMessage implements Callable<Boolean> {
    
    private IoSession session;
    private OrviboMessage message;
    private long period;
    private TimeUnit timeUnit;
    private ReadFuture readFuture;
    private Logger logger = LoggerFactory.getLogger(RetryableMessage.class);

    public RetryableMessage(IoSession session, OrviboMessage message, long period, TimeUnit timeUnit) {
        this.session = session;
        this.message = message;
        this.period = period;
        this.timeUnit = timeUnit;
        if (session.getConfig().isUseReadOperation()) {
            readFuture = session.read();
        }

    }

    @Override
    public synchronized Boolean call() throws Exception {
        boolean result = true;
        logger.debug("Sending message to {}", session.toString());
        session.write(message);
        if (session.getConfig().isUseReadOperation()) {
            readFuture.awaitUninterruptibly(period, timeUnit);
            Throwable exception = readFuture.getException();
            Object message = readFuture.getMessage();
            if (exception != null || message == null) {
                // we didn't get a response
                result = false;
                logger.debug("No response");
            } else {
                // we got a response - nothing further to do as IoHandler will
                // handle the message itself
                result = true;
                logger.debug("Response received");
            }
        } else {
            logger.warn("Unable to read session for response");
        }
        return result;

    }


}
