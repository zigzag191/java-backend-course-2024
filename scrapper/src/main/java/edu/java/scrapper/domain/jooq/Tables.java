/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq;


import edu.java.scrapper.domain.jooq.tables.Link;
import edu.java.scrapper.domain.jooq.tables.TgChat;
import edu.java.scrapper.domain.jooq.tables.TrackInfo;

import javax.annotation.processing.Generated;


/**
 * Convenience access to all tables in the default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tables {

    /**
     * The table <code>LINK</code>.
     */
    public static final Link LINK = Link.LINK;

    /**
     * The table <code>TG_CHAT</code>.
     */
    public static final TgChat TG_CHAT = TgChat.TG_CHAT;

    /**
     * The table <code>TRACK_INFO</code>.
     */
    public static final TrackInfo TRACK_INFO = TrackInfo.TRACK_INFO;
}
