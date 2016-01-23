Java SDK for the Orvibo S20

This project aims to provide a reusable Java library that implements the Orvibo S20 protocol.
A lot of the initial findings regarding the protocol were found https://stikonas.eu/wordpress/2015/02/24/reverse-engineering-orvibo-s20-socket/.

Basic Example (Assuming you know the MAC of the switch)

```
		String mac = "XXXXXXXXXXXX";
		S20Client client = null;
	        try {
	            client = S20Client.getInstance();
	            client.connect();
	            if (client.isConnected()) {
	                Socket socket = client.socketWithDeviceID(mac);
	                socket.findOnNetwork();
	                Thread.sleep(1000);
	                socket.subscribe();
	                Thread.sleep(1000);
	                socket.on();
	                Thread.sleep(5000);
	                socket.off();
	                }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            client.disconnect();
	        }
	
```

Please report bugs via the Issues tab, alternatively if you can fix or improve things please send me a Pull Request.
