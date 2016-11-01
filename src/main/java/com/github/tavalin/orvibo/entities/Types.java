package com.github.tavalin.orvibo.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class Types.
 */
public class Types {
	
		 /**
 		 * The Enum PowerState.
 		 */
 		public enum PowerStateOld {
 			
 			/** The on. */
 			ON((byte)0x01),
 			
 			UNKNOWN((byte)0xFF),
			 
 			/** The off. */
 			OFF((byte)0x00);
 		    
 		    
 		    
			 
			 /** The text. */
 			private byte state;

			 /**
 			 * Instantiates a new power state.
 			 *
 			 * @param text the text
 			 */
 			PowerStateOld(byte state) {
					this.state = state;
				}
 			
 			@SuppressWarnings("unused")
			private static final Logger logger = LoggerFactory.getLogger(PowerStateOld.class);

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

			    public static PowerStateOld getPowerState(byte state) {
			        for (final PowerStateOld powerState : values()) {
			            if (powerState.getByte() == state) {
			                return powerState;
			            }
			        }
			        return null;
			    }
		 }
	 
}
