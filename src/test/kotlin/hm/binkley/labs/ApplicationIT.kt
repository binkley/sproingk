package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.junit.jupiter.SpringExtension

@DisplayName("GIVEN a running application on a random port")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
internal open class ApplicationIT : RestITBase() {
    @DisplayName("WHEN root URL is called")
    @Nested
    inner class Root {
        private val path = "/"

        @DisplayName("THEN it says 'Hello, world!'")
        @Test
        fun shouldRespondCheerfully()
                = assertEquals("Hello, world!\n", get(path))
    }
}
