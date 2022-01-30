package com.lss.onevision.demo.exception;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
public abstract class ApiRuntimeException extends RuntimeException {
    private final String messageCode;
    private final Object[] messageArgs;

    public ApiRuntimeException(@NonNull String messageCode, @Nullable Object[] messageArgs) {
        super(messageCode);
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }

    public ApiRuntimeException(@NonNull String messageCode) {
        super(messageCode);
        this.messageCode = messageCode;
        this.messageArgs = null;
    }
}
