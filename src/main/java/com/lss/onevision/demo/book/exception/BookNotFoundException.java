package com.lss.onevision.demo.book.exception;

import com.lss.onevision.demo.exception.ApiRuntimeException;
import com.lss.onevision.demo.exception.ExceptionMessageKeys;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends ApiRuntimeException {

    private BookNotFoundException(@NonNull String messageCode, @Nullable Object[] messageArgs) {
        super(messageCode, messageArgs);
    }

    public static BookNotFoundException notFoundById(long bookId) {
        return new BookNotFoundException(ExceptionMessageKeys.Book.NOT_FOUND_BY_ID, new Object[]{bookId});
    }
}
