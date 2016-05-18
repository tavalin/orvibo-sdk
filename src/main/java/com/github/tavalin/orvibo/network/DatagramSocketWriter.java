package com.github.tavalin.orvibo.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.protocol.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class SocketWriter.
 */
public class DatagramSocketWriter implements Runnable {

	/** The Constant logger. */
	private final Logger logger = LoggerFactory.getLogger(DatagramSocketWriter.class);
    
    /** The datagramSocket. */
    private DatagramSocket datagramSocket;
    
    /** The queue. */
    private BlockingQueue<DatagramPacket> queue = new ArrayBlockingQueue<DatagramPacket>(100);
    
    /** The running. */
    private boolean running;

    /**
     * Instantiates a new socket writer.
     *
     * @param datagramSocket the socket
     */
    public DatagramSocketWriter(DatagramSocket datagramSocket) {
        if (datagramSocket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.datagramSocket = datagramSocket;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        running = true;
        while (running && !Thread.currentThread().isInterrupted()//&& !queue.isEmpty()
                ) {
            try {
                DatagramPacket packet = queue.take();
                datagramSocket.send(packet);
                logger.debug("--> {} - {}", packet.getSocketAddress(), Message.bb2hex(packet.getData()));
                Thread.sleep(50); // 20 bursts of packets per second maximum
            } catch (InterruptedException e) {
                logger.debug("Interrupted.");
                running = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        }
        logger.debug("Run loop ended.");
    }

   
    public synchronized void send(DatagramPacket packet) {
        queue.add(packet);
    }
    
    
 
}
