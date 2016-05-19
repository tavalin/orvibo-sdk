package com.github.tavalin.orvibo.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.interfaces.MessageListener;
import com.github.tavalin.orvibo.protocol.Message;
import com.github.tavalin.orvibo.utils.Utils;
import com.google.common.primitives.Bytes;

public class PacketHandler {

    private ByteBuffer byteBuffer = null;
    private static final byte[] HEADER = new byte[] { 0x68, 0x64 };
    private final static int MAX_SIZE = 0xFF;
    /** The listeners. */
    private List<MessageListener> listeners;
    private InetAddress remote;
    public final static int STORED_MESSAGES = 1;
    private CircularFifoQueue<DatagramPacket> previousPackets;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(PacketHandler.class);

    public static void main(String[] args) {
        // byte[][] input = new byte[][] { p1, p2 };
        // byte[] buf = new byte[0];
        InetAddress remote = null;
        try {
            remote = InetAddress.getByName("192.168.0.123");
        } catch (UnknownHostException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        PacketHandler rt;

        rt = new PacketHandler(remote);

        byte[] p1 = new byte[] { 0x68, 0x64, 0x00, 0x10 };
        byte[] p2 = new byte[] { 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F };
        byte[] p3 = new byte[] { 0x0B, 0x0B, 0x0B, 0x0B, 0x0B, 0x0B };

        /*
         * byte[][] input = new byte[][] { { 0x68 }, { 0x64 }, { 0x00 }, { 0x10
         * }, { 0x11 }, {(byte)0xFF} };
         */
        try {
            /*
             * for (byte[] b : input) { rt.handle(b); }
             */
            rt.processPacketBytes(p1);
            rt.processPacketBytes(p2);
            // assertArrayEquals(Bytes.concat(p1, p2), rt.getByteBuffer());
            rt.processPacketBytes(p3);
            // assertArrayEquals(new byte[0], rt.getByteBuffer());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // e.printStackTrace();
        }

        try {
            rt.processPacketBytes(p1);
            rt.processPacketBytes(p2);
        } catch (Exception e) {

        }

    }

    public PacketHandler(InetAddress remote) {
        setRemote(remote);
        clearBuffer();
        listeners = new ArrayList<MessageListener>();
        previousPackets = new CircularFifoQueue<DatagramPacket>(STORED_MESSAGES);
    }

    public InetAddress getRemote() {
        return remote;
    }

    public void setRemote(InetAddress remote) {
        this.remote = remote;
    }

    public synchronized void packetReceived(DatagramPacket packet) throws OrviboException {
            byte[] bytes = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());
            logger.debug("<-- {} - {}", packet.getAddress(), Utils.toPrettyHexString(bytes));
            if (!packetAlreadyReceived(packet)) {
                previousPackets.add(packet);
                processPacketBytes(bytes);
            }
     }

    private int getExpectedLength() {
        byte[] byteBuffer = getByteBuffer();
        return byteBuffer[2] + byteBuffer[3];
    }

    private void clearBuffer() {
        byteBuffer = ByteBuffer.allocate(MAX_SIZE);
    }

    private void updateBuffer(byte[] bytes) throws OrviboException {
        if (byteBuffer.position() + bytes.length > byteBuffer.capacity()) {
            clearAndThrowException(
                    "Buffer full. Discarding current buffer: " + Utils.toPrettyHexString(getByteBuffer()));
        } else {
            byteBuffer.put(bytes);
        }
    }

    private void processPacketBytes(byte[] bytes) throws OrviboException {
        updateBuffer(bytes);
        int bufferPosition = getBufferPosition();
        byte[] bufCopy = Arrays.copyOf(getByteBuffer(), getByteBuffer().length);
        if (bufferPosition > 3) {
            if (bufferContains(HEADER)) {
                int expectedLength = getExpectedLength();
                if (bufferPosition < expectedLength) {
                    // we haven't yet received what we expected so wait for next
                    // packet
                    logger.debug("Waiting for further packets.");
                    return;
                } else if (bufferPosition == expectedLength) {
                    logger.debug("Complete packet received.");
                    clearBuffer();
                    Message message = new Message(bufCopy);
                    notifyListeners(message);
                } else if (bufferPosition > expectedLength) {
                    // somehow we've received more data that expected
                    clearAndThrowException(
                            "Invalid packet size. Discarding current buffer: " + Utils.toPrettyHexString(bufCopy));
                }
            } else {
                // we've got 4 or more bytes and it doesn't have a valid header
                clearAndThrowException(
                        "Invalid packet header. Discarding current buffer: " + Utils.toPrettyHexString(bufCopy));
            }
        }
    }

    private int getBufferPosition() {
        return byteBuffer.position();
    }

    private byte[] getByteBuffer() {
        return Arrays.copyOfRange(byteBuffer.array(), 0, byteBuffer.position());
    }

    private boolean bufferContains(byte[] bytes) {
        return Bytes.indexOf(getByteBuffer(), bytes) >= 0;
    }

    private void clearAndThrowException(String msg) throws OrviboException {
        clearBuffer();
        throw new OrviboException(msg);
    }

    /**
     * Adds the listener.
     *
     * @param listener the listener
     */
    public void addListener(MessageListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Message packet) {
        for (MessageListener listener : listeners) {
            listener.messageReceived(remote, packet);
        }
    }
    
    private boolean packetAlreadyReceived(DatagramPacket newPacket) {
        Iterator<DatagramPacket> it = previousPackets.iterator();
        while (it.hasNext()) {
            DatagramPacket oldPacket = it.next();
            if (Arrays.equals(oldPacket.getData(), newPacket.getData())) {
                return true;
            }
        }
        return false;
    }

}
