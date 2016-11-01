package com.github.tavalin.orvibo.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {
    
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);
    
    /** The broadcast address. */
    private static InetSocketAddress broadcastAddress;
    
    /**
     * Returns the broadcast address that sockets should use.
     *
     * @return the broadcast address
     * @throws SocketException the socket exception
     */
    public static synchronized InetSocketAddress getBroadcastAddress(int port) throws SocketException {
        if (broadcastAddress == null) {
            broadcastAddress = new InetSocketAddress(getFirstActiveBroadcast(), port);
        }
        
        // TODO: FIXME
        broadcastAddress =  new InetSocketAddress("192.168.0.255", port);
        return broadcastAddress;
    }

    /**
     * Gets the first active broadcast.
     *
     * @return the first active broadcast
     * @throws SocketException the socket exception
     */
    private static InetAddress getFirstActiveBroadcast() throws SocketException {
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
     * Gets the first active ipv4 interface.
     *
     * @return the first active ipv4 interface
     * @throws SocketException the socket exception
     */
    private static NetworkInterface getFirstActiveIPv4Interface() throws SocketException {
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
    public static boolean isLocalAddress(InetAddress address) {
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

}
