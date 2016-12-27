package hm.binkley.labs

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
        fun shouldRespondCheerfully() = mvc.perform(get("/")).
                andExpect(status().isOk).
                andExpect(content().string("Hello, world!\n"))!!
    }

    @DisplayName("WHEN batch URL is called with <name>")
    @Nested
    inner class Batch {
        @DisplayName("THEN it redirects to the queue for <name>")
        @Test
        fun shouldRespondCheerfully() = mvc.perform(get("/batch/Brian")).
                andExpect(status().isTemporaryRedirect).
                andExpect(header().string(LOCATION, "/queue/Brian"))!!
    }

    @DisplayName("WHEN queue URL is called with <name> AND is not ready")
    @Nested
    inner class QueueNotReady {
        @DisplayName("THEN it says to wait further")
        @Test
        fun shouldRespondCheerfully() {
            repository.done = false

            mvc.perform(get("/queue/Brian")).
                    andExpect(status().isOk)!!
        }
    }

    @DisplayName("WHEN queue URL is called with <name> AND is ready")
    @Nested
    inner class QueueReady {
        @DisplayName("THEN it redirects to the finished document for <name>")
        @Test
        fun shouldRespondCheerfully() {
            repository.done = true

            mvc.perform(get("/queue/Brian")).
                    andExpect(status().isSeeOther).
                    andExpect(header().string(LOCATION, "/ready/Brian"))!!
        }
    }

    @DisplayName("WHEN ready URL is called with <name>")
    @Nested
    inner class Ready {
        @DisplayName("THEN it greets <name> warmly")
        @Test
        fun shouldRespondCheerfully() = mvc.perform(get("/ready/Brian")).
                andExpect(status().isOk).
                andExpect(content().json("{\"content\":\"Brian\"}"))!!
    }
}