package com.lss.onevision.demo.book.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
public class BookCreateDto {
    @NotBlank
    @Size(max = 150)
    private final String title;
    @NotBlank
    @Size(max = 150)
    private final String author;
    @NotBlank
    @Size(max = 150)
    private final String description;
}
