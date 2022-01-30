package com.lss.onevision.demo.book.dao;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dto.BookCreateDto;
import com.lss.onevision.demo.book.enums.BookSortField;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    String TABLE_NAME = "book";
    String COLUMN_ID = "id";
    String COLUMN_AUTHOR = "author";
    String COLUMN_TITLE = "title";
    String COLUMN_DESCRIPTION = "description";

    @NonNull
    Optional<Book> findOne(long id);

    @NonNull
    List<Book> findAll(Iterable<Pair<BookSortField, Sort.Direction>> sortInfo);

    long create(@NonNull BookCreateDto dto);

    boolean isRecordAlreadyExists(@NonNull BookCreateDto dto);
}
