package hm.binkley.labs

import hm.binkley.labs.TestingGreetingRepository.State.COMPLETE
import hm.binkley.labs.TestingGreetingRepository.State.NONE
import hm.binkley.labs.TestingGreetingRepository.State.PENDING
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
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

    @DisplayName("WHEN batch URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenNew() {
            repository.state = NONE

            GET("/batch/Brian").
                    andExpect(status().isTemporaryRedirect).
                    andExpect(header().string(LOCATION, "/queue/Brian"))!!
        }
    }

    @DisplayName("WHEN batch URL is called for <name> AND is in progress")
    @Nested
    inner class BatchQueued {
        @DisplayName("THEN it redirects to the queue")
        @Test
        fun shouldRedirectForBatchWhenPending() {
            repository.state = PENDING

            GET("/batch/Brian").
                    andExpect(status().isTemporaryRedirect).
                    andExpect(header().string(LOCATION, "/queue/Brian"))!!
        }
    }

    @DisplayName("WHEN batch URL is called for <name> AND is ready")
    @Nested
    inner class BatchReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForBatchWhenComplete() {
            repository.state = COMPLETE

            GET("/batch/Brian").
                    andExpect(status().isSeeOther).
                    andExpect(header().string(LOCATION, "/ready/Brian"))!!
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is new")
    @Nested
    inner class QueueNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForQueueWhenNew() {
            repository.state = NONE

            GET("/queue/Brian").
                    andExpect(status().isNotFound)!!
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is in progress")
    @Nested
    inner class QueueNotReady {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondForQueueWhenPending() {
            repository.state = PENDING

            GET("/queue/Brian").
                    andExpect(status().isOk)!!
        }
    }

    @DisplayName("WHEN queue URL is called for <name> AND is ready")
    @Nested
    inner class QueueReady {
        @DisplayName("THEN it redirects to the completed document")
        @Test
        fun shouldRedirectForQueueWhenComplete() {
            repository.state = COMPLETE

            GET("/queue/Brian").
                    andExpect(status().isSeeOther).
                    andExpect(header().string(LOCATION, "/ready/Brian"))
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is new")
    @Nested
    inner class ReadyNew {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenNew() {
            repository.state = NONE

            GET("/ready/Brian").
                    andExpect(status().isNotFound)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is in progress")
    @Nested
    inner class ReadyPending {
        @DisplayName("THEN it says not found")
        @Test
        fun shouldComplainForReadyWhenPending() {
            repository.state = PENDING

            GET("/ready/Brian").
                    andExpect(status().isNotFound)
        }
    }

    @DisplayName("WHEN ready URL is called for <name> AND is ready")
    @Nested
    inner class Ready {
        @DisplayName("THEN it gives warm greetings")
        @Test
        fun shouldRespondForReadyWhenComplete() {
            repository.state = COMPLETE

            GET("/ready/Brian").
                    andExpect(status().isOk).
                    andExpect(content().json("""
{
  "content": "Brian"
}
"""))
        }
    }

    private fun GET(path: String) = mvc.perform(get(path))
}
