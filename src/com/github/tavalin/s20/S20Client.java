package com.github.tavalin.s20;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.tavalin.s20.entities.DeviceMapping;
import com.github.tavalin.s20.entities.internal.Message;
import com.github.tavalin.s20.entities.internal.MessageType;
import com.github.tavalin.s20.network.RoutingTable;
import com.github.tavalin.s20.network.TransportManager;
import com.github.tavalin.s20.socket.Socket;

public class S20Client {
	
	public interface SocketDiscoveryListener {
		public void socketDiscovered(Socket socket);
	}
	
	private ArrayList<SocketDiscoveryListener> discoveryListeners = new ArrayList<SocketDiscoveryListener>();
	
	private TransportManager transportManager; 
    private RoutingTable routingTable;
	private Map<String,Socket> allSocketsCollection = new HashMap<String,Socket>();
	
	public Map<String, Socket> getAllSocketsCollection() {
		return allSocketsCollection;
	}
	
	private static S20Client instance;
	
	public static S20Client getInstance() throws SocketException {
		if (instance == null) {
			instance = new S20Client();
		}
		return instance;
	}

	public S20Client() throws SocketException {
		transportManager = new TransportManager(this);
		routingTable = new RoutingTable();
	}

    public boolean isConnected() {
    	
        return (transportManager != null && transportManager.isConnected());
    }


    public void connect() {
    	if (transportManager != null && !transportManager.isConnected()) {
    		transportManager.connect();
    	}
    }
    
    public void globalDiscovery() {
		
		String command = "686400067161";
		Message message = new Message(command);
		message.setMessageType(MessageType.DISCOVERY_REQUEST);
		try {
			sendMessage(message);
		} catch (SocketException e) {
			// logger.error("Ooops. Couldn't send message to socket for some
			// reason :( " + e.getMessage())
		}
    }

	public void sendMessage(Message message) throws SocketException {
		
		DeviceMapping mapping = routingTable.getDeviceMappingForDeviceID(message.getDeviceId());
		if (mapping == null || message.getDeviceId() == null) {
			message.setAddress(TransportManager.getBroadcastAddress());
		} else {
			message.setAddress(mapping.getAddress());
		}
		transportManager.addToWriteQueue(message);
	}
	
    public void handleMessage(Message message) {
        routingTable.updateMappingsFromMessage(message);
        forwardMessageToDeviceWithDeviceID(message, message.getDeviceId());
   }
	
	
	public void forwardMessageToDeviceWithDeviceID(Message message, String deviceID) {
        Socket socket = allSocketsCollection.get(deviceID);
        if (socket == null) {
        	socket = Socket.socketWithDeviceID(deviceID, this);
        	notifyDiscoveryListeners(socket);
         }
        socket.handleMessage(message);
    }
	
    public void disconnect()  {
    	if (transportManager != null && transportManager.isConnected()) {
    		transportManager.disconnect();
    	}
    }
    
	public void addSocketDiscoveryListener(SocketDiscoveryListener listener) {
		if (!discoveryListeners.contains(listener)) {
			discoveryListeners.add(listener);
		}
	}

	public void removeAllSocketDiscoveryListeners() {
		discoveryListeners.clear();
	}

	public void removeSocketDiscoveryListener(SocketDiscoveryListener listener) {
		discoveryListeners.remove(listener);
	}
	
	public void notifyDiscoveryListeners(Socket socket) {
		for (SocketDiscoveryListener aListener : discoveryListeners) {
			aListener.socketDiscovered(socket);
		}
	
	}

}
