package edu.java.bot.controller;

import edu.java.bot.service.UserService;
import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
@Log4j2
public class UpdatesController implements UpdatesApi {

    private final UserService userService;

    @Override
    public void sendUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        userService.sendUpdates(
            linkUpdateRequest.url(),
            linkUpdateRequest.tgChatIds(),
            linkUpdateRequest.linkUpdateInfo()
        );
    }

}
