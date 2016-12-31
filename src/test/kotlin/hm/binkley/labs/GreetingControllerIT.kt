package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import hm.binkley.labs.State.NONE
import hm.binkley.labs.State.PENDING
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
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.HttpStatus.TEMPORARY_REDIRECT
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.net.URI

@DisplayName("GIVEN a running application on a random port")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class GreetingControllerIT {
    @Autowired lateinit private var restTemplate: TestRestTemplate
    @Autowired lateinit var repository: TestingGreetingRepository

    @DisplayName("WHEN greet URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenNew() {
            repository.state = NONE

            val entity = GET("/greet/Brian")
            assertEquals(URI.create("/queue/Brian"), entity.headers.location)
            entity.andExpect(TEMPORARY_REDIRECT, PENDING)
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is in progress")
    @Nested
    inner class BatchInProgress {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenPending() {
            repository.state = PENDING

            val entity = GET("/greet/Brian")
            assertEquals(URI.create("/queue/Brian"), entity.headers.location)
            entity.andExpect(TEMPORARY_REDIRECT, PENDING)
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is ready")
    @Nested
    inner class BatchReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForBatchWhenComplete() {
            repository.state = COMPLETE

            val entity = GET("/greet/Brian")
            assertEquals(URI.create("/greetings/Brian"),
                    entity.headers.location)
            entity.andExpect(SEE_OTHER, COMPLETE)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is new")
    @Nested
    inner class QueueNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForQueueWhenNew() {
            repository.state = NONE

            GET("/queue/Brian").andExpect(NOT_FOUND, NONE)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is in progress")
    @Nested
    inner class QueueInProgress {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondForQueueWhenPending() {
            repository.state = PENDING

            GET("/queue/Brian").andExpect(OK, PENDING)
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
            assertEquals(URI.create("/greetings/Brian"),
                    entity.headers.location)
            entity.andExpect(SEE_OTHER, COMPLETE)
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
            GET("/queue/Brian").andExpect(NOT_FOUND, NONE)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is new")
    @Nested
    inner class ReadyNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenNew() {
            repository.state = NONE

            GET("/greetings/Brian").andExpect(NOT_FOUND, NONE)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is in progress")
    @Nested
    inner class ReadyInProgress {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenPending() {
            repository.state = PENDING

            GET("/greetings/Brian").andExpect(NOT_FOUND, NONE)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is ready")
    @Nested
    inner class ReadyReady {
        @DisplayName("THEN it gives warm greetings")
        @Test
        fun shouldRespondForReadyWhenComplete() {
            repository.state = COMPLETE

            val entity = GET("/greetings/Brian")
            assertEquals(OK, entity.statusCode)
            JSONAssert.assertEquals("""
{
  content: "Brian"
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

            DELETE("/greetings/Brian")
            GET("/greetings/Brian").andExpect(NOT_FOUND, NONE)
        }
    }

    private fun GET(path: String) = restTemplate.getForEntity(path,
            String::class.java)

    private fun DELETE(path: String) = restTemplate.delete(path)

    private fun <T> ResponseEntity<T>.andExpect(status: HttpStatus,
            state: State):
            ResponseEntity<T> {
        assertEquals(status, this.statusCode)
        JSONAssert.assertEquals("""
{
  state: "$state"
}
""",
                this.body.toString(), STRICT)
        return this
    }
}
