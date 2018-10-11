package hm.binkley.labs

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("GIVEN a mock MVC")
@SpringJUnitConfig(Application::class, TestingConfiguration::class)
@WebMvcTest
internal class MainControllerValidationTest(
    @Autowired val mvc: MockMvc
) {
    @DisplayName("WHEN greet URL is called for <name> AND is new")
    @Nested
    inner class BatchNew {
        @DisplayName("AND name is missing")
        @Nested
        inner class BatchNewMissingName {
            @Test
            fun `THEN it complains`() {
                POST("/greetings", "{}")
                    .andExpect(BAD_REQUEST) // TODO: 422
            }
        }

        @DisplayName("AND name is empty")
        @Nested
        inner class BatchEmptyName {
            @Test
            fun `THEN it complains`() {
                greet("")
                    .andExpect(BAD_REQUEST) // TODO: 422
            }
        }

        @DisplayName("AND name is blank")
        @Nested
        inner class BatchBlankName {
            @Test
            fun `THEN it complains`() {
                greet(" ")
                    .andExpect(BAD_REQUEST) // TODO: 422
            }
        }
    }

    private fun POST(path: String, beginGreeting: String) = mvc.perform(
        post(path).contentType(APPLICATION_JSON_UTF8).content(
            beginGreeting
        )
    )

    private fun greet(name: String): ResultActions {
        return POST(
            "/greetings", """
{
    "name": "$name"
}
"""
        )
    }

    private fun ResultActions.andExpect(status: HttpStatus) = andExpect(
        status().`is`(status.value())
    )
}
