package com.lss.onevision.demo.book;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class Book {
    private final long id;
    private final String title;
    private final String author;
    private final String description;
}
