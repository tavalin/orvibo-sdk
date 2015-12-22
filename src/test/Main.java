package test;

import com.github.tavalin.s20.S20Client;
import com.github.tavalin.s20.socket.Socket;

public class Main {
	
	public static void main(String args[]) {
		Main m = new Main();
		m.run();
	}

	public void run() {
		String mac = "ACCF23728BF2";
		 S20Client client = null;
	        try {
	            client = S20Client.getInstance();
	            client.connect();
	            if (client.isConnected()) {
	                Socket socket = Socket.socketWithDeviceID(mac, client);
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
	}



}
