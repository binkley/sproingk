package hm.binkley.labs

import hm.binkley.labs.State.COMPLETE
import hm.binkley.labs.State.PENDING
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.HttpStatus.TEMPORARY_REDIRECT
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("GIVEN a mock MVC")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@WebMvcTest
internal class GreetingControllerTest {
    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var repository: TestingGreetingRepository

    @DisplayName("WHEN root URL is called")
    @Nested
    inner class Root {
        @DisplayName("THEN it says 'Hello, world!'")
        @Test
        fun shouldRespondCheerfully() = GET("/").
                andExpect(status().isOk).
                andExpect(content().string("Hello, world!\n"))!!
    }

    @DisplayName("WHEN greet URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenNew() {
            repository.state = null

            GET("/greet/Brian").
                    andExpect(header().string(LOCATION, "/queue/Brian")).
                    andExpect(TEMPORARY_REDIRECT, PENDING)
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is in progress")
    @Nested
    inner class BatchQueued {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenInProgress() {
            repository.state = PENDING

            GET("/greet/Brian").
                    andExpect(header().string(LOCATION, "/queue/Brian")).
                    andExpect(TEMPORARY_REDIRECT, PENDING)
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is ready")
    @Nested
    inner class BatchReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForBatchWhenReady() {
            repository.state = COMPLETE

            GET("/greet/Brian").
                    andExpect(header().string(LOCATION, "/greetings/Brian")).
                    andExpect(SEE_OTHER, COMPLETE)
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

    @DisplayName("WHEN queue URL is called for <name> AND is in progress")
    @Nested
    inner class QueueNotReady {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondForQueueWhenInProgress() {
            repository.state = PENDING

            GET("/queue/Brian").andExpect(OK, PENDING)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is ready")
    @Nested
    inner class QueueReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForQueueWhenReady() {
            repository.state = COMPLETE

            GET("/queue/Brian").
                    andExpect(header().string(LOCATION, "/greetings/Brian")).
                    andExpect(SEE_OTHER, COMPLETE)
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

    @DisplayName("WHEN ready URL is called for <name> AND is in progress")
    @Nested
    inner class ReadyPending {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenInProgress() {
            repository.state = PENDING

            GET("/greetings/Brian").andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is ready")
    @Nested
    inner class Ready {
        @DisplayName("THEN it gives warm greetings")
        @Test
        fun shouldRespondForReadyWhenReady() {
            repository.state = COMPLETE

            GET("/greetings/Brian").
                    andExpect(status().isOk).
                    andExpect(content().json("""
{
  content: "Brian"
}
"""))
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
            GET("/greetings/Brian").andExpect(NOT_FOUND)
        }
    }

    private fun GET(path: String) = mvc.perform(get(path))
    private fun DELETE(path: String) = mvc.perform(delete(path))

    private fun ResultActions.andExpect(status: HttpStatus)
            = andExpect(status().`is`(status.value()))

    private fun ResultActions.andExpect(status: HttpStatus, state: State)
            = andExpect(status().`is`(status.value())).
            andExpect(content().json("""
{
  state: "$state"
}
"""))
}
