package com.github.tavalin.s20.socket;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.entities.Types.DeviceReachability;
import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.entities.internal.Message;
import com.github.tavalin.s20.entities.internal.MessageType;
import com.github.tavalin.s20.utils.Utils;

public class Socket {

	public interface SocketStateListener {
		public void socketDidChangeLabel(Socket socket, String label);
		public void socketDidChangePowerState(Socket socket, PowerState powerState);
		public void socketDidInitialisation(Socket socket);

	}
	

	
	public interface SocketReachabilityListener {
		public void socketDidChangeReachability(Socket socket, DeviceReachability reachability);
	}

	// Initialisation steps
	public enum InitActions {
		DEVICE_ID, POWER, LABEL, TIME, ALARM
	}

	private InitActions[] initArray = { InitActions.DEVICE_ID, InitActions.POWER };
	private List<InitActions> initList = new ArrayList<InitActions>(Arrays.asList(initArray));
	private String deviceID;
	private String reverseDeviceId;
	private PowerState powerState;
	private long mostRecentMessageTimestamp;
	private DeviceReachability mostRecentReachability;
	private String label;
	private ArrayList<SocketStateListener> stateListeners = new ArrayList<SocketStateListener>();



	private S20Client s20Client;


	public PowerState getPowerState() {
		if (powerState == null) {
			return PowerState.OFF;
		}
		return powerState;
	}

	public String getDeviceId() {
		return deviceID;
	}

	public boolean isInitialised() {
		return initList.size() == 0;
	}

	private void setReverseDeviceID(String idToReverse) {

		// TODO: is there a cleaner way to do this?
		String[] pairs = idToReverse.split("(?<=\\G..)");
		StringBuilder sb = new StringBuilder();

		for (int i = pairs.length - 1; i >= 0; i--) {
			sb.append(pairs[i]);
		}
		reverseDeviceId = sb.toString();
	}

	private void updateInitActions(InitActions action) {
		initList.remove(action);
		if (initList.size() == 0) {
			notifyInitComplete();
		}
	}

	private long getMostRecentMessageTimestamp() {
		return mostRecentMessageTimestamp;
	}

	private void setMostRecentMessageTimestamp(long mostRecentMessageTimestamp) {
		this.mostRecentMessageTimestamp = mostRecentMessageTimestamp;
	}

	public DeviceReachability getReachability() {
		if ((System.currentTimeMillis() - getMostRecentMessageTimestamp()) < 120000) {
			return DeviceReachability.REACHABLE;
		}
		return DeviceReachability.UNREACHABLE;
	}

	public void setLabel(String label) {

		if (label != null && label.length() <= 16) {
			String tmp = String.format("%1$-16s", label);
			String labelHex = Utils.toHexString(tmp.getBytes());
			MessageType type = MessageType.SOCKET_DATA_WRITE;
			
			//TODO: Fix this so that message is not hardcoded
			String command = "6864" + type.getText() + getDeviceId() + "20202020202000000000040001be0001004325"
					+ getDeviceId() + "202020202020" + getReverseDeviceId() + "202020202020383838383838202020202020"
					+ labelHex
					+ "0400200000001a000000050000001027341c19ff1027766963656e7465722e6f727669626f2e636f6d202020202020202020202020202020202020202020c0a801c8c0a80101ffffff000101000000ff000000000000000000000000000030303030303030303030303030303030303030303030303030303030303030303030303030303030";
			Message message = new Message(command);
			message.setDeviceId(getDeviceId());
			message.setMessageType(MessageType.SOCKET_DATA_WRITE);
			try {
				s20Client.sendMessage(message);
			} catch (SocketException e) {
				// logger.error("Ooops. Couldn't send message to socket for some
				// reason :( " + e.getMessage())
			}
		}
	}

	public String getLabel() {
		return label;
	}

	private void notifyListenerLabelDidChange(String label) {
		for (SocketStateListener aListener : stateListeners) {
			aListener.socketDidChangeLabel(this, label);
		}
	}

	private void notifyListenersPowerStateDidChange(PowerState powerState) {
		for (SocketStateListener aListener : stateListeners) {
			aListener.socketDidChangePowerState(this, powerState);
		}
	}

	/*
	private void checkReachable() {
		if (getReachability() == mostRecentReachability) {
			return;
		}
		mostRecentReachability = getReachability();
		if (mostRecentReachability == DeviceReachability.UNREACHABLE) {
			//for (SocketStateListener aListener : stateListeners) {
				//aListener.socketUnreachable(this);
			//}
		} else {
			//for (SocketStateListener aListener : stateListeners) {
				//aListener.socketReachable(this);
			//}
		}
	}
	*/

	private void notifyInitComplete() {
		for (SocketStateListener aListener : stateListeners) {
			aListener.socketDidInitialisation(this);
		}
	}

	public void findOnNetwork() {
		String command = "686400127167" + getDeviceId() + "202020202020";
		Message message = new Message(command);
		message.setDeviceId(getDeviceId());
		message.setMessageType(MessageType.LOCAL_DISCOVERY_REQUEST);
		try {
			s20Client.sendMessage(message);
		} catch (SocketException e) {
			// logger.error("Ooops. Couldn't send message to socket for some
			// reason :( " + e.getMessage())
		}
	}

