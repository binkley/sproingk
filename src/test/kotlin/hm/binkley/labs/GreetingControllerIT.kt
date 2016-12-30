package hm.binkley.labs

import hm.binkley.labs.TestingGreetingRepository.State.COMPLETE
import hm.binkley.labs.TestingGreetingRepository.State.PENDING
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.HttpStatus.TEMPORARY_REDIRECT
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.net.URI

@DisplayName("GIVEN a running application on a random port")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class GreetingControllerIT {
    @Autowired lateinit private var restTemplate: TestRestTemplate
    @Autowired lateinit var repository: TestingGreetingRepository

    @DisplayName("WHEN root URL is called")
    @Nested
    inner class Root {
        @DisplayName("THEN it says 'Hello, world!'")
        @Test
        fun shouldRespondCheerfully() {
            val entity = get("/")
            assertEquals(OK, entity.statusCode)
            assertEquals("Hello, world!\n", entity.body)
        }
    }

    @DisplayName("WHEN batch URL is called with <name>")
    @Nested
    inner class Name {
        @DisplayName("THEN it redirects to the queue for <name>")
        @Test
        fun shouldRespondCheerfully() {
            val entity = get("/batch/Brian")
            assertEquals(TEMPORARY_REDIRECT, entity.statusCode)
            assertEquals(URI.create("/queue/Brian"), entity.headers.location)
        }
    }

    @DisplayName("WHEN queue URL is called with <name> AND is not ready")
    @Nested
    inner class QueueNotReady {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondCheerfully() {
            repository.state = PENDING

            val entity = get("/queue/Brian")
            assertEquals(OK, entity.statusCode)
        }
    }

    @DisplayName("WHEN queue URL is called with <name> AND is ready")
    @Nested
    inner class QueueReady {
        @DisplayName("THEN it redirects to the finished document for <name>")
        @Test
        fun shouldRespondCheerfully() {
            repository.state = COMPLETE

            val entity = get("/queue/Brian")
            assertEquals(SEE_OTHER, entity.statusCode)
            assertEquals(URI.create("/ready/Brian"), entity.headers.location)
        }
    }

    @DisplayName("WHEN ready URL is called with <name>")
    @Nested
    inner class Ready {
        @DisplayName("THEN it greets <name> warmly")
        @Test
        fun shouldRespondCheerfully() {
            val entity = get("/ready/Brian")
            assertEquals(OK, entity.statusCode)
            JSONAssert.assertEquals("""
{
  "content": "Brian"
}
""",
                    entity.body, STRICT)
        }
    }

    private fun get(path: String) = restTemplate.getForEntity(path,
            String::class.java)
}
