package com.lss.onevision.demo.rest;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dao.BookDao;
import com.lss.onevision.demo.book.dto.BookCreateDto;
import com.lss.onevision.demo.book.dto.BookResponseDto;
import com.lss.onevision.demo.book.enums.BookSortField;
import com.lss.onevision.demo.book.exception.BookAlreadyExistsException;
import com.lss.onevision.demo.book.exception.BookNotFoundException;
import com.lss.onevision.demo.book.mapper.BookToResponseDtoConverter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping(
        value = "/books",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class BookController {
    private final BookDao dao;
    private final BookToResponseDtoConverter toResponseDtoConverter;

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        List<BookResponseDto> result = dao.findAll(List.of(Pair.of(BookSortField.title, Sort.Direction.DESC)))
                .stream()
                .map(toResponseDtoConverter::convert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_OK),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_NOT_FOUND)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getOneBook(
            @Positive @PathVariable("id") long bookId
    ) {
        BookResponseDto dto = dao.findOne(bookId)
                .map(toResponseDtoConverter::convert)
                .orElseThrow(() -> BookNotFoundException.notFoundById(bookId));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/authors")
    public ResponseEntity<LinkedHashMap<String, List<BookResponseDto>>> getAllBooksGroupByAuthor() {
        var result = dao.findAll(
                List.of(
                        Pair.of(BookSortField.author, Sort.Direction.ASC),
                        Pair.of(BookSortField.title, Sort.Direction.DESC)
                ))
                .stream()
                .collect(Collectors.groupingBy(Book::getAuthor, LinkedHashMap::new,
                        Collectors.mapping(toResponseDtoConverter::convert, Collectors.toList())));
        return ResponseEntity.ok(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_CREATED),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_BAD_REQUEST),
            @ApiResponse(responseCode = "" + HttpServletResponse.SC_CONFLICT)
    })
    @PostMapping
    public ResponseEntity<Void> createBook(@Valid @RequestBody BookCreateDto dto) {
        if (dao.isRecordAlreadyExists(dto)) {
            throw new BookAlreadyExistsException();
        }
        long id = dao.create(dto);
        return ResponseEntity.created(URI.create("/books/" + id)).build();
    }

}
