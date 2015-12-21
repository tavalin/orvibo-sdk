package com.github.tavalin.s20.entities.internal;

import java.net.InetSocketAddress;

import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.utils.Utils;

public class Message {
	
	private String deviceId;
	private String message;
	private InetSocketAddress address;
	private MessageType messageType;
	
	public Message(String message) {
		setMessage(message);
	}

	public boolean isAResponseMessage() {
		MessageType type = getMessageType();
		if (type == MessageType.DISCOVERY_RESPONSE || 
				type == MessageType.LOCAL_DISCOVERY_RESPONSE ||
				type == MessageType.SUBSCRIPTION_RESPONSE ||
				type == MessageType.POWER_RESPONSE ||
				type == MessageType.SOCKET_DATA_RESPONSE ||
				type == MessageType.TABLE_DATA_RESPONSE
				) 
		{
			return true;
		}
		return false;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getMessage() {
		return message;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	
	private void setMessage(String message) {
		this.message = message;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	
	public static String getLabelFromMessage(Message m) {
		if (m.getMessageType() == MessageType.SOCKET_DATA_RESPONSE) {
			int start = 140;
			int end = start + 32;
			String labelHex = m.getMessage().substring(start, end);
			return new String(Utils.hexStringToByteArray(labelHex)).trim();
		}
		return null;
	}
	
	public static String getDeviceIdFromMessage(Message m) {
		if (m.getMessageType() == MessageType.DISCOVERY_RESPONSE || m.getMessageType() == MessageType.LOCAL_DISCOVERY_RESPONSE) {
			int start = 14;
			int end = start + 12;
			String deviceId = m.getMessage().substring(start, end);
			return deviceId;
		} else {
			int start = 12;
			int end = start + 12;
			String deviceId = m.getMessage().substring(start, end);
			return deviceId;
		}
	}
	
	public static MessageType getMessageTypeFromMessage(Message m) {
		int start = 4;
		int end = 4 + 8;
		String typeString = m.getMessage().substring(start, end);
		return MessageType.fromString(typeString);
	}

	public static PowerState getPowerStateMessage(Message message) {
		int len = message.getMessage().length();
		String onOff = message.getMessage().substring(len - 2, len);
		if (onOff.equals(PowerState.ON.getText())) {
			return PowerState.ON;
		}
		return PowerState.OFF;
		
	}

}
