package com.googlecode.luceneappengine;

import com.googlecode.luceneappengine.dto.Hit;
import com.googlecode.luceneappengine.dto.IndexSearchRequest;
import com.googlecode.luceneappengine.dto.IndexSearchResult;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.exceptions.HttpStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.micronaut.http.HttpStatus.NOT_FOUND;
import static io.micronaut.http.MediaType.APPLICATION_JSON;
import static io.micronaut.http.MediaType.TEXT_PLAIN;
import static java.util.stream.Collectors.toList;

@Controller("/index")
public class IndexController {

    private static final int MAX_HITS = 200;

    List<String> indexes = Stream.of("defaultIndex", "stoca").collect(toList());

    List<Hit> allList = IntStream.range(1, 32)
            .mapToObj(idx -> new Hit(String.valueOf(idx), idx % 2 == 0 ? "foo" + idx : "bar" + idx))
            .collect(toList());

    @Get(uri = "/status", produces = TEXT_PLAIN)
    public String status() {
        return "App is up";
    }

    @Get(uri = "/")
    public List<String> listIndexes() {
        return indexes;
    }

    @Post(uri = "/{index}")
    public void createIndex(String index) {
        indexes.add(index);
    }

    @Delete(uri = "/{index}")
    public void deleteIndex(String index) {
        indexes.remove(index);
    }

    @Post(uri = "/{index}/purge")
    public void purgeIndex(String index) {
        allList.clear();
    }

    @Get(uri = "/{index}/doc{?searchRequest*}")
    @SingleResult
    public IndexSearchResult search(String index, @RequestBean @Valid IndexSearchRequest searchRequest) {
        if (!indexes.contains(index)) {
            throw new HttpStatusException(NOT_FOUND, "index not found");
        }
        return new IndexSearchResult(index,searchRequest.query,
                allList.subList(searchRequest.page*searchRequest.size, Math.min(allList.size(), searchRequest.page*searchRequest.size + searchRequest.size)),
                searchRequest.page, (allList.size() - 1) / searchRequest.size + 1, searchRequest.size,
                allList.size(),
                 allList.size() >= MAX_HITS
        );
    }

    @Post(uri = "/{index}/doc")
    public void indexText(String index, @Body String text) {
        allList.add(new Hit(UUID.randomUUID().toString(), text));
    }

    @Delete(uri = "/{index}/doc/{docId}")
    public void deindexText(String index, String docId) {
        allList.removeIf(hit -> hit.id().equals(docId));
    }



}
