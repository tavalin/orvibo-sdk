package com.github.tavalin.orvibo.devices;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.exceptions.OrviboException;
import com.github.tavalin.orvibo.messages.request.LocalDiscoveryRequest;
import com.github.tavalin.orvibo.messages.request.SubscriptionRequest;

public abstract class OrviboDevice {
    
    final static int MAX_PASSWORD_LENGTH = 6;
    final static int MAX_LABEL_LENGTH = 12;
    final static String DEFAULT_PASSWORD = "888888";
	
	private String deviceId;
	private String reverseDeviceId;
	private OrviboClient orviboClient;
	private String label;
	private DeviceType deviceType;
	private String password = DEFAULT_PASSWORD;
	
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
	    LocalDiscoveryRequest request = new LocalDiscoveryRequest(getDeviceId());
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(request, true);
	}
	
	public void subscribe() {
        SubscriptionRequest request = new SubscriptionRequest();
        OrviboClient orviboClient = getNetworkContext();
        orviboClient.sendMessage(request, true);
	}


    public DeviceType getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) throws OrviboException {
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new OrviboException("Password cannot be longer than " + MAX_PASSWORD_LENGTH);
        }
        this.password = password;
    }
    




}
