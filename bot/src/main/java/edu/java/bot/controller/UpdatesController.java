package edu.java.bot.controller;

import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.LinkUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@Log4j2
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры запроса",
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
})
public class UpdatesController {

    @Operation(summary = "Отправить обновление")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Обновление обработано"),
        @ApiResponse(
            responseCode = "404",
            description = "Чат не существует",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping
    public void sendUpdate(@RequestBody LinkUpdateRequest linkUpdateRequest) {
        log.info("POST /updates endpoint was triggered");
    }

}
