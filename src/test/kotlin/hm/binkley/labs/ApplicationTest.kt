package hm.binkley.labs

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("GIVEN a mock MVC")
@ExtendWith(SpringExtension::class)
@WebMvcTest
internal open class ApplicationTest {
    @Autowired lateinit var mvc: MockMvc

    @DisplayName("WHEN root URL is called\nTHEN it says 'Hello, world!'")
    @Test
    fun shouldRespondCheerfully() = mvc.perform(get("/")).
            andExpect(status().isOk).
            andExpect(content().string("Hello, world!\n"))!!
}
