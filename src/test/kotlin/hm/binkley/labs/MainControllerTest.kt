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
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.SEE_OTHER
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("GIVEN a mock MVC")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@WebMvcTest
internal class MainControllerTest(
        @Autowired val mvc: MockMvc,
        @Autowired val repository: TestingGreetingService) {
    @DisplayName("WHEN greet URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @Test
        fun `THEN it redirects to the queue`() {
            repository.state = null

            greet("Brian")
                    .andExpect(ACCEPTED, "Brian", PENDING, 0)
                    .andRedirectTo("/queue/Brian")
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is in percentage")
    @Nested
    inner class BatchQueued {
        @Test
        fun `THEN it redirects to the queue`() {
            repository.state = PENDING

            greet("Brian")
                    .andExpect(ACCEPTED, "Brian", PENDING, 0)
                    .andRedirectTo("/queue/Brian")
        }
    }

    @DisplayName("WHEN greet URL is called for <name> AND is ready")
    @Nested
    inner class BatchReady {
        @Test
        fun `THEN it redirects to the completed document`() {
            repository.state = COMPLETE

            greet("Brian")
                    .andExpect(SEE_OTHER, "Brian", COMPLETE, 100)
                    .andRedirectTo("/greetings/Brian")
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is new")
    @Nested
    inner class QueueNew {
        @Test
        fun `THEN it says not found`() {
            repository.state = null

            GET("/queue/Brian")
                    .andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is in percentage")
    @Nested
    inner class QueueNotReady {
        @Test
        fun `THEN it says to wait further`() {
            repository.state = PENDING

            GET("/queue/Brian")
                    .andExpect(OK, "Brian", PENDING, 0)
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is ready")
    @Nested
    inner class QueueReady {
        @Test
        fun `THEN it redirects to the completed document`() {
            repository.state = COMPLETE

            GET("/queue/Brian")
                    .andExpect(SEE_OTHER, "Brian", COMPLETE, 100)
                    .andRedirectTo("/greetings/Brian")
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is deleted")
    @Nested
    inner class QueueDelete {
        @Test
        fun `THEN it says not found`() {
            repository.state = PENDING

            DELETE("/queue/Brian")
                    .andExpect(NO_CONTENT)
            GET("/queue/Brian")
                    .andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is new")
    @Nested
    inner class ReadyNew {
        @Test
        fun `THEN it says not found`() {
            repository.state = null

            GET("/greetings/Brian")
                    .andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is in percentage")
    @Nested
    inner class ReadyPending {
        @Test
        fun `THEN it says not found`() {
            repository.state = PENDING

            GET("/greetings/Brian")
                    .andExpect(NOT_FOUND)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is ready")
    @Nested
    inner class Ready {
        @Test
        fun `THEN it gives warm greetings`() {
            repository.state = COMPLETE

            GET("/greetings/Brian")
                    .andExpect(status().isOk)
                    .andExpect(content().json("""
{
  content: "Brian"
}
"""))
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is deleted")
    @Nested
    inner class ReadyDelete {
        @Test
        fun `THEN it says not found`() {
            repository.state = COMPLETE

            DELETE("/greetings/Brian")
                    .andExpect(NO_CONTENT)
            GET("/greetings/Brian")
                    .andExpect(NOT_FOUND)
        }
    }

    private fun POST(path: String, beginGreeting: String) = mvc.perform(
            post(path).contentType(APPLICATION_JSON_UTF8).content(
                    beginGreeting))

    private fun greet(name: String): ResultActions {
        return POST("/greetings", """
{
    "name": "$name"
}
""")
    }

    private fun GET(path: String) = mvc.perform(get(path))
    private fun DELETE(path: String) = mvc.perform(delete(path))

    private fun ResultActions.andExpect(status: HttpStatus) = andExpect(
            status().`is`(status.value()))

    private fun ResultActions.andExpect(
            status: HttpStatus,
            name: String,
            state: State,
            percentage: Int
    ) = andExpect(
            status().`is`(status.value())).andExpect(content().json("""
{
  "name": "$name",
  "state": "$state",
  "percentage": $percentage
}
"""))

    private fun ResultActions.andRedirectTo(location: String): ResultActions {
        andExpect(header().string(LOCATION, location))
        return this
    }
}
