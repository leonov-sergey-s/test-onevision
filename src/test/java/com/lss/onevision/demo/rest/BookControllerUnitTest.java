package com.lss.onevision.demo.rest;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dao.BookDao;
import com.lss.onevision.demo.book.dto.BookCreateDto;
import com.lss.onevision.demo.book.dto.BookResponseDto;
import com.lss.onevision.demo.book.enums.BookSortField;
import com.lss.onevision.demo.book.exception.BookAlreadyExistsException;
import com.lss.onevision.demo.book.exception.BookNotFoundException;
import com.lss.onevision.demo.book.mapper.BookToResponseDtoConverter;
import com.lss.onevision.demo.test.data.WithBookDefaultData;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerUnitTest implements WithAssertions, WithBookDefaultData {
    @Mock
    private BookDao dao;
    @Mock
    private BookToResponseDtoConverter toResponseDtoConverter;
    @InjectMocks
    private BookController controller;

    @Test
    void getAllBooks() {
        var listOfBooks = List.of(
                Mockito.mock(Book.class),
                Mockito.mock(Book.class),
                Mockito.mock(Book.class)
        );
        when(dao.findAll(anyIterable())).thenReturn(listOfBooks);

        BookResponseDto bookResponseDtoMock = Mockito.mock(BookResponseDto.class);
        when(toResponseDtoConverter.convert(any(Book.class))).thenReturn(bookResponseDtoMock);

        ResponseEntity<List<BookResponseDto>> responseEntity = controller.getAllBooks();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().size()).isEqualTo(listOfBooks.size());

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(toResponseDtoConverter, times(listOfBooks.size())).convert(bookArgumentCaptor.capture());
        verifyNoMoreInteractions(toResponseDtoConverter);

        assertThat(bookArgumentCaptor.getAllValues()).containsExactlyElementsOf(listOfBooks);

        var sortInfo = List.of(Pair.of(BookSortField.title, Sort.Direction.DESC));
        verify(dao).findAll(sortInfo);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void getOneBookIfNotFound() {
        long bookId = 42;
        when(dao.findOne(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(BookNotFoundException.class)
                .isThrownBy(() -> controller.getOneBook(bookId));

        verify(dao).findOne(bookId);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void getOneBook() {
        Book bookMock = Mockito.mock(Book.class);
        when(dao.findOne(anyLong())).thenReturn(Optional.of(bookMock));

        BookResponseDto bookResponseDto = Mockito.mock(BookResponseDto.class);
        when(toResponseDtoConverter.convert(any(Book.class))).thenReturn(bookResponseDto);

        long bookId = 42;
        ResponseEntity<BookResponseDto> responseEntity = controller.getOneBook(bookId);

        verify(dao).findOne(bookId);
        verify(toResponseDtoConverter).convert(bookMock);
        verifyNoMoreInteractions(dao, toResponseDtoConverter);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(bookResponseDto);
    }

    @Test
    void getAllBooksGroupByAuthor() {
        var listOfBooks = List.of(
                defaultValidBookBuilder().id(1).author("author11").build(),
                defaultValidBookBuilder().id(2).author("author21").build(),
                defaultValidBookBuilder().id(3).author("author21").build(),
                defaultValidBookBuilder().id(4).author("author41").build()
        );
        when(dao.findAll(anyIterable())).thenReturn(listOfBooks);

        BookResponseDto bookResponseDtoMock = Mockito.mock(BookResponseDto.class);
        when(toResponseDtoConverter.convert(any(Book.class))).thenReturn(bookResponseDtoMock);

        ResponseEntity<LinkedHashMap<String, List<BookResponseDto>>> responseEntity = controller.getAllBooksGroupByAuthor();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        var result = responseEntity.getBody();

        assertThat(result).isNotNull();
        assertThat(result.keySet()).containsExactly("author11", "author21", "author41");
        assertThat(result.get("author11").size()).isEqualTo(1);
        assertThat(result.get("author21").size()).isEqualTo(2);
        assertThat(result.get("author41").size()).isEqualTo(1);

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(toResponseDtoConverter, times(listOfBooks.size())).convert(bookArgumentCaptor.capture());
        verifyNoMoreInteractions(toResponseDtoConverter);

        assertThat(bookArgumentCaptor.getAllValues()).containsExactlyElementsOf(listOfBooks);

        var sortInfo = List.of(
                Pair.of(BookSortField.author, Sort.Direction.ASC),
                Pair.of(BookSortField.title, Sort.Direction.DESC)
        );
        verify(dao).findAll(sortInfo);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void createIfBookAlreadyExists() {
        BookCreateDto createDto = defaultValidBookCreateDtoBuilder().build();

        when(dao.isRecordAlreadyExists(any(BookCreateDto.class))).thenReturn(true);

        assertThatExceptionOfType(BookAlreadyExistsException.class)
                .isThrownBy(() -> controller.createBook(createDto));

        verify(dao).isRecordAlreadyExists(createDto);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void createBook() {
        BookCreateDto createDto = defaultValidBookCreateDtoBuilder().build();
        long bookId = 42;

        when(dao.create(any(BookCreateDto.class))).thenReturn(bookId);

        ResponseEntity<Void> responseEntity = controller.createBook(createDto);

        verify(dao).isRecordAlreadyExists(createDto);
        verify(dao).create(createDto);
        verifyNoMoreInteractions(dao);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNull();
        assertThat(responseEntity.getHeaders().getLocation()).isEqualTo(URI.create("/books/" + bookId));
    }

}