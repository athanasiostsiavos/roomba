package com.atsiavos.roomba.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorExceptionHandlerTest  {

    @Test
    public void testHandleIllegalArgumentException() {

        ErrorExceptionHandler handler = new ErrorExceptionHandler();
        IllegalArgumentException exception = new IllegalArgumentException("Out of bounds position");

        ResponseEntity<Object> response = handler.handleIllegalArgumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Out of bounds position", ((java.util.Map)response.getBody()).get("error"));
    }
}

