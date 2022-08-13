package com.googlecode.luceneappengine

import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification
import jakarta.inject.Inject

@MicronautTest
class ApplicationUpIntegrationTest extends Specification {

    @Inject
    EmbeddedApplication<?> application

    void 'test it works'() {
        expect: application.running
    }

}
