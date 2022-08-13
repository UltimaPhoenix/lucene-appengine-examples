package com.googlecode.luceneappengine.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import io.micronaut.core.annotation.Introspected;

@Introspected
@JsonClassDescription
public record Hit(String id, String content) {
}
