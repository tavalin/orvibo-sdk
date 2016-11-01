package com.github.tavalin.orvibo.network.mina;

import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.messages.OrviboMessage;
import com.github.tavalin.orvibo.messages.MessageUtils;

public class MessageDecoder extends ProtocolDecoderAdapter {

    private Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
    private final int MIN_LENGTH = 6;

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        logger.debug(in.getHexDump());
        // TODO: Move checks into create Message
        if (in.remaining() >= MIN_LENGTH) {
            int magic = in.getShort();
            if (magic != MessageUtils.MAGIC) {
                return; // discard
            }
            int msgLength = in.getShort();
            int bufLength = in.limit();
            if (bufLength != msgLength) {
                return; // discard
            }

            int cmdShort = in.getShort();
            Command cmd = Command.getCommand(cmdShort);
            if (cmd == null) {
                return; // discard
            }

            byte[] data = Arrays.copyOfRange(in.array(), 0, in.limit());
            try {
                OrviboMessage message = MessageUtils.createMessage(cmd, data, msgLength);
                out.write(message);
            } catch (OrviboException ex) {
                // discard
            } finally {
                in.position(in.limit()); // TODO: See if there's a better way to mark the IoBuffer as finished with
            }

        }
    }

}
