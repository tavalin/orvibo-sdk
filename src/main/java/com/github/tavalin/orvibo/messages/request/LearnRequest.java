package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboRequest;

public class LearnRequest extends OrviboRequest {
    
	public LearnRequest(String deviceId) {
		setDeviceId(deviceId);
	}

}
