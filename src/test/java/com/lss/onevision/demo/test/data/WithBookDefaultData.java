package com.lss.onevision.demo.test.data;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dto.BookCreateDto;

public interface WithBookDefaultData {
    default BookCreateDto.BookCreateDtoBuilder defaultValidBookCreateDtoBuilder() {
        return BookCreateDto.builder()
                .author("Lev Nikolayevich Tolstoy")
                .title("War and Peace")
                .description("Very interesting book");
    }

    default Book.BookBuilder defaultValidBookBuilder() {
        return Book.builder()
                .id(2)
                .author("J. K. Rowling.")
                .title("Harry Potter")
                .description("Harry Potter is a series of seven fantasy novels written by British author J. K. Rowling. ");
    }

}
