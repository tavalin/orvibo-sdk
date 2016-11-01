# Orvibo Java SDK [![Build Status](https://travis-ci.org/tavalin/orvibo-sdk.svg?branch=master)](https://travis-ci.org/tavalin/orvibo-sdk)


This project aims to provide a reusable Java library that implements for communicating
with Orvibo devices that use a UDP protcol, including S20 Smart Sockets and Allone Smart Remotes.
A lot of the initial findings regarding the protocol were found https://stikonas.eu/wordpress/2015/02/24/reverse-engineering-orvibo-s20-socket/.

## Examples:

### Network Discovery (MAC/deviceId is unknown):
```
package test;

import java.net.SocketException;

import com.github.tavalin.orvibo.OrviboClient;
import com.github.tavalin.orvibo.OrviboClient.OrviboDiscoveryListener;
import com.github.tavalin.orvibo.devices.OrviboDevice;

public class Main implements OrviboDiscoveryListener {

    public static void main(String args[]) {
        Main m = new Main();
        m.run();
    }

    public void run() {
        OrviboClient client = null;
        try {
            client = OrviboClient.getInstance();
            client.connect();
            if (client.isConnected()) {
                client.addDeviceDiscoveryListener(this);
                client.globalDiscovery();
            }
             while (true); // wait for discovery results
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
	        if (client != null) {
	            client.disconnect();
	        }
        }
       
    }

    @Override
    public void deviceDiscovered(OrviboDevice device) {
        if (device == null) {
            return;
        }
        System.out.println("Device discovered: " + device.getDeviceId());
     }
}
```

### S20 Example (Assuming you know the MAC of the S20 Smart Socket)

```
		String mac = "XXXXXXXXXXXX";
	    OrviboClient client = null;
	        try {
            OrviboClient client = OrviboClient.getInstance();
            client.connect();
	            if (client.isConnected()) {
	                Socket socket = client.socketWithDeviceId(mac);
	                socket.find();
	                Thread.sleep(1000);
	                socket.subscribe();
	                Thread.sleep(1000);
	                socket.on();
	                Thread.sleep(5000);
	                socket.off();
	                }
	        } catch (SocketException e) {
	            e.printStackTrace();
	        } finally {
	            if (client != null) {
	                client.disconnect();
	            }
	        }
	
```

Please report bugs via the Issues tab, alternatively if you can fix or improve things please send me a Pull Request.
