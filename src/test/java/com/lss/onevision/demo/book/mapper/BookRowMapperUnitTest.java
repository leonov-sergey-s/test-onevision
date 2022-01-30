package com.lss.onevision.demo.book.mapper;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dao.BookDao;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BookRowMapperUnitTest implements WithAssertions {
    private final BookRowMapper mapper = new BookRowMapper();

    @Test
    void mapRowWhenColumnNotFound() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        String exMessage = "BookRowMapperUnitTest: test SQLException";

        when(rs.getString(anyString())).thenThrow(new SQLException(exMessage));

        var thrown = assertThrows(SQLException.class, () -> mapper.mapRow(rs, 99));

        assertThat(thrown.getMessage()).isEqualTo(exMessage);
    }

    @Test
    void mapRow() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        when(rs.getLong(BookDao.COLUMN_ID)).thenReturn(42L);
        when(rs.getString(BookDao.COLUMN_AUTHOR)).thenReturn("author");
        when(rs.getString(BookDao.COLUMN_TITLE)).thenReturn("title");
        when(rs.getString(BookDao.COLUMN_DESCRIPTION)).thenReturn("description");

        Book book = mapper.mapRow(rs, 99);

        assertThat(book).isNotNull();
        assertThat(book.getId()).isEqualTo(42);
        assertThat(book.getAuthor()).isEqualTo("author");
        assertThat(book.getTitle()).isEqualTo("title");
        assertThat(book.getDescription()).isEqualTo("description");
    }

}