package com.lss.onevision.demo.book.mapper;

import com.lss.onevision.demo.annotation.JdbcTemplateRowMapper;
import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dao.BookDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@JdbcTemplateRowMapper
public class BookRowMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Book.builder()
                .id(rs.getLong(BookDao.COLUMN_ID))
                .author(rs.getString(BookDao.COLUMN_AUTHOR))
                .title(rs.getString(BookDao.COLUMN_TITLE))
                .description(rs.getString(BookDao.COLUMN_DESCRIPTION))
                .build();
    }
}
