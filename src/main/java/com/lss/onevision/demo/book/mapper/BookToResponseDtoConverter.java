package com.lss.onevision.demo.book.mapper;

import com.lss.onevision.demo.book.Book;
import com.lss.onevision.demo.book.dto.BookResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookToResponseDtoConverter implements Converter<Book, BookResponseDto> {
    @Override
    public BookResponseDto convert(@NonNull Book source) {
        try {
            return BookResponseDto.builder()
                    .id(source.getId())
                    .author(source.getAuthor())
                    .title(source.getTitle())
                    .description(source.getDescription())
                    .build();
        } catch (Exception ex) {
            log.error("Convert Book to BookResponseDto", ex);
            throw new IllegalArgumentException(ex);
        }
    }
}
