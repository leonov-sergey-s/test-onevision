package com.lss.onevision.demo.book.dao;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dto.BookCreateDto;
import com.lss.onevision.demo.book.enums.BookSortField;
import com.lss.onevision.demo.book.mapper.BookRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BookDaoImpl implements BookDao {

    private final JdbcTemplate jdbcTemplate;
    private final BookRowMapper bookRowMapper;

    @Override
    public Optional<Book> findOne(long id) {
        try {
            String sql = String.format("select %s, %s, %s, %s from %s where id = ?",
                    COLUMN_ID, COLUMN_AUTHOR, COLUMN_TITLE, COLUMN_DESCRIPTION,
                    TABLE_NAME);
            Book result = jdbcTemplate.queryForObject(sql, new Object[]{id},
                    new int[]{Types.BIGINT}, bookRowMapper);
            return Optional.ofNullable(result);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll(Iterable<Pair<BookSortField, Sort.Direction>> sortInfo) {
        String sortStr = StreamSupport.stream(sortInfo.spliterator(), false)
                .map(p -> p.getFirst().getSortField() + " " + p.getSecond())
                .collect(Collectors.joining(", "));

        String sql = String.format("select %s, %s, %s, %s from %s order by %s, %s",
                COLUMN_ID, COLUMN_AUTHOR, COLUMN_TITLE, COLUMN_DESCRIPTION,
                TABLE_NAME,
                sortStr, COLUMN_ID);
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    @Override
    public boolean isRecordAlreadyExists(@lombok.NonNull BookCreateDto dto) {
        String sql = String.format("select count(*) from %s where %s = ? and %s = ?",
                TABLE_NAME,
                COLUMN_AUTHOR, COLUMN_TITLE);

        Integer count = jdbcTemplate.queryForObject(sql,
                new Object[]{dto.getAuthor(), dto.getTitle()},
                new int[]{Types.VARCHAR, Types.VARCHAR},
                Integer.class);
        return count != null && count != 0;
    }

    @Override
    public long create(@lombok.NonNull BookCreateDto dto) {
        String sql = String.format("insert into %s (%s, %s, %s, %s) values (book_seq.nextval, ?, ?, ?)",
                TABLE_NAME,
                COLUMN_ID, COLUMN_AUTHOR, COLUMN_TITLE, COLUMN_DESCRIPTION);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{COLUMN_ID});
                    ps.setString(1, dto.getAuthor());
                    ps.setString(2, dto.getTitle());
                    ps.setString(3, dto.getDescription());
                    return ps;
                }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

}
