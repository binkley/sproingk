package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
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
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.util.LinkedMultiValueMap
import java.net.URI

@DisplayName("GIVEN a running application on a random port")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class GreetingControllerIT {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    @Autowired
    private lateinit var repository: TestingGreetingRepository

    @DisplayName("WHEN greet URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenNew() {
            repository.state = null

            greet("Brian").andExpect(ACCEPTED, "Brian", PENDING,
                    0).andRedirectTo("/queue/Brian")
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is in percentage")
    @Nested
    inner class BatchInProgress {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenPending() {
            repository.state = PENDING

            greet("Brian").andExpect(ACCEPTED, "Brian", PENDING,
                    0).andRedirectTo("/queue/Brian")
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is ready")
    @Nested
    inner class BatchReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForBatchWhenComplete() {
            repository.state = COMPLETE

            greet("Brian").andExpect(SEE_OTHER, "Brian", COMPLETE,
                    100).andRedirectTo("/greetings/Brian")
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is new")
    @Nested
    inner class QueueNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForQueueWhenNew() {
            repository.state = null

            GET("/queue/Brian").andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is in percentage")
    @Nested
    inner class QueueInProgress {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondForQueueWhenPending() {
            repository.state = PENDING

            GET("/queue/Brian").andExpect(OK, "Brian", PENDING, 0)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is ready")
    @Nested
    inner class QueueReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForQueueWhenComplete() {
            repository.state = COMPLETE

            GET("/queue/Brian").andExpect(SEE_OTHER, "Brian", COMPLETE,
                    100).andRedirectTo("/greetings/Brian")
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is deleted")
    @Nested
    inner class QueueDelete {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForQueueWhenDeleted() {
            repository.state = PENDING

            DELETE("/queue/Brian").andExpect(NO_CONTENT)
            GET("/queue/Brian").andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is new")
    @Nested
    inner class ReadyNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenNew() {
            repository.state = null

            GET("/greetings/Brian").andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is in percentage")
    @Nested
    inner class ReadyInProgress {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenPending() {
            repository.state = PENDING

            GET("/greetings/Brian").andExpect(NOT_FOUND)
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
  content: "Brian",
  status: {
    "name": "Brian",
    "state": "COMPLETE",
    "percentage": 100
  }
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

            DELETE("/greetings/Brian").andExpect(NO_CONTENT)
            GET("/greetings/Brian").andExpect(NOT_FOUND)
        }
    }

    private fun POST(path: String, beginGreeting: String):
            ResponseEntity<String> = restTemplate.postForEntity(path,
            HttpEntity(beginGreeting,
                    LinkedMultiValueMap(mapOf(Pair(CONTENT_TYPE,
                            listOf(APPLICATION_JSON_UTF8_VALUE))))),
            String::class.java)

    private fun greet(name: String): ResponseEntity<String> {
        return POST("/greetings", """
{
    "name": "$name"
}
""")
    }

    private fun GET(path: String) = restTemplate.getForEntity(path,
            String::class.java)

    private fun DELETE(path: String) = restTemplate.exchange(path,
            HttpMethod.DELETE, null, Void::class.java)

    private fun <T> ResponseEntity<T>.andExpect(status: HttpStatus):
            ResponseEntity<T> {
        assertEquals(status, this.statusCode)
        return this
    }

    private fun <T> ResponseEntity<T>.andExpect(
            status: HttpStatus, name: String, state: State, percentage: Int):
            ResponseEntity<T> {
        assertEquals(status, this.statusCode)
        JSONAssert.assertEquals("""
{
    "name": "$name",
    "state": "$state",
    "percentage": $percentage
}
""",
                this.body.toString(), STRICT)
        return this
    }

    private fun <T> ResponseEntity<T>.andRedirectTo(
            location: String): ResponseEntity<T> {
        assertEquals(URI.create(location), this.headers.location)
        return this
    }
}
