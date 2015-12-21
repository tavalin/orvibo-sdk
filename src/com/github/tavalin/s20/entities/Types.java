package com.github.tavalin.s20.entities;

import com.github.tavalin.s20.entities.internal.MessageType;

public class Types {
	
	 public enum DeviceReachability {
		 	REACHABLE,
		 	UNREACHABLE,
		 }

		 public enum PowerState {
			 ON("01"),
			 OFF("00");
			 
			 private String text;

			 PowerState(String text) {
					this.text = text;
				}

				public String getText() {
					return this.text;
				}

				public static PowerState fromString(String text) {
					if (text != null) {
						for (PowerState p : PowerState.values()) {
							if (text.equalsIgnoreCase(p.text)) {
								return p;
							}
						}
					}
					return null;
				}
		 }
	 
}
