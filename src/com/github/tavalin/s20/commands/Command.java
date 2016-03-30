package com.github.tavalin.s20.commands;

// TODO: Auto-generated Javadoc
/**
 * The Enum Command.
 */
public enum Command {

    /** The global. */
    GLOBAL_DISCOVERY(0x7161),
    
    /** The local. */
    LOCAL_DISCOVERY(0x7167),
    
    /** The power. */
    POWER(0x7366)
    ;
	
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
     * @param code the code
     * @return the command
     */
	public static Command getCommand(short code) {
	    for (final Command command : values()) {
	        if (command.getCode() == code) {
	            return command;
	        }
	    }
	    return null;
	}
	
}


