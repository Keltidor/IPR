package com.example.ipr.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Глобальный обработчик исключений для службы пользователя.
 * Этот класс обрабатывает исключения, возникающие в службе пользователя, и предоставляет
 * структурированные ответы на запросы с различными типами исключений.
 */
@Slf4j
@ControllerAdvice
public class UserServiceExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Обрабатывает исключение общего типа {@link Exception}. Логирует исключение и возвращает
     * ответ на запрос с ошибкой внутренней ошибки сервера.
     *
     * @param e       Исключение, которое возникло во время выполнения приложения.
     * @param request Запрос, который привел к исключению.
     * @return Ответ с информацией об ошибке внутренней ошибки сервера.
     * @throws Exception Исключение, если обработка завершилась неудачно.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception e, WebRequest request) throws Exception {
        log.error("Exception during execution of application", e);
        return handleException(e, request);
    }

    /**
     * Обрабатывает исключение типа {@link BadRequestException}. Логирует исключение и возвращает
     * ответ с информацией об ошибке неверного запроса.
     *
     * @param e Исключение неверного запроса.
     * @return Ответ с информацией об ошибке неверного запроса.
     */
    @ExceptionHandler(value = BadRequestException.class)
    @ResponseBody
    public ResponseEntity<ErrorDto> badRequestException(BadRequestException e) {
        log.error("Bad Request Exception: ", e);
        ErrorDto dto = getResponseErrorDto(e);
        return ResponseEntity.status(dto.getErrorStatus()).body(dto);
    }

    /**
     * Создает объект {@link ErrorDto} на основе информации из исключения неверного запроса.
     *
     * @param e Исключение неверного запроса.
     * @return Объект {@link ErrorDto} с информацией об ошибке неверного запроса.
     */
    private ErrorDto getResponseErrorDto(BadRequestException e) {
        return ErrorDto.builder().
                error(e.getMessage()).
                errorStatus(e.getStatus())
                .build();
    }
}
