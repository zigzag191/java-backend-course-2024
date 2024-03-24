package edu.java.scrapper.domain.service.jpa.entity;

import edu.java.scrapper.domain.model.LinkType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "link")
@Data
@NoArgsConstructor
public class LinkEntity {

    public LinkEntity(URI url, LinkType type, OffsetDateTime lastPolled) {
        this.url = url;
        this.type = type;
        this.lastPolled = lastPolled;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private int linkId;

    @Convert(converter = UriConverter.class)
    @Column(unique = true)
    private URI url;

    @Enumerated(EnumType.STRING)
    private LinkType type;

    @Column(name = "last_polled")
    private OffsetDateTime lastPolled;

    @ManyToMany(mappedBy = "trackedLinks")
    private List<TgChatEntity> trackingChats;

}
