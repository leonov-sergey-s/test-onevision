package com.lss.onevision.demo.book.enums;

import com.lss.onevision.demo.book.dao.BookDao;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

class BookSortFieldUnitTest implements WithAssertions {

    @Test
    void validateSortColumns() {
        assertAll(
                () -> assertThat(BookSortField.author.getSortField()).isEqualTo(BookDao.COLUMN_AUTHOR),
                () -> assertThat(BookSortField.title.getSortField()).isEqualTo(BookDao.COLUMN_TITLE)
        );
    }

}