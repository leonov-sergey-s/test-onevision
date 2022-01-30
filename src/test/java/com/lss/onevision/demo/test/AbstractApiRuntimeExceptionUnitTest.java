package com.lss.onevision.demo.test;

import com.lss.onevision.demo.exception.ApiRuntimeException;
import org.assertj.core.api.WithAssertions;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

public abstract class AbstractApiRuntimeExceptionUnitTest implements WithAssertions {
    protected static final Locale defaultLocale = Locale.getDefault();

    protected void testResponseStatus(@NonNull ApiRuntimeException exception, @NonNull HttpStatus status) {
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);

        assertThat(responseStatus).isNotNull();
        assertThat(responseStatus.value()).isEqualTo(status);
    }

    protected void testMessageSource(@NonNull ApiRuntimeException exception, @NonNull String messageCode,
                                     @Nullable Object[] messageArgs) {
        assertThat(exception).isNotNull();
        assertThat(exception.getMessageCode()).isEqualTo(messageCode);
        if (messageArgs != null) {
            assertThat(exception.getMessageArgs()).containsExactly(messageArgs);
        } else {
            assertThat(exception.getMessageArgs()).isNull();
        }

        var source = new ResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setDefaultLocale(new Locale("ru"));
        source.setBasenames("messages");
        source.setUseCodeAsDefaultMessage(true);
    }

    protected void testMessageText(@NonNull ApiRuntimeException exception, @NonNull String expectedMessage,
                                   @NonNull Locale locale) {
        var source = new ResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.setDefaultLocale(locale);
        source.setBasenames("messages");

        var message = source.getMessage(exception.getMessageCode(), exception.getMessageArgs(), Locale.getDefault());
        assertThat(message).isEqualTo(expectedMessage);
    }

    @SuppressWarnings({"unused"})
    public abstract void testApiRuntimeException();

}
