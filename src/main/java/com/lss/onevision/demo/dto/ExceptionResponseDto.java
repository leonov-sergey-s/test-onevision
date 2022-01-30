package com.lss.onevision.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExceptionResponseDto implements Serializable {
    private final int status;
    private final String error;
    private final String message;
}
