package edu.java.scrapper.domain.model;

import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
