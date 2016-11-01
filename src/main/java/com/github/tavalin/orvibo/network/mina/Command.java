package com.github.tavalin.orvibo.network.mina;

// TODO: Auto-generated Javadoc
/**
 * The Enum Command.
 */
public enum Command {

     GLOBAL_DISCOVERY(0x7161),
    
     LOCAL_DISCOVERY(0x7167),
    
     POWER_REQUEST(0x6463),
    
    POWER(0x7366),
    
    SUBSCRIBE(0x636C),
    
    LEARN(0x6c73),
    
    EMIT(0x6963);
	
	/** The code. */
	private short code;
	
	/**
     * Instantiates a new command.
     *
     * @param code the code
     */
	private Command(int code) {
	    this.code = (short)code;
	}
	
	/**
     * Gets the code.
     *
     * @return the code
     */
	public short getCode() {
	    return code;
	}
	
	/**
     * Gets the command.
     *
     * @param cmdShort the code
     * @return the command
     */
    public static Command getCommand(int cmdShort) {
	    for (final Command command : values()) {
	        if (command.getCode() == cmdShort) {
	            return command;
	        }
	    }
	    return null;
	}
	
}


