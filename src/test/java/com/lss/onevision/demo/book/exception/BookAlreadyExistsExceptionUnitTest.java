package com.lss.onevision.demo.book.exception;

import com.lss.onevision.demo.test.AbstractApiRuntimeExceptionUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BookAlreadyExistsExceptionUnitTest extends AbstractApiRuntimeExceptionUnitTest {

    @Test
    @Override
    public void testApiRuntimeException() {
        var exception = new BookAlreadyExistsException();

        testResponseStatus(exception, HttpStatus.CONFLICT);
        testMessageSource(exception, "exception.book.already_exists", null);
        testMessageText(exception, "Book with same data already exists", defaultLocale);

    }

}