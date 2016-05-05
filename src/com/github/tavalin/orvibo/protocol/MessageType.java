package com.github.tavalin.orvibo.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Enum MessageType.
 */
public enum MessageType {

    /** The discovery request. */
    DISCOVERY_REQUEST("00067161"),

    /** The discovery response. */
    DISCOVERY_RESPONSE("002A7161"),

    /** The local discovery request. */
    LOCAL_DISCOVERY_REQUEST("00127167"),

    /** The local discovery response. */
    LOCAL_DISCOVERY_RESPONSE("002A7167"),

    /** The subscription request. */
    SUBSCRIPTION_REQUEST("001E636C"),

    /** The subscription response. */
    SUBSCRIPTION_RESPONSE("0018636C"),

    /** The power request. */
    POWER_REQUEST("00176463"),

    /** The power response. */
    POWER_RESPONSE("00177366"),
    /** The socket data request. */
    // 6463?
    SOCKET_DATA_REQUEST("001D7274"),

    /** The socket data response. */
    SOCKET_DATA_RESPONSE("00DC7274"),

    /** The socket data write. */
    SOCKET_DATA_WRITE("00D9746d"),

    /** The socket data write response. */
    SOCKET_DATA_WRITE_RESPONSE("0017746D"),

    /** The table data request. */
    TABLE_DATA_REQUEST(""),

    /** The table data response. */
    TABLE_DATA_RESPONSE(""),

    /** The type unknown. */
    TYPE_UNKNOWN("");

    private static final Logger logger = LoggerFactory.getLogger(MessageType.class);

    /** The text. */
    private String text;

    /**
     * Instantiates a new message type.
     *
     * @param text the text
     */
    MessageType(String text) {
        this.text = text;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return this.text;
    }

    /**
     * From string.
     *
     * @param text the text
     * @return the message type
     */
    public static MessageType fromString(String text) {
        if (text != null) {
            for (MessageType b : MessageType.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        logger.error("Unable to get MessageType from string.");
        return null;
    }

}
