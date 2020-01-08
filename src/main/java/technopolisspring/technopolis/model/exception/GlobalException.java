package technopolisspring.technopolis.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import technopolisspring.technopolis.model.dto.ErrorDto;

import java.time.LocalDateTime;

public abstract class GlobalException {
    @ExceptionHandler({InvalidArguments.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto invalidArgoments(Exception e){
        ErrorDto errorDTO = new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
        return errorDTO;
    }
    @ExceptionHandler({AuthorizationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto authorizationException(Exception e){
        ErrorDto errorDTO = new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
        return errorDTO;
    }
    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto badRequest(Exception e){
        ErrorDto errorDTO = new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
        return errorDTO;
    }
}