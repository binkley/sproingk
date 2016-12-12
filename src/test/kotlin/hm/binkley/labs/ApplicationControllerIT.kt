package hm.binkley.labs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT,
        classes = arrayOf(Application::class),
        properties = arrayOf("spring.main.banner-mode=OFF"))
internal open class ApplicationControllerIT {
    @Autowired lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldRespondCheerfully() {
        val response = restTemplate.getForObject("/", String::class.java)

        assertEquals("Hello, world!\n", response)
    }
}
