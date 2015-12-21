package com.github.tavalin.s20.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.github.tavalin.s20.entities.internal.Message;
import com.github.tavalin.s20.utils.Utils;

public class SocketWriter implements Runnable {

    // private Logger logger = LoggerFactory.getLogger(SocketWriter.class);
    private DatagramSocket socket;
    private BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(100);
    private boolean running;

    public SocketWriter(DatagramSocket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }
        this.socket = socket;
    }

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
                // logger.debug(String.format("Sent: %s - %s", p.getSocketAddress(), message.getMessage()));
                System.out.println(String.format("Sent: %s - %s", p.getSocketAddress(), message.getMessage()));
                Thread.sleep(50); // 20 bursts of packets a second maximum
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

    public BlockingQueue<Message> getQueue() {
        return queue;
    }
}
