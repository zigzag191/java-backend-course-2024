package edu.java.common.dto.linkupdate;

import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StackoverflowQuestionUpdateInfo extends LinkUpdateInfo {

    private List<URI> newComments;
    private List<URI> newAnswers;

}
