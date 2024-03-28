package edu.java.scrapper.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackInfo {

    private Link link;
    private TgChat tgChat;

}
