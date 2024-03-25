package edu.java.bot.controller;

import edu.java.common.dto.LinkUpdateRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class UpdatesController implements UpdatesApi {

    @Override
    public void sendUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        log.info("POST /updates endpoint was triggered");
    }

}
