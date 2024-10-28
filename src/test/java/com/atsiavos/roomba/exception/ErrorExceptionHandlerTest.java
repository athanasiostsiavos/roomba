package com.atsiavos.roomba.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ErrorExceptionHandlerTest {

  @Test
  void testHandleIllegalArgumentException() {

    ErrorExceptionHandler handler = new ErrorExceptionHandler();
    IllegalArgumentException exception = new IllegalArgumentException("Out of bounds position");

    ResponseEntity<Object> response = handler.handleIllegalArgumentException(exception);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        "Out of bounds position",
        ((java.util.Map<?, ?>) Objects.requireNonNull(response.getBody())).get("error"));
  }
}
