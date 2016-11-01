package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboMessage;

public class LearnRequest extends OrviboMessage {

	
	public LearnRequest(String deviceId) {
		setDeviceId(deviceId);
	}
}
