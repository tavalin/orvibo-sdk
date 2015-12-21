package com.github.tavalin.s20.network;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.github.tavalin.s20.entities.DeviceMapping;
import com.github.tavalin.s20.entities.internal.Message;

public class RoutingTable {

	private Map<String,DeviceMapping> mutableDeviceMappingsByDeviceID = new HashMap<String,DeviceMapping>();

	public String getDeviceIDForAddress(InetSocketAddress object) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 public DeviceMapping getDeviceMappingForDeviceID(String deviceID) {
	        return mutableDeviceMappingsByDeviceID.get(deviceID);
	    }

	public void updateMappingsFromMessage(Message message) {
	       if (message.isAResponseMessage() == false) {
	            return;
	        }
	       
	       updateDeviceMappingWithDeviceIDSiteID(message.getDeviceId(),message.getAddress());
	}
	
    public void updateDeviceMappingWithDeviceIDSiteID(String deviceID, InetSocketAddress address) {
        DeviceMapping deviceMapping = mutableDeviceMappingsByDeviceID.get(deviceID);

        if (deviceMapping == null) {
            deviceMapping = new DeviceMapping();
            mutableDeviceMappingsByDeviceID.put(deviceID, deviceMapping);
        }

        deviceMapping.setDeviceID(deviceID);
        deviceMapping.setAddress(address);
    }

}
