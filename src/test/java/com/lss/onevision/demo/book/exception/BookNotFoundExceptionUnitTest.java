package com.lss.onevision.demo.book.exception;

import com.lss.onevision.demo.test.AbstractApiRuntimeExceptionUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BookNotFoundExceptionUnitTest extends AbstractApiRuntimeExceptionUnitTest {

    @Test
    @Override
    public void testApiRuntimeException() {
        long bookId = 99;
        var exception = BookNotFoundException.notFoundById(bookId);

        testResponseStatus(exception, HttpStatus.NOT_FOUND);
        testMessageSource(exception, "exception.book.not_found_by_id", new Object[]{bookId});
        testMessageText(exception, "Book not found", defaultLocale);

    }

}