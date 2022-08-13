package com.googlecode.luceneappengine

import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import static io.micronaut.core.type.Argument.listOf
import static io.micronaut.http.HttpRequest.*

@MicronautTest
class IndexManagerIntegrationTest extends Specification {

    @Inject
    @Client("/index")
    HttpClient http;

    void 'test listIndexes works'() {
        when:
        var result = http.toBlocking().retrieve(
                GET("/").accept(MediaType.APPLICATION_JSON_TYPE),
                listOf(String)
        )
        then:
        result == [ "defaultIndex", "stoca"]
    }

    void 'test search works'() {
        when:
        var result = http.toBlocking().retrieve(
                GET("/defaultIndex/doc?q=foo").accept(MediaType.APPLICATION_JSON_TYPE),
                Map<String, Object>
        )
        then:
        result["totalPages"] == 4
        result["currentPage"] == 0
//        result["hits"] == ["defaultIndex", "stoca"]
    }

    void 'test create and delete index works'() {
        when:

        var createResponse = http.toBlocking().exchange(POST("/new-idx-1", ""))
        var listIndexes1 = http.toBlocking().retrieve(
                GET("/").accept(MediaType.APPLICATION_JSON_TYPE),
                listOf(String)
        )

        var deleteResponse = http.toBlocking().exchange(DELETE("/new-idx-1"))
        var listIndexes2 = http.toBlocking().retrieve(
                GET("/").accept(MediaType.APPLICATION_JSON_TYPE),
                listOf(String)
        )

        then:
        createResponse.status.code == 200
        listIndexes1 == ["defaultIndex", "stoca", "new-idx-1"]
        deleteResponse.status.code == 200
        listIndexes2 == ["defaultIndex", "stoca"]
    }
}