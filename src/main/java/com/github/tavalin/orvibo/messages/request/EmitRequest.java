package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboMessage;

public class EmitRequest extends OrviboMessage {

	public EmitRequest(String deviceId, Object object) {
		setDeviceId(deviceId);
	}

}
