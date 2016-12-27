package hm.binkley.labs

import hm.binkley.labs.TestingConfiguration
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("GIVEN a mock MVC")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@WebMvcTest
internal class GreetingControllerTest {
    @Autowired lateinit var mvc: MockMvc

    @DisplayName("WHEN root URL is called")
    @Nested
    inner class Root {
        private val builder = get("/")

        @DisplayName("THEN it says 'Hello, world!'")
        @Test
        fun shouldRespondCheerfully() = mvc.perform(builder).
                andExpect(status().isOk).
                andExpect(jsonPath("$.content").value("Hello, world!"))!!
    }

    @DisplayName("WHEN name URL is called")
    @Nested
    inner class Name {
        private val builder = get("/Brian")

        @DisplayName("THEN it says 'Hello, <name>!'")
        @Test
        fun shouldRespondCheerfully() = mvc.perform(builder).
                andExpect(status().isOk).
                andExpect(jsonPath("$.content").value("Hello, Brian!"))!!
    }
}
