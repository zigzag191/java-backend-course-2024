package edu.java.scrapper.domain.service;

import edu.java.common.dto.linkupdate.LinkUpdateInfo;
import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import edu.java.scrapper.domain.service.exception.KafkaException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {

    private final KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;

    public void send(long id, URI link, String description, List<Long> tgChatIds, LinkUpdateInfo info) {
        try {
            kafkaTemplate.sendDefault(new LinkUpdateRequest(id, link, description, tgChatIds, info)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaException("unable to send message", e);
        }
    }

}
