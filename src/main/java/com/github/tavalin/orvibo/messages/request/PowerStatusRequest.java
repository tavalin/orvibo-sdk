package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.devices.PowerState;
import com.github.tavalin.orvibo.messages.OrviboRequest;

public class PowerStatusRequest extends OrviboRequest {

	public PowerStatusRequest(String deviceId, PowerState powerState) {
		setDeviceId(deviceId);
		setPowerState(powerState);
	}

}