	public void subscribe() {

		String command = "6864001E636C" + getDeviceId() + "202020202020" + getReverseDeviceId() + "202020202020";
		Message message = new Message(command);
		message.setDeviceId(getDeviceId());
		message.setMessageType(MessageType.SUBSCRIPTION_REQUEST);
		try {
			s20Client.sendMessage(message);
		} catch (SocketException e) {
			// logger.error("Ooops. Couldn't send message to socket for some
			// reason :( " + e.getMessage())
		}
	}

	public String getReverseDeviceId() {
		return reverseDeviceId;
	}

	public void on() {
		try {
			setPowerState(PowerState.ON);
		} catch (SocketException e) {
			// logger.error("Ooops. Couldn't send message to socket for some
			// reason :( " + e.getMessage())
		}
	}

	public void off() {
		try {
			setPowerState(PowerState.OFF);
		} catch (SocketException e) {
			// logger.error("Ooops. Couldn't send message to socket for some
			// reason :( " + e.getMessage())
		}
	}

	private void setPowerState(PowerState powerState) throws SocketException {

		MessageType type = MessageType.POWER_REQUEST;
		String command = "6864" + type.getText() + getDeviceId() + "20202020202000000000" + powerState.getText();
		Message message = new Message(command);
		message.setDeviceId(getDeviceId());
		message.setMessageType(type);
		s20Client.sendMessage(message);
	}

	public void getSocketData() {

		// String command = "686400176463" + getDeviceID() +
		// "20202020202000000000" + powerState.getText();
		try {
			String table = "04";
			String version = "17";
			MessageType type = MessageType.SOCKET_DATA_REQUEST;
			String command = "6864" + type.getText() + getDeviceId() + "20202020202000000000" + table + "00" + version
					+ "00000000";
			Message message = new Message(command);
			message.setDeviceId(getDeviceId());
			message.setMessageType(type);

			s20Client.sendMessage(message);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getTableData() {
		try {
			MessageType type = MessageType.TABLE_DATA_REQUEST;
			String command = "6864" + type.getText() + getDeviceId() + "20202020202000000000" + powerState.getText();
			Message message = new Message(command);
			message.setDeviceId(getDeviceId());
			message.setMessageType(type);
			s20Client.sendMessage(message);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Socket socketWithDeviceID(String deviceID, S20Client networkContext) {
		Socket socket = new Socket();
		socket.setDeviceID(deviceID);
		socket.setNetworkContext(networkContext);
		networkContext.getAllSocketsCollection().put(deviceID, socket);
		return socket;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
		setReverseDeviceID(deviceID);
		initList.remove(InitActions.DEVICE_ID);
	}

	public S20Client getNetworkContext() {
		return s20Client;
	}

	private void setNetworkContext(S20Client networkContext) {
		this.s20Client = networkContext;
	}

	public synchronized void handleMessage(Message message) {
		if (message.isAResponseMessage()) {
			setMostRecentMessageTimestamp(System.currentTimeMillis());
		}
		switch (message.getMessageType()) {
		case DISCOVERY_RESPONSE: {
			PowerState powerState = Message.getPowerStateMessage(message);
			powerDidChangeTo(powerState);
			subscribe();
			getSocketData();
			break;
		}
		case LOCAL_DISCOVERY_RESPONSE: {
			PowerState powerState = Message.getPowerStateMessage(message);
			powerDidChangeTo(powerState);
			subscribe();
			getSocketData();
			break;
		}
		case SUBSCRIPTION_RESPONSE: {
			PowerState powerState = Message.getPowerStateMessage(message);
			powerDidChangeTo(powerState);
			break;
		}
		case POWER_RESPONSE: {
			PowerState powerState = Message.getPowerStateMessage(message);
			powerDidChangeTo(powerState);
			break;
		}
		case SOCKET_DATA_RESPONSE: {
			String label = Message.getLabelFromMessage(message);
			labelDidChangeTo(label);
			break;
		}
		case SOCKET_DATA_WRITE_RESPONSE: {
			getSocketData();
			break;
		}
		default:
			break;
		}
	}

	private void labelDidChangeTo(String label) {
		if (this.label == null || !this.label.equals(label)) {
			notifyListenerLabelDidChange(label);
		}
		this.label = label;
		updateInitActions(InitActions.LABEL);
	}

	public void powerDidChangeTo(PowerState powerState) {
		if (this.powerState == null || !this.powerState.equals(powerState)) {
			notifyListenersPowerStateDidChange(powerState);
		}
		this.powerState = powerState;
		updateInitActions(InitActions.POWER);
	}

	public void addSocketStateListener(SocketStateListener listener) {
		if (!stateListeners.contains(listener)) {
			stateListeners.add(listener);
		}
	}

	public void removeAllSocketStateListeners() {
		stateListeners.clear();
	}

	public void removeSocketStateListener(SocketStateListener listener) {
		stateListeners.remove(listener);
	}
	


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("DeviceId: ");
		sb.append(getDeviceId());
		sb.append(", ");
		sb.append("Label: ");
		sb.append(getLabel());
		sb.append(", ");
		sb.append("Power: ");
		sb.append(getPowerState());
		sb.append(", ");
		sb.append("Init Actions Remaining: ");
		sb.append(initList.size());
		sb.append(", ");
		sb.append("isInitialised: ");
		sb.append(isInitialised());
		sb.append("]");
		return sb.toString();
	}

}
