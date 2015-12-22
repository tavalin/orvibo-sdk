package com.github.tavalin.s20.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.entities.internal.Message;
import com.github.tavalin.s20.utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class SocketWriter.
 */
public class SocketWriter implements Runnable {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SocketWriter.class);
    
    /** The socket. */
    private DatagramSocket socket;
    
    /** The queue. */
    private BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(100);
    
    /** The running. */
    private boolean running;

    /**
     * Instantiates a new socket writer.
     *
     * @param socket the socket
     */
    public SocketWriter(DatagramSocket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = socket;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        running = true;
        while (running && !Thread.currentThread().isInterrupted() || !queue.isEmpty()) {
            try {
                Message message = queue.take();
                InetSocketAddress destination = message.getAddress();
                byte[] data = Utils.hexStringToByteArray(message.getMessage());
                DatagramPacket p = new DatagramPacket(data, data.length);
                p.setSocketAddress(destination);
                socket.send(p);
                logger.debug(String.format("%s - %s", p.getSocketAddress(), message.getMessage()));
                Thread.sleep(50); // 20 bursts of packets per second maximum
            } catch (InterruptedException e) {
                // we need to hurry up and finish
                running = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        }
    }

    /**
     * Gets the queue.
     *
     * @return the queue
     */
    public BlockingQueue<Message> getQueue() {
        return queue;
    }
}
