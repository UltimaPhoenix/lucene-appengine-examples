package com.googlecode.luceneappengine.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.QueryValue;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

//@Introspected
//@JsonClassDescription
//public record IndexSearchRequest(
//        @NotNull
//        @NotBlank
//        @QueryValue(value = "q")
//        String query,
//        @QueryValue(value = "p", defaultValue = "0")
//        @Nullable
//        @PositiveOrZero
//        int page,
//        @QueryValue(value = "s", defaultValue = "10")
//        @Nullable
//        @PositiveOrZero
//        int size) {
//}
@Introspected
@JsonClassDescription
public class IndexSearchRequest {

        @NotNull
        @NotBlank
        @QueryValue(value = "q")
        public String query;

        @QueryValue(value = "p", defaultValue = "0")
        @Nullable
        @PositiveOrZero
        public int page;

        @QueryValue(value = "s", defaultValue = "10")
        @Nullable
        @PositiveOrZero
        public int size;

}
