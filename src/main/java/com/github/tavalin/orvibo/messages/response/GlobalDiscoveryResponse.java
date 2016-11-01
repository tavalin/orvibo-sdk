package com.github.tavalin.orvibo.messages.response;

import com.github.tavalin.orvibo.devices.DeviceType;
import com.github.tavalin.orvibo.messages.OrviboMessage;

public class GlobalDiscoveryResponse extends OrviboMessage {

	private DeviceType deviceType = DeviceType.UNKNOWN;

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

}
