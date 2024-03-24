package edu.java.scrapper.domain.service.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tg_chat")
@Data
@NoArgsConstructor
public class TgChatEntity {

    public TgChatEntity(int chatId) {
        this.chatId = chatId;
    }

    @Id
    @Column(name = "chat_id")
    private int chatId;

    @ManyToMany
    @JoinTable(
        name = "track_info",
        joinColumns = @JoinColumn(name = "tg_chat"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private List<LinkEntity> trackedLinks;

}
