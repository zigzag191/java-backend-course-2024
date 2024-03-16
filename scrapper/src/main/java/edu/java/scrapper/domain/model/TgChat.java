package edu.java.scrapper.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TgChat {

    private Long chatId;

    public TgChat(Long chatId) {
        this.chatId = chatId;
    }

}
