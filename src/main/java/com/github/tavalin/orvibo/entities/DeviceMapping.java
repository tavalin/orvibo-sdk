package com.github.tavalin.orvibo.entities;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMapping.
 */
public class DeviceMapping {
	
	/** The device id. */
	private String deviceId;
	
	/** The address. */
	private InetSocketAddress address;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DeviceMapping.class);
	
	/**
	 * Gets the device id.
	 *
	 * @return the device id
	 */
	public String getDeviceId() {
		return deviceId;
	}
	
	/**
	 * Sets the device id.
	 *
	 * @param deviceID the new device id
	 */
	public void setDeviceId(String deviceID) {
		this.deviceId = deviceID;
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
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	

}
