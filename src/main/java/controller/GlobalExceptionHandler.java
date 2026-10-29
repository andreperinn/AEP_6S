package controller;

import exception.RecursoEmUsoException;
import exception.ReferenciaInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado() {
        return resposta(HttpStatus.NOT_FOUND, "Recurso nao encontrado");
    }

    @ExceptionHandler(ReferenciaInvalidaException.class)
    public ResponseEntity<Map<String, String>> referenciaInvalida(ReferenciaInvalidaException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(RecursoEmUsoException.class)
    public ResponseEntity<Map<String, String>> recursoEmUso(RecursoEmUsoException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
