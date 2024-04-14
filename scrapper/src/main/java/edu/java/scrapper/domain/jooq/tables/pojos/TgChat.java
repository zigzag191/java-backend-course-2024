/*
 * This file is generated by jOOQ.
 */
package edu.java.scrapper.domain.jooq.tables.pojos;


import java.beans.ConstructorProperties;
import java.io.Serializable;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TgChat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer chatId;

    public TgChat() {}

    public TgChat(TgChat value) {
        this.chatId = value.chatId;
    }

    @ConstructorProperties({ "chatId" })
    public TgChat(
        @NotNull Integer chatId
    ) {
        this.chatId = chatId;
    }

    /**
     * Getter for <code>TG_CHAT.CHAT_ID</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public Integer getChatId() {
        return this.chatId;
    }

    /**
     * Setter for <code>TG_CHAT.CHAT_ID</code>.
     */
    public void setChatId(@NotNull Integer chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TgChat other = (TgChat) obj;
        if (this.chatId == null) {
            if (other.chatId != null)
                return false;
        }
        else if (!this.chatId.equals(other.chatId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.chatId == null) ? 0 : this.chatId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TgChat (");

        sb.append(chatId);

        sb.append(")");
        return sb.toString();
    }
}
