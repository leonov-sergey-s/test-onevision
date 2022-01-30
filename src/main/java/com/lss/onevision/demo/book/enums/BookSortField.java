package com.lss.onevision.demo.book.enums;

import com.lss.onevision.demo.book.dao.BookDao;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookSortField {
    author(BookDao.COLUMN_AUTHOR),
    title(BookDao.COLUMN_TITLE);

    @Getter
    private final String sortField;
}
