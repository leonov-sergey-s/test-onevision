package com.lss.onevision.demo.book.exception;

import com.lss.onevision.demo.exception.ApiRuntimeException;
import com.lss.onevision.demo.exception.ExceptionMessageKeys;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends ApiRuntimeException {
    public BookAlreadyExistsException() {
        super(ExceptionMessageKeys.Book.ALREADY_EXISTS);
    }
}

