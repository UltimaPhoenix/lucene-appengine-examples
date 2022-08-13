package com.googlecode.luceneappengine.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.QueryValue;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Introspected
@JsonClassDescription
public record IndexSearchResult (
        String index,
        String query,
        List<Hit> hits,
        int currentPage,
        int totalPages,
        int pageSize,
        int totalHits,
        boolean maxHits) {
}
