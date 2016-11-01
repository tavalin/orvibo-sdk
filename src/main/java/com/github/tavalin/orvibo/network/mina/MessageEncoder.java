package com.github.tavalin.orvibo.network.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.messages.OrviboMessage;
import com.github.tavalin.orvibo.messages.MessageUtils;

public class MessageEncoder extends ProtocolEncoderAdapter {

    private Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        if (message instanceof OrviboMessage) {
            byte[] bytes = MessageUtils.createBytes((OrviboMessage) message);
            IoBuffer buf = IoBuffer.wrap(bytes);
            out.write(buf);
            logger.debug(buf.getHexDump());
        }
    }
}
