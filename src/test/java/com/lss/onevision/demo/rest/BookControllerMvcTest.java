package com.lss.onevision.demo.rest;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dao.BookDao;
import com.lss.onevision.demo.book.dto.BookCreateDto;
import com.lss.onevision.demo.book.exception.BookAlreadyExistsException;
import com.lss.onevision.demo.book.mapper.BookToResponseDtoConverter;
import com.lss.onevision.demo.test.AbstractWebMvcTest;
import com.lss.onevision.demo.test.data.WithBookDefaultData;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
class BookControllerMvcTest extends AbstractWebMvcTest implements WithBookDefaultData {
    @MockBean
    private BookDao dao;
    @SuppressWarnings("unused")
    @InjectMocks
    private final BookToResponseDtoConverter toResponseDtoConverter = new BookToResponseDtoConverter();

    @Test
    void getAllBooks() throws Exception {
        List<Book> dtoList = List.of(mock(Book.class), mock(Book.class), mock(Book.class));

        when(dao.findAll(anyIterable())).thenReturn(dtoList);

        mockMvc.perform(
                get("/books")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(dtoList.size())));
    }

    @Test
    void getAllBooksIfNotFound() throws Exception {
        mockMvc.perform(
                get("/books")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    void getOneBook() throws Exception {
        long bookId = 99;
        Book book = defaultValidBookBuilder().id(bookId).build();

        when(dao.findOne(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(
                get("/books/" + bookId)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookId));
    }

    @Test
    void getOneBookIfNotFound() throws Exception {
        mockMvc.perform(
                get("/books/99")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void getAllBooksGroupByAuthor() throws Exception {
        mockMvc.perform(
                get("/books/authors")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.anEmptyMap()));
    }

    @Test
    void getAllBooksGroupByAuthorIfNotFound() throws Exception {

        List<Book> dtoList = List.of(
                defaultValidBookBuilder().id(1).author("author1").build(),
                defaultValidBookBuilder().id(2).author("author1").build(),
                defaultValidBookBuilder().id(3).author("author2").build(),
                defaultValidBookBuilder().id(4).author("author2").build(),
                defaultValidBookBuilder().id(5).author("author3").build()
        );

        when(dao.findAll(anyIterable())).thenReturn(dtoList);

        mockMvc.perform(
                get("/books/authors")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.author1", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.author2", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.author3", Matchers.hasSize(1)));
    }

    @Test
    void createBook() throws Exception {
        BookCreateDto dto = defaultValidBookCreateDtoBuilder().build();
        long bookId = 42;
        when(dao.create(any(BookCreateDto.class))).thenReturn(bookId);

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(header().string(HttpHeaders.LOCATION, "/books/" + bookId));
    }

    @Test
    void createBookIfAlreadyExist() throws Exception {
        BookCreateDto dto = defaultValidBookCreateDtoBuilder().build();
        long bookId = 42;
        when(dao.create(any(BookCreateDto.class))).thenThrow(new BookAlreadyExistsException());

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void createBookIfIncorrectRequest() throws Exception {
        BookCreateDto dto = defaultValidBookCreateDtoBuilder().author("").build();

        mockMvc.perform(
                post("/books")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

}