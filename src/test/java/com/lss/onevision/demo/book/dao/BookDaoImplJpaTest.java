package com.lss.onevision.demo.book.dao;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dto.BookCreateDto;
import com.lss.onevision.demo.book.enums.BookSortField;
import com.lss.onevision.demo.test.AbstractDataJpaTest;
import com.lss.onevision.demo.test.data.WithBookDefaultData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

class BookDaoImplJpaTest extends AbstractDataJpaTest implements WithBookDefaultData {
    @Autowired
    private BookDao dao;

    @Test
    void findOneIfNotFound() {
        Optional<Book> optionalBook = dao.findOne(999);

        assertThat(optionalBook).isEmpty();
    }

    @Test
    @Sql("books.sql")
    void findOneIfFound() {
        long bookId = 1;

        Optional<Book> optionalBook = dao.findOne(bookId);

        assertThat(optionalBook).isPresent();

        Book book = optionalBook.get();
        assertThat(book.getId()).isEqualTo(bookId);
    }

    @Test
    void findAllIfEmpty() {
        List<Book> bookList = dao.findAll(List.of(Pair.of(BookSortField.author, Sort.Direction.ASC)));

        assertThat(bookList).isNotNull().isEmpty();
    }

    @Test
    @Sql("books.sql")
    void findAllSortByTitleDesc() {
        List<Book> bookList = dao.findAll(List.of(
                Pair.of(BookSortField.title, Sort.Direction.DESC)
        ));

        Book firstBook = bookList.get(0);
        assertThat(firstBook.getId()).isEqualTo(9);
        assertThat(firstBook.getTitle()).isEqualTo("title73");

        Book lastBook = bookList.get(bookList.size() - 1);
        assertThat(lastBook.getId()).isEqualTo(1);
        assertThat(lastBook.getTitle()).isEqualTo("title11");
    }

    @Test
    @Sql("books.sql")
    void findAllSortByAuthorAndTitle() {
        List<Book> bookList = dao.findAll(List.of(
                Pair.of(BookSortField.author, Sort.Direction.ASC),
                Pair.of(BookSortField.title, Sort.Direction.DESC)
        ));

        Book firstBook = bookList.get(0);
        assertThat(firstBook.getId()).isEqualTo(1);
        assertThat(firstBook.getTitle()).isEqualTo("title11");

        Book secondBook = bookList.get(1);
        assertThat(secondBook.getId()).isEqualTo(4);
        assertThat(secondBook.getTitle()).isEqualTo("title23");

        Book lastBook = bookList.get(bookList.size() - 1);
        assertThat(lastBook.getId()).isEqualTo(7);
        assertThat(lastBook.getTitle()).isEqualTo("title71");
    }


    @Test
    @Sql("books.sql")
    void isRecordAlreadyExistsReturnsTrue() {
        BookCreateDto dto = defaultValidBookCreateDtoBuilder()
                .author("author1")
                .title("title11")
                .build();

        boolean result = dao.isRecordAlreadyExists(dto);

        assertThat(result).isTrue();
    }

    @Test
    @Sql("books.sql")
    void isRecordAlreadyExistsReturnsFalse() {
        BookCreateDto dto = defaultValidBookCreateDtoBuilder()
                .author("John Smith")
                .title("John Smith adventures")
                .build();

        boolean result = dao.isRecordAlreadyExists(dto);

        assertThat(result).isFalse();
    }

    @Test
    void createIfSQLConstraintViolationException() {
        BookCreateDto dto = defaultValidBookCreateDtoBuilder().title(null).build();

        assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> dao.create(dto));
    }

    @Test
    void create() {
        Integer countBefore = jdbcTemplate
                .queryForObject("select count(*) from book", Integer.class);

        assertThat(countBefore).isEqualTo(0);

        BookCreateDto dto = defaultValidBookCreateDtoBuilder().build();
        dao.create(dto);

        Integer countAfter = jdbcTemplate
                .queryForObject("select count(*) from book", Integer.class);

        assertThat(countAfter).isEqualTo(1);
    }

}