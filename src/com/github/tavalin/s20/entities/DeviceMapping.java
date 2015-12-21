package com.github.tavalin.s20.entities;

import java.net.InetSocketAddress;

public class DeviceMapping {
	
	private String deviceID;
	private InetSocketAddress address;
	
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public InetSocketAddress getAddress() {
		return address;
	}
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	

}
