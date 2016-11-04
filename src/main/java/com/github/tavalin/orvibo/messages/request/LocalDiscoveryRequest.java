package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboRequest;

public class LocalDiscoveryRequest extends OrviboRequest {
    
    public LocalDiscoveryRequest(String deviceId) {
        setDeviceId(deviceId);
    }

}
