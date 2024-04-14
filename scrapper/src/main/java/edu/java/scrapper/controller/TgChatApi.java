package edu.java.scrapper.controller;

import edu.java.common.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
@ApiResponses({
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
public interface TgChatApi {

    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
        @ApiResponse(responseCode = "409",
                     description = "Чат уже существует",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
    @PostMapping("/{chatId}")
    void registerChat(@PathVariable long chatId);

    @Operation(summary = "Удалить чат")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
        @ApiResponse(responseCode = "404",
                     description = "Чат не существует",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
    @DeleteMapping("/{chatId}")
    void deleteChat(@PathVariable long chatId);

}
