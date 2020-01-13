package technopolisspring.technopolis.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import technopolisspring.technopolis.model.dto.ErrorDto;

import java.time.LocalDateTime;

public abstract class GlobalExceptionHandler {

    @ExceptionHandler({InvalidArgumentsException.class})
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

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto userNotFound(Exception e){
        ErrorDto errorDTO = new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
        return errorDTO;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto wrongDateFormat(Exception e){
        ErrorDto errorDTO = new ErrorDto(
                "Invalid arguments!",
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
        return errorDTO;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto somethingWentWrong(Exception e){
        ErrorDto errorDTO = new ErrorDto(
                "Whoops, something went wrong!",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                e.getClass().getName());
        return errorDTO;
    }

}
