package com.github.tavalin.s20.network;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.tavalin.s20.entities.DeviceMapping;
import com.github.tavalin.s20.entities.internal.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class RoutingTable.
 */
public class RoutingTable {

	/** The mutable device mappings by device id. */
	private Map<String,DeviceMapping> mutableDeviceMappingsByDeviceID = new HashMap<String,DeviceMapping>();
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(RoutingTable.class);

	 /**
 	 * Gets the device mapping for device id.
 	 *
 	 * @param deviceID the device id
 	 * @return the device mapping for device id
 	 */
 	public DeviceMapping getDeviceMappingForDeviceID(String deviceID) {
	        return getDeviceMappings().get(deviceID);
	    }

	/**
	 * Update mappings from message.
	 *
	 * @param message the message
	 */
	public void updateMappingsFromMessage(Message message) {
	       if (message.isAResponseMessage() == false) {
	            return;
	        }
	       updateDeviceMappingWithDeviceIDSiteID(message.getDeviceId(),message.getAddress());
	}
	
    /**
     * Update device mapping with device id site id.
     *
     * @param deviceID the device id
     * @param address the address
     */
    public void updateDeviceMappingWithDeviceIDSiteID(String deviceID, InetSocketAddress address) {
        DeviceMapping deviceMapping = getDeviceMappings().get(deviceID);

        if (deviceMapping == null) {
        	logger.debug("Creating new device mapping.");
            deviceMapping = new DeviceMapping();
            getDeviceMappings().put(deviceID, deviceMapping);
        }

        logger.debug("Updating device mapping details");
        deviceMapping.setDeviceID(deviceID);
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
