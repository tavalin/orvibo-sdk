package com.github.tavalin.s20.entities;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Types.
 */
public class Types {
	
	 /**
 	 * The Enum DeviceReachability.
 	 */
 	public enum DeviceReachability {
		 	
	 		/** The reachable. */
	 		REACHABLE,
		 	
	 		/** The unreachable. */
	 		UNREACHABLE;
	 		
			private static final Logger logger = Logger.getLogger(DeviceReachability.class);
		 }

		 /**
 		 * The Enum PowerState.
 		 */
 		public enum PowerState {
 			

			 
 			/** The on. */
 			ON("01"),
			 
 			/** The off. */
 			OFF("00");
			 
			 /** The text. */
 			private String text;

			 /**
 			 * Instantiates a new power state.
 			 *
 			 * @param text the text
 			 */
 			PowerState(String text) {
					this.text = text;
				}
 			
 			private static final Logger logger = Logger.getLogger(PowerState.class);

				/**
				 * Gets the text.
				 *
				 * @return the text
				 */
				public String getText() {
					return this.text;
				}

				/**
				 * Gets the PowerState From string.
				 *
				 * @param text the text
				 * @return the power state
				 */
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
