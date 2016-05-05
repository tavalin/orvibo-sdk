package com.github.tavalin.orvibo.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.AbstractCommandHandler;
import com.github.tavalin.orvibo.commands.Command;
import com.github.tavalin.orvibo.entities.DeviceMapping;
import com.github.tavalin.orvibo.network.DatagramSocketReader.PacketListener;
import com.github.tavalin.orvibo.protocol.InvalidMessageException;
import com.github.tavalin.orvibo.protocol.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class TransportManager.
 */
public class TransportManager implements PacketListener {

    /** The Constant logger. */
    private final Logger logger = LoggerFactory.getLogger(TransportManager.class);

    /** The reader. */
    private DatagramSocketReader reader;

    /** The writer. */
    private DatagramSocketWriter writer;

    /** The reader thread. */
    private Thread readerThread;

    /** The writer thread. */
    private Thread writerThread;

    private RoutingTable routingTable;

    /** The udp socket. */
    private DatagramSocket udpSocket;

    /** The broadcast address. */
    private static InetSocketAddress broadcastAddress;

    /** The connected. */
    private boolean connected;

    private CircularFifoQueue<DatagramPacket> previousPackets;

    /** The Constant BROADCAST_PORT. */
    public final static int BROADCAST_PORT = 10000;

    /** The Constant REMOTE_PORT. */
    public final static int REMOTE_PORT = 10000;

    /** The Constant LISTEN_PORT. */
    public final static int LISTEN_PORT = 10000;

    public final static int STORED_MESSAGES = 1;

    /**
     * Instantiates a new transport manager.
     *
     * @param s20Client the s20 client
     * @throws SocketException the socket exception
     */
    public TransportManager(OrviboClient s20Client) throws SocketException {
        udpSocket = new DatagramSocket(LISTEN_PORT);
        udpSocket.setBroadcast(true);
        writer = new DatagramSocketWriter(udpSocket);
        reader = new DatagramSocketReader(udpSocket);
        reader.addListener(this);
        routingTable = new RoutingTable();
        previousPackets = new CircularFifoQueue<DatagramPacket>(STORED_MESSAGES);
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Connect.
     */
    public void connect() {
        if (!isConnected()) {
            readerThread = new Thread(reader);
            writerThread = new Thread(writer);
            writerThread.start();
            readerThread.start();
            connected = true;
        }

    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        try {
            if (isConnected()) {
                
                readerThread.interrupt();
                readerThread.join();
                
                writerThread.interrupt();
                writerThread.join();

            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            udpSocket.close();
            udpSocket = null;
            connected = false;
        }

    }

    /**
     * Returns the broadcast address that sockets should use.
     *
     * @return the broadcast address
     * @throws SocketException the socket exception
     */
    private synchronized InetSocketAddress getBroadcastAddress() throws SocketException {
        if (broadcastAddress == null) {
            broadcastAddress = new InetSocketAddress(getFirstActiveBroadcast(), BROADCAST_PORT);
        }
        return broadcastAddress;
    }

    /**
     * Gets the peer address.
     * 
     * @param peerAddress the peer address
     * @return the peer address
     */
    // TODO: Maybe unnecessary
    public InetSocketAddress getPeerAddress(InetAddress peerAddress) {
        return new InetSocketAddress(peerAddress, REMOTE_PORT);
    }

    /**
     * Gets the first active broadcast.
     *
     * @return the first active broadcast
     * @throws SocketException the socket exception
     */
    private InetAddress getFirstActiveBroadcast() throws SocketException {
        NetworkInterface iface = getFirstActiveIPv4Interface();
        if (iface != null) {
            for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
                InetAddress addr = ifaceAddr.getAddress();
                if (addr instanceof Inet4Address) {
                    return ifaceAddr.getBroadcast();
                }
            }
        }
        return null;
    }

    /**
     * Gets the first active i pv4 interface.
     *
     * @return the first active i pv4 interface
     * @throws SocketException the socket exception
     */
    private NetworkInterface getFirstActiveIPv4Interface() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface iface = networkInterfaces.nextElement();
            if (iface.isUp() && !iface.isLoopback()) {
                for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
                    if (ifaceAddr.getAddress() instanceof Inet4Address) {
                        return iface;
                    }
                }
            }
        }
        logger.debug("Unable to retrieve active network interface.");
        return null;
    }

    /**
     * Checks if is local address.
     *
     * @param address the address
     * @return true, if is local address
     */
    private boolean isLocalAddress(InetAddress address) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                if (iface.isUp()) {
                    for (InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
                        if (ifaceAddr.getAddress().equals(address)) {
                            return true;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            logger.error("Error while determining if address is local");
        }
        return false;
    }

    public void send(Message message) {
        InetSocketAddress address = null;
        try {
            DeviceMapping mapping = routingTable.getDeviceMappingForDevice(message.getDeviceId());
            if (mapping == null || message.getDeviceId() == null) {
                logger.debug("No routing table entry found, sending message as broadcast.");
                address = getBroadcastAddress();
            } else {
                logger.debug("Routing table entry found.");
                address = mapping.getAddress();
            }
            byte[] payload = message.asBytes();
            DatagramPacket packet = new DatagramPacket(payload, payload.length, address);
            writer.send(packet);
        } catch (SocketException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public synchronized void packetReceived(DatagramPacket packet) {
        InetAddress remoteAddress = packet.getAddress();
        if (!isLocalAddress(remoteAddress) && !packetAlreadyReceived(packet)) {
                byte[] bytes = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());
                logger.debug("Received Message = {}", Message.bb2hex(bytes));
                try {
                    Message message = new Message(bytes);
                    processMessage(remoteAddress, message);
                    previousPackets.add(packet);
                }
                catch (InvalidMessageException ex) {
                    logger.warn("Message is invalid, ignoring.");
                }
        }
    }

    private void processMessage(InetAddress remoteAddress, Message message) {
        Command command = message.getCommand();
        AbstractCommandHandler handler = AbstractCommandHandler.getHandler(command);
        if (handler != null) {
            String deviceId = handler.getDeviceId(message.getCommandPayload());
            routingTable.updateDeviceMapping(deviceId, new InetSocketAddress(remoteAddress, REMOTE_PORT));
            message.setDeviceId(deviceId);
            handler.handle(message);
        } else {
            logger.warn("No handler found for message {}", Message.bb2hex(message.asBytes()));
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
