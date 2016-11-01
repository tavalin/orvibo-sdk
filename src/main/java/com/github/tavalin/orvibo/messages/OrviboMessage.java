package com.github.tavalin.orvibo.messages;

import com.github.tavalin.orvibo.devices.PowerState;

public abstract class OrviboMessage {

	private String deviceId;
	private PowerState powerState;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}

	public PowerState getPowerState() {
		return powerState;
	}
	


}
