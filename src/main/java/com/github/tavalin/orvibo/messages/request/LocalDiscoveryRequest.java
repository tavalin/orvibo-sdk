package com.github.tavalin.orvibo.messages.request;

import com.github.tavalin.orvibo.messages.OrviboMessage;

public class LocalDiscoveryRequest extends OrviboMessage {
    
    public LocalDiscoveryRequest(String deviceId) {
        setDeviceId(deviceId);
    }

}
