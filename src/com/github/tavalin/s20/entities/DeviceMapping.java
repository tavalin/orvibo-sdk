package com.github.tavalin.s20.entities;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMapping.
 */
public class DeviceMapping {
	
	/** The device id. */
	private String deviceID;
	
	/** The address. */
	private InetSocketAddress address;
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceMapping.class);
	
	/**
	 * Gets the device id.
	 *
	 * @return the device id
	 */
	public String getDeviceID() {
		return deviceID;
	}
	
	/**
	 * Sets the device id.
	 *
	 * @param deviceID the new device id
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
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
