package edu.java.scrapper.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URI;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class Link {

    private Long linkId;
    private URI url;
    private LinkType type;
    private OffsetDateTime lastPolled;

    public Link(URI url, LinkType type, OffsetDateTime lastPolled) {
        this.url = url;
        this.type = type;
        this.lastPolled = lastPolled;
    }

}
