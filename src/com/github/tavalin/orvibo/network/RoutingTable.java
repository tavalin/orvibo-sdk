package com.github.tavalin.orvibo.network;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tavalin.orvibo.entities.DeviceMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class RoutingTable.
 */
public class RoutingTable {

	/** The mutable device mappings by device id. */
	private Map<String,DeviceMapping> mutableDeviceMappingsByDeviceID = new HashMap<String,DeviceMapping>();
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(RoutingTable.class);

	 /**
 	 * Gets the device mapping for device id.
 	 *
 	 * @param deviceID the device id
 	 * @return the device mapping for device id
 	 */
 	public DeviceMapping getDeviceMappingForDevice(String deviceId) {
	        return getDeviceMappings().get(deviceId);
	    }
	
    /**
     * Update device mapping with device id site id.
     *
     * @param deviceId the device id
     * @param address the address
     */
    public void updateDeviceMapping(String deviceId, InetSocketAddress address) {
        DeviceMapping deviceMapping = getDeviceMappings().get(deviceId);

        if (deviceMapping == null) {
        	logger.debug("Creating new device mapping.");
            deviceMapping = new DeviceMapping();
            getDeviceMappings().put(deviceId, deviceMapping);
        }

        logger.debug("Updating device mapping details");
        deviceMapping.setDeviceID(deviceId);
        deviceMapping.setAddress(address);
    }
    
    /**
     * Gets the device mappings.
     *
     * @return the device mappings
     */
    private synchronized Map<String,DeviceMapping> getDeviceMappings() {
    	return mutableDeviceMappingsByDeviceID;
    }

}
