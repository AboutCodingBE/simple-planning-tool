package be.aboutcoding.simpleplanningtool.worker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WorkerExceptionHandler {

    @ExceptionHandler(WorkerNotFoundException.class)
    public ResponseEntity<Void> handleWorkerNotFoundException(WorkerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
