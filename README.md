Java SDK for the Orvibo S20

This project aims to provide a reusable Java library that implements the Orvibo S20 protocol.
A lot of the initial findings regarding the protocol were found https://stikonas.eu/wordpress/2015/02/24/reverse-engineering-orvibo-s20-socket/.

Basic Example (Assuming you know the MAC of the switch)

```
		S20Client networkConnection = null;
		try {
			networkConnection = S20Client.getInstance();
			networkConnection.connect();
			if (networkConnection.isConnected()) {
				Socket socket = Socket.socketWithDeviceID("XXXXXXXXXXXX", networkConnection);
				socket.findOnNetwork();
				socket.on();
				Thread.sleep(5000);
				socket.off();
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			networkConnection.disconnect();
		}
	
```

Please report bugs via the Issues tab, alternatively if you can fix or improve things please send me a Pull Request.
