package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.devices.PowerState;
import com.github.tavalin.orvibo.messages.OrviboMessage;

public class PowerStatusRequest extends OrviboMessage {

	public PowerStatusRequest(String deviceId, PowerState powerState) {
		setDeviceId(deviceId);
		setPowerState(powerState);
	}

}
