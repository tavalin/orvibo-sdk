package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboRequest;

public class SubscriptionRequest extends OrviboRequest {

	public SubscriptionRequest(String deviceId) {
		setDeviceId(deviceId);
	}

}
