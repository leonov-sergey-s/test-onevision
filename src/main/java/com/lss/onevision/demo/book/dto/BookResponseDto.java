package com.lss.onevision.demo.book.dto;


import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class BookResponseDto implements Serializable {
    private final long id;
    private final String title;
    private final String author;
    private final String description;
}
