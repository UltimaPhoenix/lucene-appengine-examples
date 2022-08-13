package com.googlecode.luceneappengine;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Index Manager",
        version = "1.0",
        description = "An example of index manager application",
        license = @License(name = "Apache 2.0", url = "https://bigtable-lucene.appspot.com/")
    )
)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
