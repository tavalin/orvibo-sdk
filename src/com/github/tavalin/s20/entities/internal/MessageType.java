package com.github.tavalin.s20.entities.internal;

public enum MessageType {

	DISCOVERY_REQUEST("00067161"), 
	DISCOVERY_RESPONSE("002A7161"), 
	LOCAL_DISCOVERY_REQUEST("00127167"), 
	LOCAL_DISCOVERY_RESPONSE("002A7167"), 
	SUBSCRIPTION_REQUEST("001E636C"), 
	SUBSCRIPTION_RESPONSE("0018636C"), 
	POWER_REQUEST("00176463"), 
	POWER_RESPONSE("00177366"), // 6463?
							SOCKET_DATA_REQUEST("001D7274"), 
							SOCKET_DATA_RESPONSE("00DC7274"),
							SOCKET_DATA_WRITE("00D9746d"),
							SOCKET_DATA_WRITE_RESPONSE("0017746D"),
							TABLE_DATA_REQUEST(""), 
							TABLE_DATA_RESPONSE(								""), 
							TYPE_UNKNOWN("");

	private String text;

	MessageType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static MessageType fromString(String text) {
		if (text != null) {
			for (MessageType b : MessageType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

}
