package edu.java.bot.controller;

import edu.java.bot.service.UserService;
import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
@Log4j2
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
})
public class UpdatesController {

    private final UserService userService;

    @Operation(summary = "Отправить обновление")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Обновление обработано"),
        @ApiResponse(
            responseCode = "404",
            description = "Чат не существует",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "501",
            description = "Неизвестный тип ссылки",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping
    public void sendUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        userService.sendUpdates(
            linkUpdateRequest.url(),
            linkUpdateRequest.tgChatIds(),
            linkUpdateRequest.linkUpdateInfo()
        );
    }

}
