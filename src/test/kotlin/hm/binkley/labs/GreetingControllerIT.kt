package hm.binkley.labs

import hm.binkley.labs.TestingGreetingRepository.State.COMPLETE
import hm.binkley.labs.TestingGreetingRepository.State.NONE
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
import org.springframework.http.HttpStatus.NOT_FOUND
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


    @DisplayName("WHEN batch URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenNew() {
            repository.state = NONE

            val entity = GET("/batch/Brian")
            assertEquals(TEMPORARY_REDIRECT, entity.statusCode)
            assertEquals(URI.create("/queue/Brian"), entity.headers.location)
        }
    }

    @DisplayName("WHEN batch URL is called for <name> AND is in progress")
    @Nested
    inner class BatchInProgress {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenPending() {
            repository.state = PENDING

            val entity = GET("/batch/Brian")
            assertEquals(TEMPORARY_REDIRECT, entity.statusCode)
            assertEquals(URI.create("/queue/Brian"), entity.headers.location)
        }
    }

    @DisplayName("WHEN batch URL is called for <name> AND is ready")
    @Nested
    inner class BatchReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForBatchWhenComplete() {
            repository.state = COMPLETE

            val entity = GET("/batch/Brian")
            assertEquals(SEE_OTHER, entity.statusCode)
            assertEquals(URI.create("/ready/Brian"), entity.headers.location)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is new")
    @Nested
    inner class QueueNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForQueueWhenNew() {
            repository.state = NONE

            val entity = GET("/queue/Brian")
            assertEquals(NOT_FOUND, entity.statusCode)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is in progress")
    @Nested
    inner class QueueInProgress {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondForQueueWhenPending() {
            repository.state = PENDING

            val entity = GET("/queue/Brian")
            assertEquals(OK, entity.statusCode)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is ready")
    @Nested
    inner class QueueReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForQueueWhenComplete() {
            repository.state = COMPLETE

            val entity = GET("/queue/Brian")
            assertEquals(SEE_OTHER, entity.statusCode)
            assertEquals(URI.create("/ready/Brian"), entity.headers.location)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is deleted")
    @Nested
    inner class QueueDelete {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForQueueWhenDeleted() {
            repository.state = PENDING

            DELETE("/queue/Brian")
            val entity = GET("/queue/Brian")
            assertEquals(NOT_FOUND, entity.statusCode)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is new")
    @Nested
    inner class ReadyNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenNew() {
            repository.state = NONE

            val entity = GET("/ready/Brian")
            assertEquals(NOT_FOUND, entity.statusCode)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is in progress")
    @Nested
    inner class ReadyInProgress {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenPending() {
            repository.state = PENDING

            val entity = GET("/ready/Brian")
            assertEquals(NOT_FOUND, entity.statusCode)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is ready")
    @Nested
    inner class ReadyReady {
        @DisplayName("THEN it gives warm greetings")
        @Test
        fun shouldRespondForReadyWhenComplete() {
            repository.state = COMPLETE

            val entity = GET("/ready/Brian")
            assertEquals(OK, entity.statusCode)
            JSONAssert.assertEquals("""
{
  "content": "Brian"
}
""",
                    entity.body, STRICT)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is deleted")
    @Nested
    inner class ReadyDelete {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenDeleted() {
            repository.state = COMPLETE

            DELETE("/ready/Brian")
            val entity = GET("/ready/Brian")
            assertEquals(NOT_FOUND, entity.statusCode)
        }
    }

    private fun GET(path: String) = restTemplate.getForEntity(path,
            String::class.java)

    private fun DELETE(path: String) = restTemplate.delete(path)
}
