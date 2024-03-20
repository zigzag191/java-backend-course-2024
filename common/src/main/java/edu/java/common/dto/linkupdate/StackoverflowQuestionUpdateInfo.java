package edu.java.common.dto.linkupdate;

import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StackoverflowQuestionUpdateInfo extends LinkUpdateInfo {

    private final List<URI> newComments;
    private final List<URI> newAnswers;

}
