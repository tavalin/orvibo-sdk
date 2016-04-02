package com.github.tavalin.s20.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 		
 		@SuppressWarnings("unused")
		private static final Logger logger = LoggerFactory.getLogger(DeviceReachability.class);
		 }

		 /**
 		 * The Enum PowerState.
 		 */
 		public enum PowerState {
 			
 			/** The on. */
 			ON((byte)0x01),
			 
 			/** The off. */
 			OFF((byte)0x00);
			 
			 /** The text. */
 			private byte state;

			 /**
 			 * Instantiates a new power state.
 			 *
 			 * @param text the text
 			 */
 			PowerState(byte state) {
					this.state = state;
				}
 			
 			@SuppressWarnings("unused")
			private static final Logger logger = LoggerFactory.getLogger(PowerState.class);

				/**
				 * Gets the text.
				 *
				 * @return the text
				 */
				public byte getByte() {
					return this.state;
				}

				/**
				 * Gets the PowerState From string.
				 *
				 * @param text the text
				 * @return the power state
				 */

			    public static PowerState getPowerState(byte state) {
			        for (final PowerState powerState : values()) {
			            if (powerState.getByte() == state) {
			                return powerState;
			            }
			        }
			        return null;
			    }
		 }
	 
}
