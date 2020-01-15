package technopolisspring.technopolis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import technopolisspring.technopolis.model.dto.ErrorDto;

import java.time.LocalDateTime;

public abstract class GlobalExceptionHandler {

    @ExceptionHandler({InvalidArgumentsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto invalidArgoments(Exception e){
        return new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

    @ExceptionHandler({AuthorizationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto authorizationException(Exception e){
        return new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto badRequest(Exception e){
        return new ErrorDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto userNotFound(Exception e){
        return new ErrorDto(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto wrongDateFormat(Exception e){
        return new ErrorDto(
                "Invalid arguments!",
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto somethingWentWrong(Exception e){
        return new ErrorDto(
                "Whoops, something went wrong!",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto urlRequiresArguments(Exception e){
        return new ErrorDto(
                "Url requires arguments",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                e.getClass().getName());
    }

}
