package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.junit.jupiter.SpringExtension

@DisplayName("GIVEN a running application on a random port")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal class GreetingControllerIT {
    @Autowired lateinit private var restTemplate: TestRestTemplate

    @DisplayName("WHEN root URL is called")
    @Nested
    inner class Root {
        @DisplayName("THEN it says 'Hello, world!'")
        @Test
        fun shouldRespondCheerfully() {
            val entity = entity("/")
            assertEquals(OK, entity.statusCode)
            assertEquals("Hello, world!", entity.body.content)
        }
    }

    @DisplayName("WHEN name URL is called")
    @Nested
    inner class Name {
        @DisplayName("THEN it says 'Hello, <name>!'")
        @Test
        fun shouldRespondCheerfully() {
            val entity = entity("/Brian")
            assertEquals(OK, entity.statusCode)
            assertEquals("Hello, Brian!", entity.body.content)
        }
    }

    private fun entity(path: String) = restTemplate.getForEntity(path,
            Greeting::class.java)
}
