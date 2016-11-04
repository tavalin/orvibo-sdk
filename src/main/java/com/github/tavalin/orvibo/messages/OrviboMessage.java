package com.github.tavalin.orvibo.messages;

import com.github.tavalin.orvibo.devices.PowerState;

public abstract class OrviboMessage {

	private String deviceId;
	private PowerState powerState;
    private String reverseId;

	public String getDeviceId() {
		return deviceId;
	}

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        setReverseId(deviceId);
    }

	public void setPowerState(PowerState powerState) {
		this.powerState = powerState;
	}

	public PowerState getPowerState() {
		return powerState;
	}
	    
    public String getReverseId() {
        return reverseId;
    }
    
    public void setReverseId(String idToReverse) {
        reverseId = MessageUtils.getReverseDeviceId(idToReverse);
    }
	


}
