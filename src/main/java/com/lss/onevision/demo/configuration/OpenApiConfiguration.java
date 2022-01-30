package com.lss.onevision.demo.configuration;

import com.lss.onevision.demo.dto.ExceptionResponseDto;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApi() {
        var errorSchema = ModelConverters.getInstance()
                .read(ExceptionResponseDto.class)
                .getOrDefault("ExceptionResponseDto", new Schema<ExceptionResponseDto>());

        return new OpenAPI()
                .schema("ExceptionResponseDto", errorSchema)
                .info(new Info()
                        .title("Sergey Leonov demo project API")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact().email("lss.umnic@gmail.com"))
                );
    }

    private ApiResponse setErrorDefaultResponse(@NonNull ApiResponse apiResponse) {
        var schema = new Schema<ExceptionResponseDto>();
        schema.set$ref("#/components/schemas/ExceptionResponseDto");
        var content = new Content()
                .addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, new MediaType().schema(schema));

        if (apiResponse.getContent() == null) {
            apiResponse.setContent(content);
        }
        return apiResponse;
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {

            var apiResponses = operation.getResponses();
            apiResponses.computeIfPresent("" + HttpServletResponse.SC_BAD_REQUEST,
                    (s, apiResponse) -> setErrorDefaultResponse(apiResponse));
            apiResponses.computeIfPresent("" + HttpServletResponse.SC_NOT_FOUND,
                    (s, apiResponse) -> setErrorDefaultResponse(apiResponse));
            apiResponses.computeIfPresent("" + HttpServletResponse.SC_CONFLICT,
                    (s, apiResponse) -> setErrorDefaultResponse(apiResponse));

            return operation;
        };
    }

}
