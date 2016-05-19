package com.github.tavalin.orvibo.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.AbstractCommandHandler;
import com.github.tavalin.orvibo.commands.Command;
import com.github.tavalin.orvibo.entities.DeviceMapping;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.interfaces.MessageListener;
import com.github.tavalin.orvibo.interfaces.PacketListener;
import com.github.tavalin.orvibo.protocol.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class TransportManager.
 */
public class TransportManager implements PacketListener, MessageListener {

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

    /** The Constant BROADCAST_PORT. */
    public final static int BROADCAST_PORT = 10000;

    /** The Constant REMOTE_PORT. */
    public final static int REMOTE_PORT = 10000;

    /** The Constant LISTEN_PORT. */
    public final static int LISTEN_PORT = 10000;



    public final static int DISCONNECT_TIMEOUT = 30000;

    public HashMap<InetAddress, PacketHandler> packetHandlers = new HashMap<InetAddress, PacketHandler>();

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
                readerThread.join(DISCONNECT_TIMEOUT);

                writerThread.interrupt();
                writerThread.join(DISCONNECT_TIMEOUT);

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
    public void messageReceived(InetAddress remoteAddress, Message message) {
        Command command = message.getCommand();
        AbstractCommandHandler handler = AbstractCommandHandler.getHandler(command);
        if (handler != null) {
            try {
                String deviceId = handler.getDeviceId(message.getCommandPayload());
                routingTable.updateDeviceMapping(deviceId, new InetSocketAddress(remoteAddress, REMOTE_PORT));
                message.setDeviceId(deviceId);
                handler.handle(message);
            } catch (OrviboException e) {
                logger.warn("Unable to handle message {}", Message.bb2hex(message.asBytes()));
            }
        }
    }

    @Override
    public synchronized void packetReceived(DatagramPacket packet) {
        // who is the packet from?
        InetAddress remoteAddress = packet.getAddress();
        if (!isLocalAddress(remoteAddress)) { // No loopback packets
            PacketHandler handler = packetHandlers.get(remoteAddress);
            if (handler == null) {
                handler = new PacketHandler(remoteAddress);
                handler.addListener(this);
                packetHandlers.put(remoteAddress, handler);
            }
            try {
                handler.packetReceived(packet);
            } catch (OrviboException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.debug("Ignoring loopback packet.");
        }
    }

}
