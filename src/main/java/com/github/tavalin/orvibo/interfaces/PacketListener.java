package com.github.tavalin.orvibo.interfaces;

import java.net.DatagramPacket;



/**
 * The listener interface for receiving packet events.
 * The class that is interested in processing a packet
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPacketListener<code> method. When
 * the packet event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface PacketListener {
    
    /**
     * Packet received.
     *
     * @param packet the packet
     */
    public void packetReceived(DatagramPacket packet);
}
