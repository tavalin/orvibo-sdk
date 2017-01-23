package com.github.tavalin.orvibo.devices;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.commands.CommandFactory;
import com.github.tavalin.orvibo.protocol.Message;

public abstract class OrviboDevice {
	
	private String deviceId;
	private String reverseDeviceId;
	private OrviboClient orviboClient;
	private String label;
	private DeviceType deviceType;
    protected boolean lastOperationSuccess;
	
	public OrviboDevice(DeviceType type) {
	    setDeviceType(type);
	}
	
	
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        setReverseDeviceID(deviceId);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getReverseDeviceId() {
        return reverseDeviceId;
    }

    private void setReverseDeviceID(String idToReverse) {

        // TODO: is there a cleaner way to do this?
        String[] pairs = idToReverse.split("(?<=\\G..)");
        StringBuilder sb = new StringBuilder();

        for (int i = pairs.length - 1; i >= 0; i--) {
            sb.append(pairs[i]);
        }
        reverseDeviceId = sb.toString();
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public void setNetworkContext(OrviboClient orviboClient) {
        this.orviboClient = orviboClient;
    }

    public OrviboClient getNetworkContext() {
        return orviboClient;
    }
	
	public void find() {
		Message message = CommandFactory.createLocalDiscoveryCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
	}
	
	public void subscribe() {
		Message message = CommandFactory.createSubscribeCommand(this);
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(message);
	}


    public DeviceType getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }


    public boolean isLastOperationSuccess() {
        return lastOperationSuccess;
    }

    public void setLastOperationSuccess(boolean lastOperationSuccess) {
        this.lastOperationSuccess = lastOperationSuccess;
    }
}
