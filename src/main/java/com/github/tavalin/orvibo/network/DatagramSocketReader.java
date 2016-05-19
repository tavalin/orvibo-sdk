/*
 * 
 */
package com.github.tavalin.orvibo.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.interfaces.PacketListener;

// TODO: Auto-generated Javadoc
/**
 * The Class DatagramSocketReader.
 */
public class DatagramSocketReader implements Runnable {



    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(DatagramSocketReader.class);

    /** The socket. */
    private DatagramSocket socket;


    /** The running. */
    private boolean running = false;

    /** The listeners. */
    private List<PacketListener> listeners;
    
    public final int BUFFER_SIZE = 1024;

    /**
     * Instantiates a new datagram socket reader.
     * @param udpSocket the udp socket
     */
    public DatagramSocketReader(DatagramSocket udpSocket) {
        if (udpSocket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = udpSocket;
        this.listeners = new ArrayList<PacketListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        running = true;
        while (running && !Thread.currentThread().isInterrupted()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                notifyListeners(packet);
            } catch (SocketTimeoutException e) {
                logger.debug("Socket timed out");
            } catch (IOException e) {
                logger.debug("Interrupted.");
                running = false;
                Thread.currentThread().interrupt();
            }
        }
        logger.debug("Run loop ended.");
    }
    
    /**
     * Adds the listener.
     *
     * @param listener the listener
     */
    public void addListener(PacketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(PacketListener listener) {
            listeners.remove(listener);
    }
    
    private void notifyListeners(DatagramPacket packet) {
        for(PacketListener listener : listeners) {
            listener.packetReceived(packet);
        }
    }

 }
