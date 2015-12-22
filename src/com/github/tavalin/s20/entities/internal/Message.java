/*
 * 
 */
package com.github.tavalin.s20.entities.internal;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.s20.entities.Types.PowerState;
import com.github.tavalin.s20.utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class Message.
 */
public class Message {
	
	/** The device id. */
	private String deviceId;
	
	/** The message. */
	private String message;
	
	/** The address. */
	private InetSocketAddress address;
	
	/** The message type. */
	private MessageType messageType;
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(Message.class);
	
	/**
	 * Instantiates a new message.
	 *
	 * @param message the message
	 */
	public Message(String message) {
		setMessage(message);
	}

	/**
	 * Checks if is a response message.
	 *
	 * @return true, if is a response message
	 */
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

	/**
	 * Gets the message type.
	 *
	 * @return the message type
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * Sets the device id.
	 *
	 * @param deviceId the new device id
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	
	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	private void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the message type.
	 *
	 * @param messageType the new message type
	 */
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	/**
	 * Gets the device id.
	 *
	 * @return the device id
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	
	/**
	 * Gets the label from message.
	 *
	 * @param m the m
	 * @return the label from message
	 */
	public static String getLabelFromMessage(Message m) {
		if (m.getMessageType() == MessageType.SOCKET_DATA_RESPONSE) {
			int start = 140;
			int end = start + 32;
			String labelHex = m.getMessage().substring(start, end);
			return new String(Utils.hexStringToByteArray(labelHex)).trim();
		}
		logger.debug("Unable to get label from message.");
		return null;
	}
	
	/**
	 * Gets the device id from message.
	 *
	 * @param m the m
	 * @return the device id from message
	 */
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
	
	/**
	 * Gets the message type from message.
	 *
	 * @param m the m
	 * @return the message type from message
	 */
	public static MessageType getMessageTypeFromMessage(Message m) {
		int start = 4;
		int end = 4 + 8;
		String typeString = m.getMessage().substring(start, end);
		return MessageType.fromString(typeString);
	}

	/**
	 * Gets the power state message.
	 *
	 * @param message the message
	 * @return the power state message
	 */
	public static PowerState getPowerStateMessage(Message message) {
		int len = message.getMessage().length();
		String onOff = message.getMessage().substring(len - 2, len);
		if (onOff.equals(PowerState.ON.getText())) {
			return PowerState.ON;
		}
		return PowerState.OFF;
		
	}

}
