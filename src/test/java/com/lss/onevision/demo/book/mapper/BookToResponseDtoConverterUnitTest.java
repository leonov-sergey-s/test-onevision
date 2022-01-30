package com.lss.onevision.demo.book.mapper;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dto.BookResponseDto;
import com.lss.onevision.demo.test.data.WithBookDefaultData;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BookToResponseDtoConverterUnitTest implements WithAssertions, WithBookDefaultData {
    private final BookToResponseDtoConverter converter = new BookToResponseDtoConverter();

    @Test
    void convert() {
        Book source = defaultValidBookBuilder().build();

        BookResponseDto dto = converter.convert(source);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(source.getId());
        assertThat(dto.getAuthor()).isEqualTo(source.getAuthor());
        assertThat(dto.getTitle()).isEqualTo(source.getTitle());
        assertThat(dto.getDescription()).isEqualTo(source.getDescription());
    }

    @Test
    void convertIfIllegalArgumentException() {
        Book source = Mockito.mock(Book.class);
        String exMessage = "BookToResponseDtoConverterUnitTest: test exception";

        when(source.getId()).thenThrow(new RuntimeException(exMessage));

        var thrown = assertThrows(IllegalArgumentException.class, () -> converter.convert(source));

        assertThat(thrown.getCause().getMessage()).isEqualTo(exMessage);
    }

}